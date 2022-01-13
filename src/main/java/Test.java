import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.Validate;
import sun.misc.IOUtils;

import org.json.JSONObject;

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
            HashMap<String, String> answ = null;
            problem.addStudent("1");
            //String text = problem.getFullText();
            //System.out.println(text);

            String method = t.getRequestMethod();

            //System.out.println(t.getRequestBody().toString());
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

                //System.out.println("direction: " + param_value.get("direction"));

                String interaction = param_value.get("interaction");
                if (interaction!=null && interaction.equals("1")) //Выбор элементов данных
                {
                    String leftBorder = param_value.get("leftBorder");
                    String rightBorder = param_value.get("rightBorder");
                    answ = problem.chooseDataElementBorders("1", leftBorder, rightBorder);
                    response = answ.get("correct");
                }

                if (interaction!=null && interaction.equals("2"))
                {
                    System.out.println("Interaction 2");
                    String elementName = param_value.get("elementName");
                    String elementDirection = param_value.get("direction");
                    answ = problem.chooseElementDirection("1", elementName, elementDirection);
                }

                if (interaction!=null && interaction.equals("3"))
                {
                    String elementName = param_value.get("elementName");
                    String presentationName = param_value.get("presentationName");
                    answ = problem.choosePresentationForDataElement("1", elementName, presentationName);
                }

                if (interaction!=null && interaction.equals("4"))
                {
                    String elementName = param_value.get("elementName");
                    String componentName = param_value.get("componentName");
                    String transferMethod = param_value.get("transferMethod");
                    answ = problem.chooseParameterOrReturnValue("1", elementName, componentName, transferMethod);
                }

                if (interaction!=null && interaction.equals("5"))
                {
                    String paramName = param_value.get("parameter");
                    String typeName = param_value.get("type");
                    answ = problem.chooseParameterType("1", paramName, typeName);
                }

                if (param_value.containsKey("fullText"))
                {
                    answ = new HashMap<String, String>();
                    response = problem.getFullText();
                    answ.put("fulltext", response);
                }

                if (param_value.containsKey("componentList"))
                {
                    answ = problem.getStudentComponents("1");
                }
                if (param_value.containsKey("paramList"))
                {
                    answ = new HashMap<String, String>();
                    ArrayList<String> studentParams = problem.getStudentParameters("1");
                    for (String p:
                         studentParams) {
                        answ.put(p,"");
                    }
                }
            }

            OutputStream os = t.getResponseBody();
            OutputStreamWriter ow = new OutputStreamWriter(os, "Cp1251");
            String respStr = new String(response.getBytes("ISO-8859-1"), "Cp1251");
            JSONObject jo = new JSONObject();

            List<String> keys = new ArrayList<String>(answ.keySet());
            for(int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = answ.get(key);
                value = StringEscapeUtils.escapeJava(value);
                jo.put(key,value);
            }

            //jo.put("text", StringEscapeUtils.escapeJava(response));
            String jsonStr = jo.toString();


            t.sendResponseHeaders(200, jsonStr.length());
            //System.out.println(jsonStr);
            ow.write(jsonStr);
            ow.flush();
            ow.close();
            //os.write(response.getBytes());
            //os.close();
        }

        private String requestToJena(HashMap<String, String> param_value)
        {
            if (param_value.get("dataelement").equals("dayscount") && !param_value.get("direction").equals("output"))
                return "Error";
            return "Correct";
        }

    }
}