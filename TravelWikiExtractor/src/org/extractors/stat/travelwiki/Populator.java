package org.extractors.stat.travelwiki;

import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

 
import org.ontologies.populators.Ontopop;
import org.utilities.Utilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Populator {
	
	private String topic = "other";
	private String subTopic = "other";
	public HashMap<String, HashMap<String, ArrayList<String>>> myMap = new  HashMap<String, HashMap<String, ArrayList<String>>>();
	public Ontopop op = new Ontopop("travelProfilingLocationOntology", "https://raw.github.com/wattale/TravelProfiling/master/travelProfilingLocationOntology.owl");
    public RuleEngine re = new RuleEngine(op.getClassNames());
    
	
	public void populateTopic(String item){
		if(!myMap.containsKey(item)){
			this.topic = item;
			myMap.put(item, new HashMap<String, ArrayList<String>>());
		}
	}
	
	public void populateSubTopic(String item){
		if(this.topic != null){
			if(!myMap.containsKey(this.topic)){
				this.populateSubTopic(this.topic);
			}
			if(!myMap.get(this.topic).containsKey(item)){
				this.subTopic = item;
				myMap.get(this.topic).put(item, new ArrayList<String>());
			}
		}
	}
	
	public void populateInstance(String item, String locationName){
		String word = "";
		if(item.split("\\s").length < 6){
			boolean hasActivity = false;
			for(String name : op.getSubClasses("ActivityQualifier")){
				if(re.isIndividual(name, item)){
					word = Utilities.sentenceToSingle(item.toLowerCase());
					op.addInstance(name, word);
					op.addObjectProperty("hasActivity", locationName, word );
					hasActivity = true;
				}
			}
			
			if(!hasActivity){
				for(String name : op.getSubClasses("Place")){
					if(re.isIndividual(name, item)){
						word = Utilities.sentenceToSingle(item.toLowerCase());
						op.addInstance(name, word);
						op.addObjectProperty("hasPlace", locationName, word);
					}
				}
			}
			
		}
		
//		if(this.topic != null && this.subTopic != null){
//			if(!myMap.containsKey(this.topic)){
//				this.populateTopic("other");
//			}
//			if(!myMap.get(this.topic).containsKey(this.subTopic)){
//				this.populateSubTopic("other");
//			}
//			if(!myMap.get(this.topic).get(this.subTopic).contains(item)){
//				myMap.get(this.topic).get(this.subTopic).add(item);
//			}
//		}
	}
	
	
	
	public void setSubTopic(String subTopic){
		this.subTopic = subTopic;
	}
	
	public void setTopic(String topic){
		this.topic = topic;
	}
}

