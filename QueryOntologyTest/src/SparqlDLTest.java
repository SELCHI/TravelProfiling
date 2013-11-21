import de.derivo.sparqldlapi.Query;
import de.derivo.sparqldlapi.QueryEngine;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;


import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: lasitha
 * Date: 10/16/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SparqlDLTest {

    private static QueryEngine engine;
    public static OWLOntologyManager manager;
    public static OWLReasoner reasoner;

    public void doIT(){


        manager = OWLManager.createOWLOntologyManager();

        try{

            File file = new File("Ontology/SelchiLocationOntology_New.owl");
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            System.out.println("Loaded ontology: " + ontology);

            StructuralReasonerFactory factory = new StructuralReasonerFactory();
            reasoner = factory.createReasoner(ontology);

            reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS,InferenceType.OBJECT_PROPERTY_ASSERTIONS);

            engine = QueryEngine.create(manager,reasoner,true);

            processQuery(
                    "PREFIX entity: <https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                            "SELECT DISTINCT ?v WHERE {\n" +
                            "PropertyValue(entity:Asia_Continent, entity:hasCountry, ?i),\n" +
                            "PropertyValue(?i, entity:hasRegion, ?k),\n"+
                            "PropertyValue(?k, entity:hasQualifier, ?v),\n"+
                            "Type(?v, entity:Adventure)"+
                            "}"
            );



        }catch(OWLOntologyCreationException e){
            e.printStackTrace();
        }catch(Exception e){

        }


    }


    public static void processQuery(String q)
    {
        try {
            long startTime = System.currentTimeMillis();

            // Create a query object from it's string representation
            Query query = Query.create(q);

            System.out.println("Excecute the query:");
            System.out.println(q);
            System.out.println("-------------------------------------------------");

            // Execute the query and generate the result set
            QueryResult result = engine.execute(query);

            if(query.isAsk()) {
                System.out.print("Result: ");
                if(result.ask()) {
                    System.out.println("yes");
                }
                else {
                    System.out.println("no");
                }
            }
            else {
                if(!result.ask()) {
                    System.out.println("Query has no solution.\n");
                }
                else {
                    System.out.println("Results:");
                    System.out.print(result);
                    System.out.println("-------------------------------------------------");
                    System.out.println("Size of result set: " + result.size());
                }
            }

            System.out.println("-------------------------------------------------");
            System.out.println("Finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + "s\n");
        }
        catch(QueryParserException e) {
            System.out.println("Query parser error: " + e);
        }
        catch(QueryEngineException e) {
            System.out.println("Query engine error: " + e);
        }
    }

}
