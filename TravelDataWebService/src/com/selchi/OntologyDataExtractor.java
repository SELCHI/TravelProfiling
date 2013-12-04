package com.selchi;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.crypto.Data;


@Path("/ontodata")
public class OntologyDataExtractor {

	@Context
	ResourceConfig context;

    public OntologyDataExtractor(){  
    	
    }
    
    @GET
    @Path("/geolist")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<GeoData> getGeoDataList(){
    		
    	String ontoPath = (String) context.getProperty("ontoPath");
    	List<GeoData> geoList = new ArrayList<GeoData>();
    	
    	String queryString1 = "" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX travel:<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                "SELECT DISTINCT ?region ?latitu ?longitu WHERE { " +

                "?region travel:hasLatitude ?latitu ."+
                "?region travel:hasLongitude ?longitu ."+

                "}";
    	
    	Query query1 = QueryFactory.create(queryString1);
    	

        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, OntoSingletonModels.getOntModel(ontoPath));

        try{
        	
            ResultSet results1 = qexecu1.execSelect();
            
            while(results1.hasNext()){
            	
            	GeoData geo = new GeoData();
                QuerySolution solution = results1.nextSolution();
                String region = clean(solution.getResource("region").toString());
                geo.setRegion(region);
                String latitu = solution.getLiteral("latitu").toString().replace("^^http://www.w3.org/2001/XMLSchema#double", "");
                geo.setLatitude(Double.parseDouble(latitu));
                String longitu = solution.getLiteral("longitu").toString().replace("^^http://www.w3.org/2001/XMLSchema#double","");
                geo.setLongitude(Double.parseDouble(longitu));

                geoList.add(geo);

            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            qexecu1.close();
        }


        return geoList;
  	
    }
    
      
    @GET
    @Path("/geocodes/{region}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public GeoData getGeoCods(@PathParam("region") String region){
    	  	
    	
    	String ontoPath = (String) context.getProperty("ontoPath");
        GeoData geo = null;

        String queryString1 = "" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX travel:<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                "SELECT DISTINCT ?latitu ?longitu WHERE { " +

                "travel:"+region+" travel:hasLatitude ?latitu ."+
                "travel:"+region+" travel:hasLongitude ?longitu ."+

                "}";

        Query query1 = QueryFactory.create(queryString1);
        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, OntoSingletonModels.getOntModel(ontoPath));


        try{
            ResultSet results1 = qexecu1.execSelect();

            if(results1.hasNext()){
            	
                geo = new GeoData();
                
                QuerySolution solution = results1.nextSolution();
                
                String latitu = solution.getLiteral("latitu").toString().replace("^^http://www.w3.org/2001/XMLSchema#double", "");
                geo.setLatitude(Double.parseDouble(latitu));
                String longitu = solution.getLiteral("longitu").toString().replace("^^http://www.w3.org/2001/XMLSchema#double","");
                geo.setLongitude(Double.parseDouble(longitu));
                
                System.out.println("Region : "+region +" Latitude : "+latitu+" Longitude : "+longitu);
                
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            qexecu1.close();
        }


        return geo;

    }
      
