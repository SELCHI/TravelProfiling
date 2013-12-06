package com.selchi;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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

@Path("/updatetrends")
public class OntologyUpdater {
	
	String preFix = "https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#";
	@Context
	ResourceConfig context;
    
    
    public OntologyUpdater(){
    	
    }
	
	@GET
    @Path("/{istwitter}/{region}/{isActivity}/{type}/{mvalue}/{wvalue}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String updateOntology(@PathParam("istwitter") boolean istwitter, @PathParam("region") String regionName,@PathParam("isActivity") boolean isActivity, @PathParam("type") String type, @PathParam("wvalue") int wtrend, @PathParam("mvalue") int mtrend){
			
		type = type.replace("_", "");
		String toReturn = "Done";		
		String ontoPath = (String) context.getProperty("ontoPath");
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
                "SELECT DISTINCT ?data ?twtrend ?tmtrend ?fwtrend ?fmtrend ?totwtrend ?totmtrend WHERE { " +
                "?data rdf:type ?type ." +
                "?type rdfs:subClassOf* travel:"+type+ " ."+
                "travel:"+regionName+ " travel:"+relation+" ?data ."+
                "OPTIONAL {?data travel:hasTWTrendValue ?twtrend }"+
                "OPTIONAL { ?data travel:hasTMTrendValue ?tmtrend  }"+
                "OPTIONAL { ?data travel:hasFWTrendValue ?fwtrend  }"+
                "OPTIONAL { ?data travel:hasFMTrendValue ?fmtrend  }"+
                "OPTIONAL { ?data travel:hasTotWTrendValue ?totwtrend  }"+
                "OPTIONAL { ?data travel:hasTotMTrendValue ?totmtrend  }"+
                "}";
		
		String queryString2 = "" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX travel:<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                "SELECT DISTINCT ?data ?twtrend ?tmtrend ?fwtrend ?fmtrend ?totwtrend ?totmtrend WHERE { " +
                "?data rdf:type ?type ." +
                "?type rdfs:subClassOf* travel:"+type+ " ."+
                "travel:"+regionName+ " travel:hasRegion+ ?region ."+
                "?region travel:"+relation+" ?data ."+
                "OPTIONAL {?data travel:hasTWTrendValue ?twtrend }"+
                "OPTIONAL { ?data travel:hasTMTrendValue ?tmtrend  }"+
                "OPTIONAL { ?data travel:hasFWTrendValue ?fwtrend  }"+
                "OPTIONAL { ?data travel:hasFMTrendValue ?fmtrend  }"+
                "OPTIONAL { ?data travel:hasTotWTrendValue ?totwtrend  }"+
                "OPTIONAL { ?data travel:hasTotMTrendValue ?totmtrend  }"+
                "}";

		Query query1 = QueryFactory.create(queryString);	
        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, OntoModelSingleton.getOntModel(ontoPath));
        
        
        try{
        	
            ResultSet results1 = qexecu1.execSelect();
            if(istwitter){          	
            	updateTwitter(results1, wtrend, mtrend);
            }else{
            	updateFourSquare(results1, wtrend, mtrend);
            }
            
            
            qexecu1.close();
            query1 = QueryFactory.create(queryString2);
            qexecu1 = QueryExecutionFactory.create(query1, OntoModelSingleton.getOntModel(ontoPath));
            results1 = qexecu1.execSelect();
            
            if(istwitter){          	
            	updateTwitter(results1, wtrend, mtrend);
            }else{
            	updateFourSquare(results1, wtrend, mtrend);
            }
                                  

        }catch (Exception e){
        	toReturn ="Error";
            e.printStackTrace();
        }finally {
            qexecu1.close();
        }
		       
