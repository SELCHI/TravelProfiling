package com.selchi;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.ws.rs.core.Context;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.sun.jersey.api.core.ResourceConfig;

public class OntoModelSingleton {
	
	private static OntModel ontModel;
	@Context
	static ResourceConfig context;
	
	protected OntoModelSingleton(){
		
		
	}
	
	public static OntModel getOntModel(String ontoPath){
		
		if(ontModel==null){
			
			FileManager.get().addLocatorClassLoader(OntoModelSingleton.class.getClassLoader());
	        InputStream in = FileManager.get().open(ontoPath);
	        ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
	        ontModel.read(in,"RDF/XML");
	       
		}
		
		return ontModel;
	}
	
	
	public static String writeOntoModel(String ontoPath){
		
		PrintStream p;
		String toReturn = "Done";
		try {
			p = new PrintStream(ontoPath);
			ontModel.write(p,"RDF/XML");
			p.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			toReturn="Error";
			e.printStackTrace();
		}
	
		return toReturn;
		
	}
	
	
	

}
