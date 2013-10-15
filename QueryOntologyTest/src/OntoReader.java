import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasitha
 * Date: 10/13/13
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class OntoReader {


    public static OWLOntologyManager manager;
    public static OWLReasoner reasoner;

    public List<String> read(){

        manager = OWLManager.createOWLOntologyManager();

        try{

            File file = new File("Ontology/SelchiLocationOntology_New.owl");
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            System.out.println("Loaded ontology: " + ontology);

            OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();

            ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();

            OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);

            reasoner = reasonerFactory.createReasoner(ontology,config);

            // Ask the reasoner to do all the necessary work now
            reasoner.precomputeInferences();

            // We can determine if the ontology is actually consistent (in this
            // case, it should be).
            boolean consistent = reasoner.isConsistent();
            System.out.println("Consistent: " + consistent);
            System.out.println("\n");


            OWLDataFactory dataFactory = manager.getOWLDataFactory();
            // The IRIs used here are taken from the OWL 2 Primer
            String base = "https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#";
            PrefixManager pm = new DefaultPrefixManager(base);

            //Required Classes
            OWLClass place = dataFactory.getOWLClass(":Place", pm);

            //Accessing Place class hierarchy
            NodeSet<OWLClass> placeTyps = reasoner.getSubClasses(place,false);
            List<String> toReturn = new ArrayList<String>();

            for(Node<OWLClass> clasi:placeTyps){

                String toAdd = "";

                String[] toAddSplit = clasi.getEntities().toString().replace("[<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#","").replace(">]","")
                        .split("(?<!^)(?=[A-Z])");

                for(int i=0;i<toAddSplit.length;i++){

                    toAdd +=" "+toAddSplit[i].toLowerCase();

                }

                toAdd = toAdd.trim();


                toReturn.add(toAdd);
            }

            return toReturn;


        }catch(OWLOntologyCreationException e){
            e.printStackTrace();
            return null;
        }


    }


    public void test(){

        manager = OWLManager.createOWLOntologyManager();


        try {

            File file = new File("Ontology/SelchiLocationOntology_New.owl");
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            System.out.println("Loaded ontology: " + ontology);

            OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();

            ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();

            OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);

            reasoner = reasonerFactory.createReasoner(ontology,config);

            // Ask the reasoner to do all the necessary work now
            reasoner.precomputeInferences();

            // We can determine if the ontology is actually consistent (in this
            // case, it should be).
            boolean consistent = reasoner.isConsistent();
            System.out.println("Consistent: " + consistent);
            System.out.println("\n");


            OWLDataFactory dataFactory = manager.getOWLDataFactory();
            // The IRIs used here are taken from the OWL 2 Primer
            String base = "https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#";
            PrefixManager pm = new DefaultPrefixManager(base);


            OWLClass continent = dataFactory.getOWLClass(":Continent",pm);




        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }




    }



}