        return toReturn;
        
	}
	
	@GET
    @Path("/finalize")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String finalizeUpdate(){		
		String ontoPath = (String) context.getProperty("ontoPath");
		
		return OntoModelSingleton.writeOntoModel(ontoPath);
		
	}
	
	
	public void updateTwitter(ResultSet r,int twtrend,int tmtrend){
		
		String ontoPath = (String) context.getProperty("ontoPath");
		Property hasTWTrend = OntoModelSingleton.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTWTrendValue");
		Property hasTMTrend = OntoModelSingleton.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTMTrendValue");
		Property hasTotWTrend = OntoModelSingleton.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTotWTrendValue");
		Property hasTotMTrend = OntoModelSingleton.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTotMTrendValue");
		
		List<ItemData> itemDataList = new ArrayList<ItemData>();
				
		while(r.hasNext()){
        	
			ItemData toAdd = new ItemData();
            QuerySolution solution = r.nextSolution();
            String activity = solution.getResource("data").toString();  
            
            Individual individual = OntoModelSingleton.getOntModel(ontoPath).getIndividual(activity);
            if(individual.toString().equals("https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#Booker_Gym")){
            	System.out.println("hey");
            }
            
            System.out.println(individual.toString());
            toAdd.setIndividual(individual);
            
            //Removing Twitter weekly trend value
            
            try{
            	
            	String rdata = solution.getLiteral("twtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	int twtrendOld = Integer.parseInt(rdata);             
                toAdd.setTwtrendOld(twtrendOld);
                
            }catch(Exception e){
            	 toAdd.setTwtrendOld(-1);
            }
            
            //Removing Twitter monthly trend value
            
            try{
            	String rdata = solution.getLiteral("tmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	int tmtrendOld = Integer.parseInt(rdata);
            	toAdd.setTmtrendOld(tmtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTmtrendOld(-1);
            }
            
            //Removing Total weekly trend value
            
            try{
            	String rdata = solution.getLiteral("totwtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	int totwtrendOld = Integer.parseInt(rdata);
            	toAdd.setTotwtrendOld(totwtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTotwtrendOld(-1);
            }
            
            
            //Removing Total Monthly trend value
            try{
            	String rdata = solution.getLiteral("totmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	int totmtrendOld = Integer.parseInt(rdata);
            	toAdd.setTotmtrendOld(totmtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTotmtrendOld(-1);
            }
            
            //Acquiring current Foursquare weekly trend value and monthly trend value
            
            int fwtrend;
            
            try{
            	String rdata = solution.getLiteral("fwtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	fwtrend = Integer.parseInt(rdata);
            	
            }catch(Exception e){
            	
            	fwtrend = 0;
            	
            }
            
            toAdd.setFwtrendOld(fwtrend);
            
            int fmtrend;
            
            try{
            	String rdata = solution.getLiteral("fmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	fmtrend = Integer.parseInt(rdata);
            	
            }catch(Exception e){
            	
            	fmtrend = 0;
            	
            }
            
            toAdd.setFmtrendOld(fmtrend);
            
            itemDataList.add(toAdd);
            
        }
		
		
		for(int i=0;i<itemDataList.size();i++){
			
			ItemData itemData = itemDataList.get(i);
			
			//Remove old Twitter weekly value
			if(itemData.getTwtrendOld()!=-1){
				Statement stm = OntoModelSingleton.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTWTrend,itemData.getTwtrendOld()); 
				OntoModelSingleton.getOntModel(ontoPath).remove(stm);
			}
			
			//Adding new Twitter Weekly value
            
            OntoModelSingleton.getOntModel(ontoPath).addLiteral(itemData.getIndividual(),hasTWTrend,twtrend); 
			
            //Remove old Twitter monthly value
			if(itemData.getTmtrendOld()!=1){
				Statement stm = OntoModelSingleton.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTMTrend, itemData.getTmtrendOld());
            	OntoModelSingleton.getOntModel(ontoPath).remove(stm); 
			}
			
			//Adding Twitter monthly trend value
            
            OntoModelSingleton.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTMTrend, tmtrend);
			
            //Removing Total weekly trend value
			if(itemData.getTotwtrendOld()!=-1){
				Statement stm = OntoModelSingleton.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTotWTrend, new Integer(itemData.getTotwtrendOld()));
            	OntoModelSingleton.getOntModel(ontoPath).remove(stm);
			}
			
			//Removing Total Monthly trend value
			if(itemData.getTotmtrendOld()!=-1){
				Statement stm = OntoModelSingleton.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTotMTrend, itemData.getTotmtrendOld());
            	OntoModelSingleton.getOntModel(ontoPath).remove(stm);
			}
			
			
		  //Calculate Total trend values
          int totwtrend,totmtrend;
          
          if(itemData.getFwtrendOld()==0){
          	totwtrend = twtrend;
          	
          }else{
          	totwtrend = (twtrend+itemData.getFwtrendOld())/2;
          }
          
          if(itemData.getFmtrendOld()==0){
          	totmtrend = tmtrend;
          }else{
          	totmtrend = (tmtrend+itemData.getFmtrendOld())/2;
          }
          
          //Updating the Total Weekly Trend Value
          OntoModelSingleton.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTotWTrend, totwtrend);

          //Updating the Total Monthly Trend Value
          OntoModelSingleton.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTotMTrend, totmtrend);
			
		}
				
	}
	
	
	public void updateFourSquare(ResultSet r,int fwtrend,int fmtrend){
		
		String ontoPath = (String) context.getProperty("ontoPath");
		Property hasFWTrend = OntoModelSingleton.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasFWTrendValue");
		Property hasFMTrend = OntoModelSingleton.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasFMTrendValue");
		Property hasTotWTrend = OntoModelSingleton.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTotWTrendValue");
		Property hasTotMTrend = OntoModelSingleton.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTotMTrendValue");
		
		List<ItemData> itemDataList = new ArrayList<ItemData>();		
		while(r.hasNext()){
			
			ItemData toAdd = new ItemData();
			QuerySolution solution = r.nextSolution();
            String activity = solution.getResource("data").toString();  
            
            Individual individual = OntoModelSingleton.getOntModel(ontoPath).getIndividual(activity);
            System.out.println(individual.toString());
            toAdd.setIndividual(individual);
            //Removing Four Square weekly trend value
            try{
            	String rdata = solution.getLiteral("fwtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	int fwtrendOld = Integer.parseInt(rdata);             
            	toAdd.setFwtrendOld(fwtrendOld);
                
            }catch(Exception e){
            	toAdd.setFwtrendOld(-1);
            }
            
            
            //Removing Four Square monthly trend value
            try{
            	String rdata = solution.getLiteral("fmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	int fmtrendOld = Integer.parseInt(rdata);
            	toAdd.setFmtrendOld(fmtrendOld);
            	
            }catch(Exception e){
            	toAdd.setFmtrendOld(-1);
            }
            
                     
            //Removing Total weekly trend value
            try{
            	String rdata = solution.getLiteral("totwtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	int totwtrendOld = Integer.parseInt(rdata);
            	toAdd.setTotwtrendOld(totwtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTotwtrendOld(-1);
            }
            
            
            //Removing Total Monthly trend value
            try{
            	String rdata = solution.getLiteral("totmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	int totmtrendOld = Integer.parseInt(rdata);
            	toAdd.setTotmtrendOld(totmtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTotmtrendOld(-1);
            }
            
            //Acquiring current Twitter weekly trend value and monthly trend value
            
            int twtrend;
            
            try{
            	String rdata = solution.getLiteral("twtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	twtrend = Integer.parseInt(rdata);
            	
            }catch(Exception e){
            	
            	twtrend = 0;
            	
            }
            toAdd.setTwtrendOld(twtrend);
            
            int tmtrend;
            
            try{
            	String rdata = solution.getLiteral("tmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
            	rdata = rdata.replace("eger","").trim();
            	tmtrend = Integer.parseInt(rdata);
            	
            }catch(Exception e){
            	
            	tmtrend = 0;
            	
            }
            
            toAdd.setTmtrendOld(tmtrend);
			
            itemDataList.add(toAdd);
			
		}
		
		for(int i =0;i<itemDataList.size();i++){
			ItemData itemData = itemDataList.get(i);
			
			//Remove old FourSquare weekly value
			if(itemData.getFwtrendOld()!=-1){
				Statement stm = OntoModelSingleton.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasFWTrend,itemData.getFwtrendOld()); 
				OntoModelSingleton.getOntModel(ontoPath).remove(stm);
			}
			
			//Adding new FourSquare Weekly value
            
            OntoModelSingleton.getOntModel(ontoPath).addLiteral(itemData.getIndividual(),hasFWTrend,fwtrend); 
			
            //Remove old FourSquare monthly value
			if(itemData.getFmtrendOld()!=1){
				Statement stm = OntoModelSingleton.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasFMTrend, itemData.getFmtrendOld());
            	OntoModelSingleton.getOntModel(ontoPath).remove(stm);
			}
			
			//Adding FourSquare monthly trend value
            
            OntoModelSingleton.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasFMTrend, fmtrend);
			
            //Removing Total weekly trend value
			if(itemData.getTotwtrendOld()!=-1){
				Statement stm = OntoModelSingleton.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTotWTrend, itemData.getTotwtrendOld());
            	OntoModelSingleton.getOntModel(ontoPath).remove(stm);
			}
			
			//Removing Total Monthly trend value
			if(itemData.getTotmtrendOld()!=-1){
				Statement stm = OntoModelSingleton.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTotMTrend, itemData.getTotmtrendOld());
            	OntoModelSingleton.getOntModel(ontoPath).remove(stm);
			}
			
			
			
			
			 int totwtrend,totmtrend;
	            
	            if(itemData.getTwtrendOld()==0){
	            	totwtrend = fwtrend;
	            	
	            }else{
	            	totwtrend = (itemData.getTwtrendOld()+fwtrend)/2;
	            }
	            
	            if(itemData.getTmtrendOld()==0){
	            	totmtrend = fmtrend;
	            }else{
	            	totmtrend = (itemData.getTmtrendOld()+fmtrend)/2;
	            }
	            
	            //Updating the Total Weekly Trend Value
	            OntoModelSingleton.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTotWTrend, totwtrend);
	            
	            //Updating the Total Monthly Trend Value
	            OntoModelSingleton.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTotMTrend, totmtrend);
		}
		
	}
	
	
}
