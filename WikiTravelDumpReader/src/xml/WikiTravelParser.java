package xml; /**
 * Created with IntelliJ IDEA.
 * User: lasitha
 * Date: 10/11/13
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */

import Onto.OntoReader;
import data.*;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class WikiTravelParser {

    static final String PAGE = "page";
    static final String TITLE = "title";
    static final String ID = "id";
    static final String REVISION = "revision";
    static final String TEXT = "text";

    List<String> countryList;
    List<String> placeTypesList;
    List<String> qualifierTypeList;

    OWLOntologyManager manager;
    OWLReasoner reasoner;
    OWLDataFactory dataFactory;
    PrefixManager pm;
    OWLOntology ontology;

    OWLClass region;

    OWLObjectProperty hasRegion;

    OWLObjectProperty hasPlace;

    OWLObjectProperty hasQualifier;

    public WikiTravelParser(){

        OntoReader ontoReader = new OntoReader();
        countryList = ontoReader.readIndividuals();
        placeTypesList = ontoReader.readSubClasses("Place");
        qualifierTypeList = ontoReader.readSubClasses("Qualifier");

        manager = OWLManager.createOWLOntologyManager();
        try{
            File file = new File("Ontology/SelchiLocationOntology_New.owl");
            ontology = manager.loadOntologyFromOntologyDocument(file);
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

            dataFactory = manager.getOWLDataFactory();
            // The IRIs used here are taken from the OWL 2 Primer
            String base = "https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#";
            pm = new DefaultPrefixManager(base);


            region = dataFactory.getOWLClass(":Region",pm);

            hasRegion = dataFactory.getOWLObjectProperty(":hasRegion",pm);

            hasPlace = dataFactory.getOWLObjectProperty(":hasPlace",pm);

            hasQualifier = dataFactory.getOWLObjectProperty(":hasQualifier",pm);


        }catch(OWLOntologyCreationException e){
            e.printStackTrace();
        }

    }


    public void readWikiWriteOnto(String fileName){

        //List<Page> pages = new ArrayList<Page>();

        try {

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            InputStream in = new FileInputStream(fileName);

            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // readSubClasses the XML document
            Page page = null;
            Revision revision = null;

            int k = 0;

            while(eventReader.hasNext()){

                XMLEvent event = eventReader.nextEvent();

                if(event.isStartElement()){

                    StartElement startElement = event.asStartElement();

                    if(startElement.getName().getLocalPart().equals(PAGE)){

                        page = new Page();

                    }

                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart().equals(TITLE)) {
                            event = eventReader.nextEvent();
                            page.title = (event.asCharacters().getData()).replaceAll("[()/| ]"," ").trim();
                            continue;
                        }
                    }

                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart().equals(ID)) {
                            event = eventReader.nextEvent();
                            page.id = (event.asCharacters().getData());
                            continue;
                        }
                    }

                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart().equals(REVISION)) {

                            revision = new Revision();
                            continue;

                        }
                    }

                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart().equals(TEXT)) {
                            event = eventReader.nextEvent();
                            if(revision!=null){
                                //revision.text =  event.asCharacters().getData();

                                while(!event.isEndElement()&&eventReader.hasNext()){

                                    revision.text +=" "+ event.asCharacters().getData().trim();
                                    event = eventReader.nextEvent();
                                }
                                revision.text = revision.text.trim();
                                continue;
                            }

                        }
                    }


                }

                if(event.isEndElement())
                {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart() == (PAGE)) {
                        //pages.add(page);
                    }

                    if(endElement.getName().getLocalPart() == (REVISION)){

                        ///Title has to be cleaned here , other wise
                        if(!sentenceValidator(page.title)){
                           continue;
                        }
                        page.title = formatString(page.title);
                        revision.data = formatData(revision.text);
                        page.revision = revision;

//                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        //Consider only locations in the first sentense describint the city
                        List<String> locations = getLocations(revision.data.get("text").split("\\.")[0]);
//                        System.out.println("Locations " +locations);

                        ///Writing the extracted data to the ontology

                        OWLNamedIndividual previous=null;

                        if(countryList.contains(page.title))
                            continue;

                        if(locations.size() !=0){

                            int countryIndex = locations.size()-1;
                            boolean hasCountry = false;

                            for(int i=0;i<locations.size();i++){

                                if(countryList.contains(locations.get(i))){
                                    hasCountry = true;
                                    countryIndex = i;
                                    break;
                                }

                            }


                            for(int i=countryIndex;i>=0;i--){

                                OWLNamedIndividual loc = dataFactory.getOWLNamedIndividual(":"+locations.get(i),pm);

                                if(!hasCountry){
                                    OWLClassAssertionAxiom assertion = dataFactory.getOWLClassAssertionAxiom(region,loc);
                                    manager.addAxiom(ontology, assertion);

                                }

                                if(i!=countryIndex){
                                    OWLObjectPropertyAssertionAxiom propAssertion = dataFactory.getOWLObjectPropertyAssertionAxiom(hasRegion,previous,loc);
                                    manager.addAxiom(ontology,propAssertion);

                                }

                                previous = loc;

                            }

                        }



                        OWLNamedIndividual titleLoc = dataFactory.getOWLNamedIndividual(":"+page.title,pm);

                        OWLClassAssertionAxiom titleClassAss = dataFactory.getOWLClassAssertionAxiom(region,titleLoc);
                        manager.addAxiom(ontology,titleClassAss);


                        if(previous!=null){
                            OWLObjectPropertyAssertionAxiom titlePropAss = dataFactory.getOWLObjectPropertyAssertionAxiom(hasRegion,previous,titleLoc);
                            manager.addAxiom(ontology,titlePropAss);

                        }

                        List<String> insideThings = getInsidePlaces(revision.data.get("Do")+revision.data.get("See"));

//                        System.out.println("Places :"+insideThings);
//                        System.out.println("Title :"+page.title);

                        for(String thing:insideThings){

                            OWLNamedIndividual locPlace = dataFactory.getOWLNamedIndividual(":"+thing,pm);
                            OWLClass thingType;
                            OWLClassAssertionAxiom placeTypeAsser;
                            OWLObjectPropertyAssertionAxiom placeProp;

                            //Getting the qualifier or Place type based on the text extracted from the <text></text>

                            if(!getQualifierType(thing).equals("Not")){

                                thingType = dataFactory.getOWLClass(":" + getQualifierType(thing), pm);
                                placeTypeAsser = dataFactory.getOWLClassAssertionAxiom(thingType,locPlace);
                                placeProp = dataFactory.getOWLObjectPropertyAssertionAxiom(hasQualifier,titleLoc,locPlace);

                            }else{

                                thingType = dataFactory.getOWLClass(":" + getPlaceType(thing), pm);
                                placeTypeAsser = dataFactory.getOWLClassAssertionAxiom(thingType,locPlace);
                                placeProp = dataFactory.getOWLObjectPropertyAssertionAxiom(hasPlace,titleLoc,locPlace);

                            }

                            manager.addAxiom(ontology,placeTypeAsser);
                            manager.addAxiom(ontology,placeProp);

                        }

                        /*k++;
                        System.out.println(k);
                        if(k==510){
                            break;
                        }*/


                    }
                }



            }




        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (XMLStreamException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }catch(Exception e){

        }finally {
            try {
                manager.saveOntology(ontology);
            } catch (OWLOntologyStorageException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


    }




    public Map<String,String> formatData(String tt){
        String texti = tt ;
        Map<String,String> toReturn = new HashMap<String, String>();

        Pattern patern = Pattern.compile("(==.*==)");
        String[] splitty = texti.split("(==.*==)");
        toReturn.put("text",textClean(splitty[0]));

        Matcher match = patern.matcher(texti);

        for(int i=1;i<splitty.length&&match.find();i++){


            toReturn.put(match.group(1).replace("=",""),textClean(splitty[i]));

        }

        return toReturn;
    }


    public String textClean(String text){

        String toAdd = "";
        String[] cleani = text.split("\n");
        for(String cleanta:cleani){
            toAdd +=" "+cleanta.trim();
        }

        toAdd = toAdd.trim();

        return toAdd;
    }


    public List<String> getLocations(String text){

        List<String> locations = new ArrayList<String>();
        if(text ==null || text=="")
            return locations;

        Pattern pattern = Pattern.compile("(\\[\\[([a-zA-Z _])*\\]\\])");


        Matcher match = pattern.matcher(text);

        int i = 0;
        while(match.find()&&i<3){

            String locPls = match.group(1).replace("[[", "").replace("]]", "").trim();
            if(!sentenceValidator(locPls))
                continue;
             locPls = formatString(locPls);

            if(!locations.contains(locPls)){
                locations.add(locPls);
                i++;
            }

        }

        return locations;

    }

    public String getQualifierType(String thing){
        int index = 0;
        boolean contains = false;
        for(int i=0;i<qualifierTypeList.size();i++){

            if(thing.contains(qualifierTypeList.get(i))){
               contains = true;
                index = i;
                break;
            }

        }

        if(contains){
            return qualifierTypeList.get(index).replace("_","");
        }else{
            return "Not";
        }


    }

    public List<String> getInsidePlaces(String text){

        List<String> placesList = new ArrayList<String>();

        if(text ==null || text=="")
            return placesList;

        Pattern pattern = Pattern.compile("('''[a-zA-Z _]*''')");


        Matcher match = pattern.matcher(text);


        while(match.find()){

            String inPls = match.group(1).replace("'''", "").trim();
            if(!sentenceValidator(inPls))
                continue;
            inPls = formatString(inPls);
            if(!placesList.contains(inPls)){
                placesList.add(formatString(inPls));
            }

        }

        return placesList;

    }

    public String getPlaceType(String place){


         int index = 0;
        boolean contains = false;
        for(int i = 0;i<placeTypesList.size();i++){

            if(place.contains(placeTypesList.get(i))){
                index = i;
                contains = true;
                break;
            }

        }


        if(contains){
            return placeTypesList.get(index).replace("_","");
        }else{
            return "Place";
        }


    }

    public boolean sentenceValidator(String text){


        Pattern pattern1 = Pattern.compile("[^a-zA-Z _]");
        Matcher match1  = pattern1.matcher(text);

        if(match1.find())
            return false;


        Pattern pattern2 = Pattern.compile("^\\w");
        Matcher match2 = pattern2.matcher(text);

        if(match2.find())
            return true;

        return false;

    }


    public String formatString(String data){
        String formatted = "";
        String temp = data.replace("_"," ").replaceAll("(\\s+)"," ");
        String[] splitted = temp.split(" ");

        for(String str:splitted){

            if(str=="")
                continue;

            str = str.toLowerCase();
            char[] chars = str.toCharArray();
            try{

                chars[0] = Character.toUpperCase(chars[0]);
                formatted +=" "+new String(chars);

            }catch(ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }

        return formatted.trim().replace(" ","_");
    }





}
