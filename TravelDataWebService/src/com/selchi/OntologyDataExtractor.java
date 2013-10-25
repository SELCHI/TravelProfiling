package com.selchi;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.sun.org.glassfish.gmbal.ParameterNames;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/trendsaround/{region}/{type}/{isActivity}")
public class OntologyDataExtractor {

    private Model model;

    public OntologyDataExtractor(){

        FileManager.get().addLocatorClassLoader(OntologyDataExtractor.class.getClassLoader());
        model = FileManager.get().loadModel("/home/lasitha/Programing/FYP/TravelProfiling/TravelDataWebService/res/SelchiLocationOntology_New2.owl");

    }


    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Item> getTrendsAround(@PathParam("region") String regionName, @PathParam("type") String activitytype, @PathParam("isActivity") boolean isActivity ){

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
                 "travel:"+regionName+ " travel:hasRegion+ ?region ."+
                 "?region travel:"+relation+" ?data ."+
                 "?data travel:hasTrendValue ?trend ."+
                 "}" +
                 "LIMIT 10";



        Query query1 = QueryFactory.create(queryString1);


        QueryExecution qexecu1 = QueryExecutionFactory.create(query1, model);

        try{
            ResultSet results1 = qexecu1.execSelect();

            while(results1.hasNext()){
                QuerySolution solution = results1.nextSolution();
                String qdata = clean(solution.getResource("data").toString());
                int trend = Integer.parseInt(solution.getLiteral("trend").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer",""));
                Item toAdd = new Item();
                toAdd.setName(qdata);
                toAdd.setTrending(trend>30?true:false);
                data.add(toAdd);
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
