package org.extractors.stat.travelwiki;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Writer {
	public void WriteFile(String fileName, HashMap<String, HashMap<String, ArrayList<String>>> myMap){
		try {
			String indendation = "  ";
	        File statText = new File("res/"+ fileName +".txt");
	        FileOutputStream is = new FileOutputStream(statText);
	        OutputStreamWriter osw = new OutputStreamWriter(is);    
	        BufferedWriter w = new BufferedWriter(osw);
	        
	        for(String topic: myMap.keySet()){
	        	w.write(topic + "\n");
	        	for(String subTopic: myMap.get(topic).keySet()){
	        		w.write(indendation + subTopic + "\n");
	        		for(String instant: myMap.get(topic).get(subTopic)){
	        			w.write(indendation + indendation + instant + "\n");
	        		}
	        	}
	        	
	        }
	        w.close();
	    } catch (IOException e) {
		    System.err.println("Problem writing to the file statsTest.txt");
		}
	}
}
