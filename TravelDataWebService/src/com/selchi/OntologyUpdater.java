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
        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, OntoSingletonModels.getOntModel(ontoPath));
        
        
        try{
        	
            ResultSet results1 = qexecu1.execSelect();
            if(istwitter){          	
            	updateTwitter(results1, wtrend, mtrend);
            }else{
            	updateFourSquare(results1, wtrend, mtrend);
            }
            
            
            qexecu1.close();
            query1 = QueryFactory.create(queryString2);
            qexecu1 = QueryExecutionFactory.create(query1, OntoSingletonModels.getOntModel(ontoPath));
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
		
		return OntoSingletonModels.writeOntoModel(ontoPath);
		
	}
	
	
	public void updateTwitter(ResultSet r,int twtrend,int tmtrend){
		
		String ontoPath = (String) context.getProperty("ontoPath");
		Property hasTWTrend = OntoSingletonModels.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTWTrendValue");
		Property hasTMTrend = OntoSingletonModels.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTMTrendValue");
		Property hasTotWTrend = OntoSingletonModels.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTotWTrendValue");
		Property hasTotMTrend = OntoSingletonModels.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTotMTrendValue");
		
		List<ItemData> itemDataList = new ArrayList<ItemData>();
				
		while(r.hasNext()){
        	
			ItemData toAdd = new ItemData();
            QuerySolution solution = r.nextSolution();
            String activity = solution.getResource("data").toString();  
            
            Individual individual = OntoSingletonModels.getOntModel(ontoPath).getIndividual(activity);
            System.out.println(individual.toString());
            toAdd.setIndividual(individual);
            
            //Removing Twitter weekly trend value
            try{
            	
            	int twtrendOld = Integer.parseInt(solution.getLiteral("twtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());             
                toAdd.setTwtrendOld(twtrendOld);
            }catch(Exception e){
            	 toAdd.setTwtrendOld(-1);
            }
            
            //Removing Twitter monthly trend value
            try{
            	
            	int tmtrendOld = Integer.parseInt(solution.getLiteral("tmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	toAdd.setTmtrendOld(tmtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTmtrendOld(-1);
            }
            
            //Removing Total weekly trend value
            try{
            	
            	int totwtrendOld = Integer.parseInt(solution.getLiteral("totwtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	toAdd.setTotwtrendOld(totwtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTotwtrendOld(-1);
            }
            
            
            //Removing Total Monthly trend value
            try{
            	
            	int totmtrendOld = Integer.parseInt(solution.getLiteral("totmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	toAdd.setTotmtrendOld(totmtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTotmtrendOld(-1);
            }
            
            //Acquiring current Foursquare weekly trend value and monthly trend value
            
            int fwtrend;
            
            try{
            	
            	fwtrend = Integer.parseInt(solution.getLiteral("fwtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	
            }catch(Exception e){
            	
            	fwtrend = 0;
            	
            }
            
            toAdd.setFwtrendOld(fwtrend);
            
            int fmtrend;
            
            try{
            	
            	fmtrend = Integer.parseInt(solution.getLiteral("fmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	
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
				Statement stm = OntoSingletonModels.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTWTrend,itemData.getTwtrendOld()); 
				OntoSingletonModels.getOntModel(ontoPath).remove(stm);
			}
			
			//Adding new Twitter Weekly value
            
            OntoSingletonModels.getOntModel(ontoPath).addLiteral(itemData.getIndividual(),hasTWTrend,twtrend); 
			
            //Remove old Twitter monthly value
			if(itemData.getTmtrendOld()!=1){
				Statement stm = OntoSingletonModels.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTMTrend, itemData.getTmtrendOld());
            	OntoSingletonModels.getOntModel(ontoPath).remove(stm);
			}
			
			//Adding Twitter monthly trend value
            
            OntoSingletonModels.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTMTrend, tmtrend);
			
            //Removing Total weekly trend value
			if(itemData.getTotwtrendOld()!=-1){
				Statement stm = OntoSingletonModels.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTotWTrend, itemData.getTotwtrendOld());
            	OntoSingletonModels.getOntModel(ontoPath).remove(stm);
			}
			
			//Removing Total Monthly trend value
			if(itemData.getTotmtrendOld()!=-1){
				Statement stm = OntoSingletonModels.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTotMTrend, itemData.getTotmtrendOld());
            	OntoSingletonModels.getOntModel(ontoPath).remove(stm);
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
          OntoSingletonModels.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTotWTrend, totwtrend);

          //Updating the Total Monthly Trend Value
          OntoSingletonModels.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTotMTrend, totmtrend);
			
		}
				
	}
	
	
	public void updateFourSquare(ResultSet r,int fwtrend,int fmtrend){
		
		String ontoPath = (String) context.getProperty("ontoPath");
		Property hasFWTrend = OntoSingletonModels.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasFWTrendValue");
		Property hasFMTrend = OntoSingletonModels.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasFMTrendValue");
		Property hasTotWTrend = OntoSingletonModels.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTotWTrendValue");
		Property hasTotMTrend = OntoSingletonModels.getOntModel(ontoPath).getDatatypeProperty(preFix+"hasTotMTrendValue");
		
		List<ItemData> itemDataList = new ArrayList<ItemData>();		
		while(r.hasNext()){
			
			ItemData toAdd = new ItemData();
			QuerySolution solution = r.nextSolution();
            String activity = solution.getResource("data").toString();  
            
            Individual individual = OntoSingletonModels.getOntModel(ontoPath).getIndividual(activity);
            System.out.println(individual.toString());
            toAdd.setIndividual(individual);
            //Removing Four Square weekly trend value
            try{
            	
            	int fwtrendOld = Integer.parseInt(solution.getLiteral("fwtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());             
            	toAdd.setFwtrendOld(fwtrendOld);
                
            }catch(Exception e){
            	toAdd.setFwtrendOld(-1);
            }
            
            
            //Removing Four Square monthly trend value
            try{
            	
            	int fmtrendOld = Integer.parseInt(solution.getLiteral("fmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	toAdd.setFmtrendOld(fmtrendOld);
            	
            }catch(Exception e){
            	toAdd.setFmtrendOld(-1);
            }
            
                     
            //Removing Total weekly trend value
            try{
            	
            	int totwtrendOld = Integer.parseInt(solution.getLiteral("totwtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	toAdd.setTotwtrendOld(totwtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTotwtrendOld(-1);
            }
            
            
            //Removing Total Monthly trend value
            try{
            	
            	int totmtrendOld = Integer.parseInt(solution.getLiteral("totmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	toAdd.setTotmtrendOld(totmtrendOld);
            	
            }catch(Exception e){
            	toAdd.setTotmtrendOld(-1);
            }
            
            //Acquiring current Twitter weekly trend value and monthly trend value
            
            int twtrend;
            
            try{
            	
            	twtrend = Integer.parseInt(solution.getLiteral("twtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	
            }catch(Exception e){
            	
            	twtrend = 0;
            	
            }
            toAdd.setTwtrendOld(twtrend);
            
            int tmtrend;
            
            try{
            	
            	tmtrend = Integer.parseInt(solution.getLiteral("tmtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").replace("eger","").trim());
            	
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
				Statement stm = OntoSingletonModels.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasFWTrend,itemData.getFwtrendOld()); 
				OntoSingletonModels.getOntModel(ontoPath).remove(stm);
			}
			
			//Adding new FourSquare Weekly value
            
            OntoSingletonModels.getOntModel(ontoPath).addLiteral(itemData.getIndividual(),hasFWTrend,fwtrend); 
			
            //Remove old FourSquare monthly value
			if(itemData.getFmtrendOld()!=1){
				Statement stm = OntoSingletonModels.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasFMTrend, itemData.getFmtrendOld());
            	OntoSingletonModels.getOntModel(ontoPath).remove(stm);
			}
			
			//Adding FourSquare monthly trend value
            
            OntoSingletonModels.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasFMTrend, fmtrend);
			
            //Removing Total weekly trend value
			if(itemData.getTotwtrendOld()!=-1){
				Statement stm = OntoSingletonModels.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTotWTrend, itemData.getTotwtrendOld());
            	OntoSingletonModels.getOntModel(ontoPath).remove(stm);
			}
			
			//Removing Total Monthly trend value
			if(itemData.getTotmtrendOld()!=-1){
				Statement stm = OntoSingletonModels.getOntModel(ontoPath).createLiteralStatement(itemData.getIndividual(), hasTotMTrend, itemData.getTotmtrendOld());
            	OntoSingletonModels.getOntModel(ontoPath).remove(stm);
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
	            OntoSingletonModels.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTotWTrend, totwtrend);
	            
	            //Updating the Total Monthly Trend Value
	            OntoSingletonModels.getOntModel(ontoPath).addLiteral(itemData.getIndividual(), hasTotMTrend, totmtrend);
		}
		
	}
	
	
}
