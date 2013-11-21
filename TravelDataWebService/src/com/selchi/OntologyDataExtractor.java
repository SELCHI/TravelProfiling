package com.selchi;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


@Path("/ontodata")
public class OntologyDataExtractor {

    private Model model = null;
    @Context ResourceConfig context;

    public OntologyDataExtractor(){
    	
    	
    	
    }
       
    @GET
    @Path("/geolist")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<GeoData> getGeoDataList(){
    	
    	if(model==null){   		
    		String ontoPath = (String) context.getProperty("ontoPath");
        	FileManager.get().addLocatorClassLoader(OntologyDataExtractor.class.getClassLoader());
            model = FileManager.get().loadModel(ontoPath);
    	}
    	
    	
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
    	

        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, model);

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
    	if(model==null){   		
    		System.out.println("OntoModel is initialized");
        	FileManager.get().addLocatorClassLoader(OntologyDataExtractor.class.getClassLoader());
            model = FileManager.get().loadModel(ontoPath);
            
    	}
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


        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, model);

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
    	if(model==null){   		
    		System.out.println("OntoModel is initialized");
        	FileManager.get().addLocatorClassLoader(OntologyDataExtractor.class.getClassLoader());
            model = FileManager.get().loadModel(ontoPath);
            
    	}
    	
    	int count = 0;
    	
    	List<Item> data = new ArrayList<Item>();

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
                "travel:"+regionName+ " travel:hasQualifier ?data ."+
                "OPTIONAL {?data travel:hasWeeklyTrendValue ?trend }"+
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
                 "OPTIONAL {?data travel:hasWeeklyTrendValue ?trend }"+
                 "}"+
                 "ORDER BY DESC (?trend)"+
                 "LIMIT 10";
         

        Query query1 = QueryFactory.create(queryString1);


        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, model);

        try{
            ResultSet results1 = qexecu1.execSelect();

            while(results1.hasNext()){
            	count++;
            	
                QuerySolution solution = results1.nextSolution();
                String qdata = clean(solution.getResource("data").toString());
                int trend;
                try{
                	trend = Integer.parseInt(solution.getLiteral("trend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").trim());
                }catch(Exception e){
                	trend = 0;
                }
                
                Item toAdd = new Item();
                toAdd.setName(qdata);
                toAdd.setTrending(trend>30?true:false);
                data.add(toAdd);
                
            }
            
            if(count<10){
            	qexecu1.close();
            	
            	query1 = QueryFactory.create(queryString2);
            	qexecu1 = QueryExecutionFactory.create(query1, model);
            	
            	results1 = qexecu1.execSelect();
            	
            	while(results1.hasNext()&&count<10){
            		
            		count++;
            		QuerySolution solution = results1.nextSolution();
            		String qdata = clean(solution.getResource("data").toString());
            		int trend;
                    try{
                    	trend = Integer.parseInt(solution.getLiteral("trend").toString().replace("^^http://www.w3.org/2001/XMLSchema#int","").trim());
                    }catch(Exception e){
                    	trend = 0;
                    }
            		
                    Item toAdd = new Item();
                    toAdd.setName(qdata);
                    toAdd.setTrending(trend>30?true:false);
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


}
