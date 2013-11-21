package Onto;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasitha
 * Date: 10/12/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */


public class OntoWriter {

    public static OWLOntologyManager manager;
    public static OWLReasoner reasoner;


    public void write(){

        manager = OWLManager.createOWLOntologyManager();

        try {
            File file = new File("Ontology/SelchiLocationOntology_New.owl");
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            System.out.println("Loaded ontology: " + ontology);

            IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
            System.out.println("    from: " + documentIRI);

            OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();

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
            OWLClass country = dataFactory.getOWLClass(":Country", pm);
            OWLClass continent = dataFactory.getOWLClass(":Continent",pm);
            OWLClass region = dataFactory.getOWLClass(":Region",pm);

            List<String[]> data = fileRead();

            //Get the continent
            OWLIndividual asia_continent = dataFactory.getOWLNamedIndividual(":Central_Africa",pm);
            //Properties
            OWLObjectProperty hasCountry = dataFactory.getOWLObjectProperty(":hasCountry",pm);
            OWLObjectProperty hasRegion = dataFactory.getOWLObjectProperty(":hasRegion",pm);

            for(int i=0;i<data.size();i++){

                String[] conCity = data.get(i);
                OWLNamedIndividual countryToAdd = dataFactory.getOWLNamedIndividual(":"+conCity[0].replace(" ","_"),pm);
                OWLClassAssertionAxiom classAssertionCnty = dataFactory.getOWLClassAssertionAxiom(country,countryToAdd);
                manager.addAxiom(ontology,classAssertionCnty);

                OWLObjectPropertyAssertionAxiom opassertion = dataFactory.getOWLObjectPropertyAssertionAxiom(hasCountry,asia_continent,countryToAdd);
                manager.addAxiom(ontology,opassertion);

                String[] cities = conCity[1].split(",");

                for(int j=0;j<cities.length;j++){

                    if(cities[j].equals("—"))
                        continue;
                    OWLNamedIndividual cityToAdd = dataFactory.getOWLNamedIndividual(":"+cities[j].trim().replace(" ","_"),pm);
                    OWLClassAssertionAxiom  classAssertionRgn = dataFactory.getOWLClassAssertionAxiom(region,cityToAdd);
                    manager.addAxiom(ontology,classAssertionRgn);

                    OWLObjectPropertyAssertionAxiom opAssHR = dataFactory.getOWLObjectPropertyAssertionAxiom(hasRegion,countryToAdd,cityToAdd);
                    manager.addAxiom(ontology,opAssHR);
                    manager.saveOntology(ontology);
                }

            }

        } catch (OWLOntologyCreationException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (OWLOntologyStorageException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    public List<String[]> fileRead(){

        List<String[]> countryCity = null;

        BufferedReader br = null;

        try {

            br = new BufferedReader(new FileReader("res/CentralAfrica.txt"));
            String currentStr = null;

            countryCity = new ArrayList<String[]>();


            while((currentStr=br.readLine())!=null){

                String[] splitta = currentStr.split("\\*");
                if(splitta.length !=2)
                    break;
                splitta[0] = splitta[0].trim();
                splitta[1] = splitta[1].trim();
                countryCity.add(splitta);
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally {

                try {
                    if(br!=null)
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
        }

        return countryCity;
    }





}
