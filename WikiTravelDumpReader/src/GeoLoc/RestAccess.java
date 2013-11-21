package GeoLoc;

import data.GeoData;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasitha
 * Date: 10/27/13
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class RestAccess {


    private int count;
    private String consumerKey;

    public RestAccess(){
        count = 1;
    }


    public GeoData getGeoData(String location){

        if(count ==1){

            consumerKey = "dj0yJmk9M0tockNrd1FIZEgwJmQ9WVdrOVNqVnNWMUk0TXpZbWNHbzlNVFUxTmpRME5qWXkmcz1jb25zdW1lcnNlY3JldCZ4PTFh";

        }else if(count ==2001){
            consumerKey = "dj0yJmk9eEtjNWppVTRmUXNrJmQ9WVdrOWRqTmhaR3hFTjJjbWNHbzlOekF4TnpNd09EWXkmcz1jb25zdW1lcnNlY3JldCZ4PTk5";
        }else if(count == 4001){
            consumerKey = "dj0yJmk9bk5tb0NQTXEzcmtQJmQ9WVdrOVdFZFJVR2RhTkdNbWNHbzlNVGcyT0RNNE1EazJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1kYQ--";
        }

        GeoData data = new GeoData();
        List<String> regionHierarchy = new ArrayList<String>();
        location = location.replace(" ","%20");

        String uri = "http://where.yahooapis.com/v1/places.q('"+location+"')?appid="+consumerKey;


        try {

            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept","application/xml");


            InputStream  in = connection.getInputStream();

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            while(eventReader.hasNext()){

                XMLEvent event = eventReader.nextEvent();

                if(event.isStartElement()){

                    StartElement startElement = event.asStartElement();

                    if(startElement.getName().getLocalPart().equals("name")){
                        event = eventReader.nextEvent();
                        data.setRegionName(event.asCharacters().getData());
                        continue;
                    }

                   if(startElement.getName().getLocalPart().equals("country")||startElement.getName().getLocalPart().equals("admin1")||startElement.getName().getLocalPart().equals("admin2")||
                           startElement.getName().getLocalPart().equals("admin3")){
                       event = eventReader.nextEvent();
                       if(!event.isStartElement()&&!event.isEndElement()&&!event.asCharacters().getData().equals(location))
                            regionHierarchy.add(event.asCharacters().getData());
                       continue;

                   }

                   if(startElement.getName().getLocalPart().equals("centroid")){
                       eventReader.nextEvent();
                       event = eventReader.nextEvent();
                       data.setLatitude(Double.parseDouble(event.asCharacters().getData()));

                       eventReader.nextEvent();
                       eventReader.nextEvent();
                       event = eventReader.nextEvent();
                       data.setLongitude(Double.parseDouble(event.asCharacters().getData()));

                       data.setRegionHierarchy(regionHierarchy);

                       return data;


                   }

                }

            }



        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (XMLStreamException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        count++;
        return null;


    }


}
