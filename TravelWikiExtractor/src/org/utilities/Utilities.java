package org.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
	
	public static String camelToSentence(String camel){
		String ouput = "";
		Pattern pattern = Pattern.compile("([A-Z])");
	    Matcher matcher = pattern.matcher(camel);
	    while(matcher.find()){
	    	ouput = matcher.replaceAll(" $1");
	    }
		return ouput.trim();
	}
	
	public static String sentenceToSingle(String sentence){
		return sentence.replaceAll("\\s", "_");
	}
	
	public static ArrayList<String> citiesList() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader("res/maincities.txt"));
		ArrayList<String> al = new ArrayList<String>();
		while (in.ready()) {
		  String s = in.readLine();
		  String word = s.split(",")[0];
		  String[] words = word.split("\\s");
		  if(words[words.length-1].matches("\\([\\w]+\\)")){
			  word = words[0];
			  for(int i = 1 ; i < words.length - 1 ; i++){
				  word = word + " " + words[i];
			  }
		  }
		  al.add(word.toLowerCase());
		}
		in.close();
		return al;
	}
}
