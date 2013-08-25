package org.ontologies.populators;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class Ontopop {
	File f;
	OWLOntologyManager manager;
	OWLOntology ontology;
	IRI ontologyIRI;
	OWLDataFactory dataFactory;
	String baseIRI;
	ArrayList<String> classNames;
	public Ontopop(String fileName, String baseIRI) {
		try {
			f = new File("res/ontologies/" + fileName + ".owl");
			this.baseIRI = baseIRI;
			manager = OWLManager.createOWLOntologyManager();
			ontology = manager.loadOntologyFromOntologyDocument(f);
			ontologyIRI = manager.getOntologyDocumentIRI(ontology);
			dataFactory = manager.getOWLDataFactory();
			this.extractClasses();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}
	
	private void extractClasses(){
		String regex = "#(\\w+)";
		classNames = new ArrayList<String>();
		Iterator it = ontology.getClassesInSignature().iterator();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher;
		while(it.hasNext()){
			OWLClass single = (OWLClass) it.next();
			matcher = pattern.matcher(single.toString());
			while(matcher.find()){
				classNames.add(matcher.group(1));
			}
		}
	}
	
	public ArrayList<String> getSubClasses(String className){
		String regex = "#(\\w+)";
		ArrayList<String> allNames = new ArrayList<String>();
		OWLClass myClass = dataFactory.getOWLClass(IRI.create(baseIRI + "#" + className));
		Iterator it = myClass.getSubClasses(ontology).iterator();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher;
		while(it.hasNext()){
			OWLClass single = (OWLClass) it.next();
			matcher = pattern.matcher(single.toString());
			while(matcher.find()){
				allNames.add(matcher.group(1));
				allNames.addAll(this.getSubClasses(matcher.group(1)));
			}
		}
		return allNames;
		
	}
	
	public void addInstance(String className, String individualName){
		 try {
			 OWLIndividual myIndividual = dataFactory.getOWLNamedIndividual(IRI.create(this.baseIRI + "#" + individualName));
			 OWLClass myClass = dataFactory.getOWLClass(IRI.create(baseIRI + "#" + className));
			 OWLClassAssertionAxiom classAssertion = dataFactory.getOWLClassAssertionAxiom(myClass, myIndividual);
			 manager.addAxiom(ontology, classAssertion);
			 manager.saveOntology(ontology);
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addObjectProperty(String propertyName, String subject, String object){
		 try {
			 OWLIndividual mySubject = dataFactory.getOWLNamedIndividual(IRI.create(this.baseIRI + "#" + subject));
			 OWLIndividual myObject = dataFactory.getOWLNamedIndividual(IRI.create(this.baseIRI + "#" + object));
			 OWLObjectProperty myProperty = dataFactory.getOWLObjectProperty(IRI.create(this.baseIRI + "#" + propertyName));
			 OWLObjectPropertyAssertionAxiom assertion = dataFactory.getOWLObjectPropertyAssertionAxiom(myProperty, mySubject, myObject);
			 AddAxiom addAxiomChange = new AddAxiom(ontology, assertion);
			 manager.applyChange(addAxiomChange);
			 manager.saveOntology(ontology);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getIndividuals(String className){
		OWLClass myClass = dataFactory.getOWLClass(IRI.create(baseIRI + "#" + className));
		Iterator it = myClass.getIndividuals(ontology).iterator();
		while(it.hasNext()){
			OWLIndividual single = (OWLIndividual) it.next();
			System.out.println(single.toString());
		}
	}
	
	public void extractRelations(){
		Iterator it = ontology.getObjectPropertiesInSignature().iterator();
		while(it.hasNext()){
			OWLObjectProperty single = (OWLObjectProperty) it.next();
			System.out.println(single.toString());
		}
	}
	
	public ArrayList<String> getClassNames(){
		return classNames;
	}
	
}
