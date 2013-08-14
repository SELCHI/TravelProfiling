/**
 * Created with IntelliJ IDEA.
 * User: lasitha
 * Date: 8/12/13
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */

import static org.semanticweb.owlapi.vocab.OWLFacet.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import org.semanticweb.HermiT.Reasoner;

public class Main {

    public static OWLOntologyManager manager;
    public static OWLReasoner reasoner;

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {

        manager = OWLManager.createOWLOntologyManager();

        File file = new File("Ontology/travelProfilingLocationOntology.owl");
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);

        System.out.println("Loaded ontology: " + ontology);

        // We can always obtain the location where an ontology was loaded from
        IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
        System.out.println("    from: " + documentIRI);

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


        // Now we want to query the reasoner for all descendants of vegetarian.
        // Vegetarians are defined in the ontology to be animals that don't eat
        // animals or parts of animals.
        OWLDataFactory fac = manager.getOWLDataFactory();

        OWLClass country = fac.getOWLClass(IRI.create("https://raw.github.com/wattale/TravelProfiling/master/travelProfilingLocationOntology.owl#Country"));

        // Ask the reasoner for the instances of pet
        NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(country, true);

        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
        System.out.println("Instances of Country: ");
        for (OWLNamedIndividual ind : individuals) {
            System.out.println("    " + ind);
        }
        System.out.println("\n");

        //Get the Country instance for SriLanka
        OWLNamedIndividual SriLanka = fac.getOWLNamedIndividual(IRI.create("https://raw.github.com/wattale/TravelProfiling/master/travelProfilingLocationOntology.owl#Sri_Lanka"));


        //Get property hasCity
        OWLObjectProperty hasCity = fac.getOWLObjectProperty(IRI.create("https://raw.github.com/wattale/TravelProfiling/master/travelProfilingLocationOntology.owl#hasCity"));

        //Get the individuals which relate through hasCity property to Country Sri Lanka
        NodeSet<OWLNamedIndividual> cityValuesNodeSet = reasoner.getObjectPropertyValues(SriLanka, hasCity);
        Set<OWLNamedIndividual> values = cityValuesNodeSet.getFlattened();

        System.out.println("The hasCity property values for SriLanka are: ");


        for (OWLNamedIndividual ind : values) {
            System.out.println("    " + ind);
        }

        dlQueryTest();

    }


    public static void dlQueryTest() throws IOException {

        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        DLQueryInstances dlQueryInstances = new DLQueryInstances(new DLQueryEngine(reasoner, shortFormProvider));

        System.out
                .println("Please type a class expression in Manchester Syntax and press Enter (or press x to exit):");
        System.out.println("");
        String classExpression = readInput();
        // Check for exit condition
        if (classExpression.equalsIgnoreCase("x")) {
            return;
        }

        Set<OWLNamedIndividual> entities = dlQueryInstances.getInstances(classExpression.trim());

        for(OWLEntity enti:entities)
        {
            System.out.println(shortFormProvider.getShortForm(enti));
        }

        System.out.println();
        System.out.println();

    }

    private static String readInput() throws IOException {
        InputStream is = System.in;
        InputStreamReader reader;
        reader = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(reader);
        return br.readLine();
    }

}
