package org.extractors.stat.travelwiki;
import java.util.ArrayList;

import org.utilities.Utilities;



public class RuleEngine {
	ArrayList<String> classNames;
	public RuleEngine(ArrayList<String> classNames) {
		this.classNames = classNames;
	}
	
	public boolean isIndividual(String className, String instantName){
		String[] parts = instantName.split("\\s");
		if(parts.length > 1){
			String[] tokens = Utilities.camelToSentence(className).split("\\s");
			for(String item : tokens){
				if(item.toLowerCase().equals(parts[parts.length-1].toLowerCase())){
					return true;
				}
				
				if(item.toLowerCase().equals(parts[0].toLowerCase())){
					return true;
				}
			}
		}
		return false;
	}
	
}
