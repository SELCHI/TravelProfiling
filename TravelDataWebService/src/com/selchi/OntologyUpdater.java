package com.selchi;

import java.io.InputStream;
import java.io.PrintStream;

import javax.naming.spi.DirStateFactory.Result;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import com.sun.jersey.api.core.ResourceConfig;

@Path("/ontoupdate")
public class OntologyUpdater {
	
	String preFix = "https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#";
	private Model queryModel;
	private OntModel ontModel;
    @Context ResourceConfig context;
    
    
    public OntologyUpdater(){
    	
    	
    }
	
	@GET
    @Path("/add/{region}/{isActivity}/{type}/{mvalue}/{wvalue}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String updateOntology(@PathParam("region") String regionName,@PathParam("isActivity") boolean isActivity, @PathParam("type") String type, @PathParam("wvalue") int wtrend, @PathParam("mvalue") int mtrend){
				
		String toReturn = "Done";		
		
		String ontoPath = (String) context.getProperty("ontoPath");
    	if(queryModel==null){   		
    		
        	FileManager.get().addLocatorClassLoader(OntologyDataExtractor.class.getClassLoader());
        	queryModel = FileManager.get().loadModel(ontoPath);
        	
    	}
		
		if(ontModel == null){

			FileManager.get().addLocatorClassLoader(RDFWriter.class.getClassLoader());
	        InputStream in = FileManager.get().open(ontoPath);
	        ontModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
	        ontModel.read(in,null);
		}
		
		String relation = "hasPlace";

        if(isActivity){
            relation = "hasQualifier";
        }
		
		
		String queryString = "" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX travel:<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                "SELECT DISTINCT ?data ?trend ?mtrend WHERE { " +
                "?data rdf:type ?type ." +
                "?type rdfs:subClassOf* travel:"+type+ " ."+
                "travel:"+regionName+ " travel:"+relation+" ?data ."+
                "OPTIONAL {?data travel:hasWeeklyTrendValue ?trend }"+
                "OPTIONAL { ?data travel:hasMonthlyTrendValue ?mtrend  }"+
                "}";
		
		String queryString2 = "" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX travel:<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                "SELECT DISTINCT ?data ?trend ?mtrend WHERE { " +
                "?data rdf:type ?type ." +
                "?type rdfs:subClassOf* travel:"+type+ " ."+
                "travel:"+regionName+ " travel:hasRegion+ ?region ."+
                "?region travel:"+relation+" ?data ."+
                "OPTIONAL {?data travel:hasWeeklyTrendValue ?trend }"+
                "OPTIONAL { ?data travel:hasMonthlyTrendValue ?mtrend  }"+
                "}";

		Query query1 = QueryFactory.create(queryString);	
        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, queryModel);
        
        
        try{
        	
            ResultSet results1 = qexecu1.execSelect();

            update(results1, wtrend, mtrend);
            
            qexecu1.close();
            query1 = QueryFactory.create(queryString2);
            qexecu1 = QueryExecutionFactory.create(query1, queryModel);
            results1 = qexecu1.execSelect();
            
            update(results1,wtrend,mtrend);
                       
            PrintStream p = new PrintStream(ontoPath);
            ontModel.write(p,null);
            p.close();

        }catch (Exception e){
        	toReturn ="NotDone";
            e.printStackTrace();
        }finally {
            qexecu1.close();
        }
		
        
        return toReturn;
	}
	

	
	public void update(ResultSet r,int wtrend,int mtrend){
		
		Property hasWeeklyTrend = ontModel.getDatatypeProperty(preFix+"hasWeeklyTrendValue");
		Property hasMonthlyTrend = ontModel.getDatatypeProperty(preFix+"hasMonthlyTrendValue");
		
		while(r.hasNext()){
        	
            QuerySolution solution = r.nextSolution();
            String activity = solution.getResource("data").toString();  
            
            Individual regionQualifier = ontModel.getIndividual(activity);
            System.out.println(regionQualifier.toString());
            try{
            	
            	int wtrendOld = Integer.parseInt(solution.getLiteral("trend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());             
                Statement stm = ontModel.createLiteralStatement(regionQualifier, hasWeeklyTrend,wtrendOld); 
                ontModel.remove(stm);
                
            }catch(Exception e){
            	
            }
                        
            try{
            	
            	int mtrendOld = Integer.parseInt(solution.getLiteral("mtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	Statement stm2 = ontModel.createLiteralStatement(regionQualifier, hasMonthlyTrend, mtrendOld);
            	ontModel.remove(stm2);
            	
            }catch(Exception e){
            	
            }
                           
            ontModel.addLiteral(regionQualifier,hasWeeklyTrend,wtrend);  
            ontModel.addLiteral(regionQualifier, hasMonthlyTrend, mtrend);
            
        }
		
	}
	
	
}
