import org.apache.jena.base.Sys;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL2;

import javax.jws.WebParam;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import static org.apache.jena.ontology.OntModelSpec.OWL_MEM_MICRO_RULE_INF;

public class Problem {

    protected Model model = null;
    protected OntModel inf = null;
    Reasoner reasoner = null;
    InfModel infModel = null;

    protected static String BASE_URL = "http://www.semanticweb.org/problem-ontology";
    protected static String PROBLEM_ONTOLOGY_FILE = "ProblemOntology.owl";
    protected static String SESSION_ONTOLOGY_FILE = "SessionOntology.owl";
    protected static String DATA_DIRECTION_RULES = "rules/data_direction.rules";

    protected static String DATA_TRANSFER_METHOD_RETURN = "return";
    protected static String DATA_TRANSFER_METHOD_READ_ONLY = "read-only";
    protected static String DATA_TRANSFER_METHOD_WRITE_ONLY = "write-only";
    protected static String DATA_TRANSFER_METHOD_READ_WRITE = "read-write";

    protected Individual daysCount = null;
    protected Individual firstDate_DataElement = null;
    protected Individual secondDate_DataElement = null;

    protected Individual problem = null;

    protected Individual date_Entity = null;


    public Problem()
    {
        createModel(DATA_DIRECTION_RULES);
        calcModel();
    }

