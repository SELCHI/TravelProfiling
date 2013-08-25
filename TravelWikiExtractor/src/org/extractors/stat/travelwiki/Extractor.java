package org.extractors.stat.travelwiki;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ontologies.populators.OntologyPopulator;
import org.utilities.Utilities;
import org.w3c.dom.NodeList;

public class Extractor {
	String[] patterns = { "==([\\p{L} ]+)==", "===([\\p{L} ]+)===", "'''([\\p{L} ]+)'''", "\\[\\[([\\p{L} ]+)\\]\\]", "\\*([\\p{L} ]+)", "\\[\\[([\\p{L} ]+)\\|([\\p{L} ]+)\\]\\]"};
	public Populator populator;
	public OntologyPopulator popltr;
	
	public Extractor(OntologyPopulator popltr) {
		this.popltr = popltr;
	}

	public void extract(String wikiMarkup, String locationName) {
		String regex = combine(patterns, "|");
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(wikiMarkup);
		
		String mainTopic = "";
		String subTopic = "";
		
		
		
		while(matcher.find()){ 
			
//			if(null != matcher.group(1)){
//				populator.populateTopic(matcher.group(1));
//				continue;
//			}
//			
//			if(null != matcher.group(2)){
//				populator.populateSubTopic(matcher.group(2));
//				continue;
//			}
			
			for(int i = 3; i < 7; i++){
				if(matcher.group(i) != null){
					String curMatch = matcher.group(i).trim();
					if((curMatch.split("\\s").length < 7) && (curMatch.split("\\s").length > 1)){
						if(popltr.populateOnLeaf(curMatch.replaceAll("[^\\w^\\s]", ""), "Qualifier")){
							popltr.addObjectProperty("hasQualifier", Utilities.sentenceToSingle(locationName), Utilities.sentenceToSingle(curMatch));
						}
						else if(popltr.populateOnLeaf(curMatch.replaceAll("[^\\w^\\s]", ""), "Region")){
							popltr.addObjectProperty("hasPlace", Utilities.sentenceToSingle(locationName), Utilities.sentenceToSingle(curMatch));
						}
					}
				}
			}
			
			
		}
		
	}

	public String combine(String[] s, String glue) {
		int k = s.length;
		if (k == 0){
			return null;
		}
		StringBuilder out = new StringBuilder();
		out.append(s[0]);
		for (int x = 1; x < k; ++x){
			out.append(glue).append(s[x]);
		}
		return out.toString();
	}
	
}