    @GET
    @Path("/trendsaround/{region}/{type}/{isActivity}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Item> getTrendsAround(@PathParam("region") String regionName, @PathParam("type") String activitytype, @PathParam("isActivity") boolean isActivity ){
    	String ontoPath = (String) context.getProperty("ontoPath");    	
    	int count = 0;
    	
    	List<Item> data = new ArrayList<Item>();
    	activitytype = activitytype.replace("_","");
    	
        String relation = "hasPlace";

        if(isActivity){
            relation = "hasQualifier";
        }
        
        

        String queryString1 = "" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX travel:<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                "SELECT DISTINCT ?data ?trend WHERE { " +
                "?data rdf:type ?type ." +
                "?type rdfs:subClassOf* travel:"+activitytype+ " ."+
                "travel:"+regionName+ " travel:"+relation+" ?data ."+
                "OPTIONAL {?data travel:hasTotWTrendValue ?trend }"+
                "}" +
                "ORDER BY DESC (?trend)"+
                "LIMIT 10" ;
        
         String queryString2 = "" +
                 "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                 "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                 "PREFIX travel:<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                 "SELECT DISTINCT ?data ?trend WHERE { " +
                 "?data rdf:type ?type ." +
                 "?type rdfs:subClassOf* travel:"+activitytype+ " ."+
                 "travel:"+regionName+ " travel:hasRegion+ ?region ."+
                 "?region travel:"+relation+" ?data ."+
                 "OPTIONAL {?data travel:hasTotWTrendValue ?trend }"+
                 "}"+
                 "ORDER BY DESC (?trend)"+
                 "LIMIT 10";
         

        Query query1 = QueryFactory.create(queryString1);


        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, OntoSingletonModels.getOntModel(ontoPath));

        try{
            ResultSet results1 = qexecu1.execSelect();

            while(results1.hasNext()){
            	count++;
            	
                QuerySolution solution = results1.nextSolution();
                String qdata = clean(solution.getResource("data").toString());
                int trend;
                try{
                	String rdata = solution.getLiteral("trend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
                	rdata = rdata.replace("eger", "").trim();
                	trend = Integer.parseInt(rdata);
                }catch(Exception e){
                	trend = 0;
                }
                
                Item toAdd = new Item();
                toAdd.setName(qdata);
                toAdd.setTrending(trend>30?true:false);
                toAdd.setTrend(trend);
                data.add(toAdd);
            }
            
            if(count<10){
            	qexecu1.close();
            	
            	query1 = QueryFactory.create(queryString2);
            	qexecu1 = QueryExecutionFactory.create(query1, OntoSingletonModels.getOntModel(ontoPath));
            	
            	results1 = qexecu1.execSelect();
            	
            	while(results1.hasNext()&&count<10){
            		
            		count++;
            		QuerySolution solution = results1.nextSolution();
            		String qdata = clean(solution.getResource("data").toString());
            		int trend;
                    try{
                    	String rdata = solution.getLiteral("trend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
                    	rdata = rdata.replace("eger", "").trim();
                    	trend = Integer.parseInt(rdata);
                    }catch(Exception e){
                    	trend = 0;
                    }
            		
                    Item toAdd = new Item();
                    toAdd.setName(qdata);
                    toAdd.setTrending(trend>30?true:false);
                    toAdd.setTrend(trend);
                    data.add(toAdd);
            		
            	}
            	
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            qexecu1.close();
        }

        return data;

    }

    private String clean(String data){
        return data.replace("https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#","");
    }

    
    @GET
    @Path("/trendingregionsfor/{type}/{isActivity}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Item> getTrends(@PathParam("type") String type, @PathParam("isActivity") boolean isActivity){
    	
    	String ontoPath = (String) context.getProperty("ontoPath");
    	    	
    	List<Item> data = new ArrayList<Item>();
    	type = type.replace("_", "");
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
                "SELECT DISTINCT ?region ?totwtrend WHERE { " +
                "?data rdf:type ?type ." +
                "?type rdfs:subClassOf* travel:"+type+ " ."+
                "?region travel:"+relation+" ?data ."+
                "OPTIONAL {?data travel:hasTotWTrendValue ?totwtrend }"+
                "}"+
                "ORDER BY DESC (?totwtrend)"+
                "LIMIT 10";
        
        
        Query query1 = QueryFactory.create(queryString);	
        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, OntoSingletonModels.getOntModel(ontoPath));

        try{
        	
        	ResultSet results1 = qexecu1.execSelect();
        	while(results1.hasNext()){
        		
        		QuerySolution solution = results1.nextSolution();
                String region = clean(solution.getResource("region").toString());
                
                int trend;
                try{
                	String rdata = solution.getLiteral("totwtrend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
                	rdata = rdata.replace("eger", "").trim();
                	trend = Integer.parseInt(rdata);
                }catch(Exception e){                	
                	trend = 0;
                }
                
                Item toAdd = new Item();
                toAdd.setName(region);
                toAdd.setTrending(trend>30?true:false);
                toAdd.setTrend(trend);
                data.add(toAdd);
                     		
        	}
        	
        }catch(Exception e){
        	
        }finally{
        	qexecu1.close();
        }
        
        return data;
    	
    }
    
    
    
    @GET
    @Path("/typedata/{type}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public DataOb getTypeData(@PathParam("type") String type){
    	
    	String ontoPath = (String) context.getProperty("ontoPath");
    	type = type.replace("_","");   	
    	DataOb toReturn = new DataOb();
    	List<String> subclasses = new ArrayList<String>();
    	List<Item> individuals = new ArrayList<Item>();
    	  	
    	String subclassQuery = "" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX travel:<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                "SELECT DISTINCT ?type WHERE { " +
                "?type rdfs:subClassOf travel:"+type+ " ."+
                "}";
    	
    	String individualQuery = "" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX travel:<https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology_New.owl#>\n" +
                "SELECT DISTINCT ?indi ?trend WHERE { " +
                "?indi rdf:type travel:"+type+" ."+
                "OPTIONAL { ?indi travel:hasTotWTrendValue ?trend }"+
                "}"+
                "ORDER BY DESC (?trend)"+
                "LIMIT 7";
    	
    	Query query1 = QueryFactory.create(subclassQuery);	
        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, OntoSingletonModels.getOntModel(ontoPath));
       	
        try{
        	
        	ResultSet results1 = qexecu1.execSelect();
        	
        	while(results1.hasNext()){
        		
        		QuerySolution solution = results1.nextSolution();
                String subclass = clean(solution.getResource("type").toString());
                subclasses.add(subclass);               
                
        	}
        	
        	qexecu1.close();
        	query1 = QueryFactory.create(individualQuery);
        	qexecu1 = QueryExecutionFactory.create(query1, OntoSingletonModels.getOntModel(ontoPath));
        	
        	results1 = qexecu1.execSelect();
        	
        	while(results1.hasNext()){
        		
        		QuerySolution solution = results1.nextSolution();
                String indi = clean(solution.getResource("indi").toString());
                
                int trend;
                try{
                	String rdata = solution.getLiteral("trend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","");
                	rdata = rdata.replace("eger", "").trim();
                	trend = Integer.parseInt(rdata);
                }catch(Exception e){
                	trend = 0;
                }
                Item toAdd = new Item();
                toAdd.setName(indi);
                toAdd.setTrend(trend);               
                individuals.add(toAdd);           
                
        	}
        	
        }catch(Exception e){
        
        	e.printStackTrace();
        	
        }finally{
        	qexecu1.close();
        }
        
        toReturn.setInstenses(individuals);
        toReturn.setSubclasses(subclasses);
        
        return toReturn;
    }
    
    
    
}