    protected Model readModel(String modelFile)
    {
        // create an empty model
        Model model = ModelFactory.createDefaultModel();
        String inputFileName=modelFile;
        // use the RDFDataMgr to find the input file
        InputStream in = RDFDataMgr.open( inputFileName );
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileName + " not found");
        }

        // read the RDF/XML file
        model.read(in, null);
        return model;
    }

    protected void createModel(String rulesFile)
    {
        Model problemModel = readModel(PROBLEM_ONTOLOGY_FILE);
        Model sessionModel = readModel(SESSION_ONTOLOGY_FILE);
        model = ModelFactory.createUnion(problemModel, sessionModel);


        InputStream stream = getClass().getClassLoader().getResourceAsStream(rulesFile);
        String rules = readStream( stream);
        //System.out.print(rules);
        reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
        reasoner.setDerivationLogging(true);
        reasoner.setDerivationLogging(true);
        reasoner.bindSchema(model);


        inf = ModelFactory.createOntologyModel( OWL_MEM_MICRO_RULE_INF, model);

//        //Фраза "Количество дней"
//        Individual daysCount_phrase = inf.createIndividual(BASE_URL+"#Phrase_DaysCount", inf.getOntClass(BASE_URL+"#Phrase"));
//        daysCount_phrase.addRDFType(OWL2.NamedIndividual);
//        Property textProperty=inf.createDatatypeProperty(BASE_URL+"#text");
//        daysCount_phrase.addProperty(textProperty, "Количество дней");
//
//        daysCount = inf.createIndividual(BASE_URL + "#DataElement_DaysCount", inf.createOntResource(BASE_URL+"#DataElement"));
//        daysCount.addRDFType(OWL2.NamedIndividual);
//        daysCount.addProperty(inf.createDatatypeProperty(BASE_URL+"#name"), "dayscount");
//        daysCount.addProperty(inf.createDatatypeProperty(BASE_URL+"#mission"), "Количество дней между двумя датами");
//
//        daysCount_phrase.addProperty(inf.createObjectProperty(BASE_URL+"#describe"), daysCount);
//
//        //Фраза "между"
//        Individual between_phrase = inf.createIndividual(BASE_URL+"#Phrase_Between", inf.getOntClass(BASE_URL+"#Phrase"));
//        between_phrase.addRDFType(OWL2.NamedIndividual);
//        between_phrase.addProperty(inf.createDatatypeProperty(BASE_URL+"#text"), "между");
//
//
//        //Фраза "первой датой"
//        Individual firstDate_phrase = inf.createIndividual(BASE_URL+"#Phrase_FirstDate", inf.getOntClass(BASE_URL+"#Phrase"));
//        firstDate_phrase.addRDFType(OWL2.NamedIndividual);
//        firstDate_phrase.addProperty(inf.createDatatypeProperty(BASE_URL+"#text"), "датой рождения человека");
//
//        firstDate_DataElement = inf.createIndividual(BASE_URL + "#DataElement_FirstDate", inf.createOntResource(BASE_URL+"#DataElement"));
//        firstDate_DataElement.addRDFType(OWL2.NamedIndividual);
//        firstDate_DataElement.addProperty(inf.createDatatypeProperty(BASE_URL+"#name"), "дата рождения");
//        firstDate_DataElement.addProperty(inf.createDatatypeProperty(BASE_URL+"#mission"), "дата рождения");
//
//        firstDate_phrase.addProperty(inf.createObjectProperty(BASE_URL+"#describe"), firstDate_DataElement);
//
//        //Фраза "и"
//        Individual and_phrase = inf.createIndividual(BASE_URL+"#Phrase_And", inf.getOntClass(BASE_URL+"#Phrase"));
//        and_phrase.addRDFType(OWL2.NamedIndividual);
//        and_phrase.addProperty(inf.createDatatypeProperty(BASE_URL+"#text"), "и");
//
//        //Фраза "второй датой"
//        Individual secondDate_phrase = inf.createIndividual(BASE_URL+"#Phrase_SecondDate", inf.getOntClass(BASE_URL+"#Phrase"));
//        secondDate_phrase.addRDFType(OWL2.NamedIndividual);
//        secondDate_phrase.addProperty(inf.createDatatypeProperty(BASE_URL+"#text"), "датой первого дня в школе");
//
//        secondDate_DataElement = inf.createIndividual(BASE_URL + "#DataElement_SecondDate", inf.createOntResource(BASE_URL+"#DataElement"));
//        secondDate_DataElement.addRDFType(OWL2.NamedIndividual);
//        secondDate_DataElement.addProperty(inf.createDatatypeProperty(BASE_URL+"#name"), "дата первого дня в школе");
//        secondDate_DataElement.addProperty(inf.createDatatypeProperty(BASE_URL+"#mission"), "дата первого дня в школе");
//
//        //Соединяем фразы
//        daysCount_phrase.addProperty(inf.createObjectProperty(BASE_URL+"#next"), between_phrase);
//        between_phrase.addProperty(inf.createObjectProperty(BASE_URL+"#next"), firstDate_phrase);
//        firstDate_phrase.addProperty(inf.createObjectProperty(BASE_URL+"#next"), and_phrase);
//        and_phrase.addProperty(inf.createObjectProperty(BASE_URL+"#next"), secondDate_phrase);
//
//
//
//
//        //
//        //Тип предметной области "Дата"
//        date_Entity = inf.createIndividual(BASE_URL + "#Entity_Date", inf.createOntResource(BASE_URL + "#Entity"));
//        date_Entity.addRDFType(OWL2.NamedIndividual);
//        date_Entity.addProperty(inf.createDatatypeProperty(BASE_URL+"#name"), "дата");
//
//
//        //Тип предметной области "Период"
//        Individual daysPeriod_Scalar = inf.createIndividual(BASE_URL + "#Scalar_DaysPeriod", inf.createOntResource(BASE_URL + "#Scalar"));
//        daysPeriod_Scalar.addRDFType(OWL2.NamedIndividual);
//        daysPeriod_Scalar.addProperty(inf.createDatatypeProperty(BASE_URL+"#name"), "интервал в днях");
//
//        //Тип предметной области "Номер дня"
//        Individual dayNumber_Scalar = inf.createIndividual(BASE_URL + "#Scalar_DayNumber", inf.createOntResource(BASE_URL + "#Scalar"));
//        dayNumber_Scalar.addRDFType(OWL2.NamedIndividual);
//        dayNumber_Scalar.addProperty(inf.createDatatypeProperty(BASE_URL+"#name"), "номер дня месяца");
//
//        //Тип предметной области "Номер месяца"
//        Individual monthNumber_Scalar = inf.createIndividual(BASE_URL + "#Scalar_MonthNumber", inf.createOntResource(BASE_URL + "#Scalar"));
//        monthNumber_Scalar.addRDFType(OWL2.NamedIndividual);
//        monthNumber_Scalar.addProperty(inf.createDatatypeProperty(BASE_URL+"#name"), "номер месяца в году");
//
//        //Тип предметной области "Номер года"
//        Individual yearNumber_Scalar = inf.createIndividual(BASE_URL + "#Scalar_YearNumber", inf.createOntResource(BASE_URL + "#Scalar"));
//        yearNumber_Scalar.addRDFType(OWL2.NamedIndividual);
//        yearNumber_Scalar.addProperty(inf.createDatatypeProperty(BASE_URL+"#name"), "номер года");
//
//        //Компоненты даты
//        date_Entity.addProperty(inf.createObjectProperty(BASE_URL+"#hasComponent"), dayNumber_Scalar );
//        date_Entity.addProperty(inf.createObjectProperty(BASE_URL+"#hasComponent"), monthNumber_Scalar);
//        date_Entity.addProperty(inf.createObjectProperty(BASE_URL+"#hasComponent"), yearNumber_Scalar);
//
//        //Типы элементов данных
//        firstDate_DataElement.addProperty(inf.createObjectProperty(BASE_URL+"#hasDomainType"), date_Entity);
//        secondDate_DataElement.addProperty(inf.createObjectProperty(BASE_URL+"#hasDomainType"), date_Entity);
//        daysCount.addProperty(inf.createObjectProperty(BASE_URL+"#hasDomainType"), daysPeriod_Scalar);
//
//
//
//        //Сущность самой задачи
//        //problem = inf.createIndividual(BASE_URL + "#Problem_Individual", inf.getOntClass(BASE_URL + "#Problem"));
//       // problem.addRDFType(OWL2.NamedIndividual);
//        problem =  inf.createIndividual(inf.createResource());
//        problem.addOntClass(inf.getOntClass(BASE_URL+"#Problem"));
//        problem.addProperty(inf.createObjectProperty(BASE_URL+"#hasFormulation"), daysCount_phrase);
//        problem.addProperty(inf.createObjectProperty(BASE_URL+"#hasInputData"), firstDate_DataElement);
//        problem.addProperty(inf.createObjectProperty(BASE_URL+"#hasInputData"), secondDate_DataElement);
//        problem.addProperty(inf.createObjectProperty(BASE_URL+"#hasOutputData"), daysCount);
//
//        //Первая фраза задачи
//        problem.addProperty(inf.createObjectProperty(BASE_URL+"#hasFormulation"), daysCount_phrase);
    }

    protected static String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder(512);
        try {
            Reader r = new InputStreamReader(is, "UTF-8");
            int c = 0;
            while ((c = r.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    protected void calcModel()
    {
        infModel = ModelFactory.createInfModel(reasoner, (infModel == null) ? inf : infModel);
    }

    public Resource addStudent(String id)
    {


        String queryString = "PREFIX so: <http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#> " +
                "SELECT ?student " +
                " WHERE { "+
                "?student <http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#hasID> \"" + id +"\"" +
                " }";

        //String queryString = "SELECT ?problem ?parameter WHERE {?problem <http://www.semanticweb.org/problem-ontology#hasParameter> ?parameter }";
        Query query = QueryFactory.create(queryString);
        //InfModel infModel = ModelFactory.createInfModel(reasoner, inf);
        QueryExecution qExec = QueryExecutionFactory.create(query, inf);
        ResultSet rs = qExec.execSelect();
        if (! (rs.hasNext()) ) {

            Individual student = inf.createIndividual(inf.createResource());
            student.addOntClass(inf.getOntClass("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#Student"));
            student.addProperty(inf.createDatatypeProperty("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#hasID"), id);
            student.addProperty(inf.createDatatypeProperty("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#notFoundElementsCount"), inf.createTypedLiteral(3));
            student.addProperty(inf.createDatatypeProperty("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#currentInteraction"), inf.createTypedLiteral(0));
        }

        while (rs.hasNext())
        {
            Resource student = rs.next().get("?student").asResource();
            return student;
        }
        return null;
    }

    public String getFullText()
    {
        String queryString =  "SELECT ?text " +
                " WHERE { "+
                "?problem a <http://www.semanticweb.org/problem-ontology#Problem> " +
                "{ " +
                "    ?problem  <http://www.semanticweb.org/problem-ontology#hasFullText>  ?text " +
                " } " +
                "}";

        Query query = QueryFactory.create(queryString);
        InfModel infModel = ModelFactory.createInfModel(reasoner, inf);
        //inf.write(System.out);
        QueryExecution qExec = QueryExecutionFactory.create(query, infModel);
        ResultSet rs = qExec.execSelect();
        //System.out.println(rs.getRowNumber());

        if ( rs.hasNext())
        {
            System.out.println("+");
            QuerySolution qs = rs.next();
            System.out.println(qs.get("?text").toString());
            return qs.get("?text").toString();
        }
        return rs.toString();
        //return "";
    }

    public boolean studentHasChosenElement(String studentID, String elementName)
    {
        String queryString = "PREFIX so: <http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#>" +
                " SELECT (count(?element) as ?cnt)  WHERE {" +
                " ?student a so:Student ." +
                " ?student so:hasID \"" + studentID + "\" . " +
                " ?student so:foundElement ?element ." +
                " ?element <http://www.semanticweb.org/problem-ontology#name>  \"" + elementName + "\" ." +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qExec = QueryExecutionFactory.create(query, infModel);
        ResultSet rs = qExec.execSelect();
        if (rs.hasNext())
        {
            QuerySolution qs = rs.next();
            int s = qs.get("?cnt").asLiteral().getInt();
            return (s>0);
        }
        return false;
    }

    public HashMap<String, String> chooseDataElementBorders(String studentID, String leftBorderNumLexem, String rightBorderNumLexem)
    {

        HashMap<String, String> result = new HashMap<String,String>();

        Resource student = addStudent(studentID);
        Individual answer = inf.createIndividual(inf.createResource());
        answer.addOntClass(inf.getOntClass("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#Answer"));
        answer.addOntClass(inf.getOntClass("http://www.semanticweb.org/problem-ontology#Phrase"));
        answer.addProperty(inf.getDatatypeProperty("http://www.semanticweb.org/problem-ontology#hasLeftBorder"),ResourceFactory.createTypedLiteral(Integer.parseInt(leftBorderNumLexem)));
        answer.addProperty(inf.getDatatypeProperty("http://www.semanticweb.org/problem-ontology#hasRightBorder"), ResourceFactory.createTypedLiteral(Integer.parseInt(rightBorderNumLexem)));

        student.addProperty(inf.getObjectProperty("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#hasAnswer"), answer);
        infModel = ModelFactory.createInfModel(reasoner, inf);
        //calcModel();
        //infModel.write(System.out);

        String queryString = "PREFIX so: <http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#> " +
                "PREFIX po: <http://www.semanticweb.org/problem-ontology#> " +
                "SELECT ?correct ?message WHERE " +
                "{" +
                "?student a so:Student ." +
                " ?student so:hasID \""+studentID+"\" . " +
                "?student so:hasAnswer ?answ. " +
                "?answ po:hasLeftBorder "+leftBorderNumLexem+" . " +
                "?answ po:hasRightBorder "+rightBorderNumLexem+" . " +
                "?answ so:isCorrectAnswer ?correct . " +
                "?student so:hasMessage ?message . " +
                "} ";
        Query query = QueryFactory.create(queryString);
        QueryExecution qExec = QueryExecutionFactory.create(query, infModel);
        ResultSet rs = qExec.execSelect();
        if (rs.hasNext())
        {
            QuerySolution qs = rs.next();
            int s = qs.get("?correct").asLiteral().getInt();
            result.put("correct", (s==1) ? "true" : "false");
            result.put("message", qs.get("?message").asLiteral().getString());
            if (s==1)
            {
                String queryStringElementName = "PREFIX so: <http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#> " +
                        "PREFIX po: <http://www.semanticweb.org/problem-ontology#> " +
                        "SELECT ?mission ?element ?notfound ?interaction WHERE " +
                        "{ " +
                        "?phrase a po:Phrase . " +
                        "?phrase po:hasLeftBorder " + leftBorderNumLexem + " ." +
                        "?phrase po:hasRightBorder " + rightBorderNumLexem + " ." +
                        "?phrase po:describe ?element . " +
                        "?element po:mission ?mission . " +
                        "?student a so:Student . " +
                        " ?student so:hasID \""+studentID+"\" . " +
                        "?student so:notFoundElementsCount ?notfound . " +
                        "?student so:currentInteraction ?interaction . " +
                        "} ";
                Query queryElementName = QueryFactory.create(queryStringElementName);
                QueryExecution qExecElementName = QueryExecutionFactory.create(queryElementName, infModel);
                ResultSet rsElementName = qExecElementName.execSelect();
                if (rsElementName.hasNext())
                {
                    QuerySolution qsElementName  = rsElementName.next();
                    Literal nameLit = qsElementName.get("?mission").asLiteral();
                    String name = nameLit.getString();
                    result.put("mission", name);
                    Resource element = qsElementName.get("?element").asResource();
                    student.addProperty(inf.getObjectProperty("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#foundElement"), element);
                    int notFound = qsElementName.get("?notfound").asLiteral().getInt();
                    student.getProperty(inf.getDatatypeProperty("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#notFoundElementsCount")).changeLiteralObject(notFound);
                    int interaction_num = qsElementName.get("?interaction").asLiteral().getInt();
                    student.getProperty(inf.getDatatypeProperty("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#currentInteraction")).changeLiteralObject(interaction_num);
                    result.put("interaction", String.valueOf(interaction_num));
                }
            }
        }

        return result;
    }

    public HashMap<String, String> chooseElementDirection(String studentID, String elementName, String direction)
    {
        HashMap<String, String> answ = new HashMap<String, String>();
        Resource student = addStudent(studentID);
        Individual answer = inf.createIndividual(inf.createResource());
        answer.addOntClass(inf.getOntClass("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#Answer"));
        answer.addProperty(inf.getDatatypeProperty("http://www.semanticweb.org/problem-ontology#hasDirection"), direction);
        student.addProperty(inf.getObjectProperty("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#hasAnswer"), answer);
        answer.addProperty(inf.getDatatypeProperty("http://www.semanticweb.org/problem-ontology#hasElementName"), elementName);
        infModel = ModelFactory.createInfModel(reasoner, inf);
        String queryString="PREFIX so: <http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#> " +
                "PREFIX po: <http://www.semanticweb.org/problem-ontology#> " +
                "SELECT ?correct ?message WHERE " +
                "{" +
                "?student a so:Student . " +
                " ?student so:hasID \""+studentID+"\" . " +
                "?student so:hasAnswer ?answ. " +
                "?answ po:hasElementName \"" + elementName + "\" ." +
                "?answ po:hasDirection \"" + direction + "\" . " +
                "?answ so:isCorrectAnswer ?correct . " +
                "?student so:hasMessage ?message . " +
                "}";


        Query query = QueryFactory.create(queryString);
        QueryExecution qExec = QueryExecutionFactory.create(query, infModel);
        ResultSet rs = qExec.execSelect();
        if (rs.hasNext()) {
            QuerySolution qs = rs.next();
            int s = qs.get("?correct").asLiteral().getInt();
            answ.put("correct", (s == 1) ? "true" : "false");
            answer.addProperty(inf.getDatatypeProperty("http://www.semanticweb.org/dns/ontologies/2021/10/session-ontology#isCorrectAnswer"),inf.createTypedLiteral(s));
            answ.put("message", qs.get("?message").asLiteral().getString());
        }
        infModel.toString();
        //answ.put("correct","true");
        return answ;
    }

}
