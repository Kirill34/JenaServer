import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sun.misc.IOUtils;

public class Test {

    private static Problem problem = new Problem();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static String createResponse(String requestMethod) {
        String resp = "<svg viewbox='-10 -10 20 20' height='100vh'><text text-" +
                "anchor='middle' stroke='red' stroke-width='0.05' font-size='4'>%s</text></svg>";
        if ("GET".equals(requestMethod))
            return String.format(resp, "GET");
        if ("POST".equals(requestMethod))
            return String.format(resp, "POST");
        return "unknown method";
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            problem.addStudent("1");
            String text = problem.getFullText();
            System.out.println(text);

            String method = t.getRequestMethod();
            //System.out.println(t.getRequestHeaders());


            //String query = t.getRequestURI().getQuery();
            //System.out.println(query);


            System.out.println(t.getRequestBody().toString());
            byte [] b = new byte[1000];
            InputStream is = t.getRequestBody();

            int ch;
            StringBuilder sb = new StringBuilder();
            while((ch = is.read()) != -1)
                sb.append((char)ch);
            String inputString = sb.toString();
            String response = "This is the response";
            if (sb.length()>0) {

                HashMap<String, String> param_value = new HashMap<String, String>();
                String[] params = inputString.split("&");
                for (String param : params) {
                    String[] key_value = param.split("=");
                    param_value.put(key_value[0], key_value[1]);
                }

                System.out.println("direction: " + param_value.get("direction"));

                String interaction = param_value.get("interaction");
                if (interaction.equals("1")) //Выбор элементов данных
                {
                    String leftBorder = param_value.get("leftBorder");
                    String rightBorder = param_value.get("rightBorder");
                    problem.chooseDataElementBorders("1", leftBorder, rightBorder);
                }


                if (method.equals("POST"))
                    response = requestToJena(param_value);
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String requestToJena(HashMap<String, String> param_value)
        {
            if (param_value.get("dataelement").equals("dayscount") && !param_value.get("direction").equals("output"))
                return "Error";
            return "Correct";
        }

    }
}