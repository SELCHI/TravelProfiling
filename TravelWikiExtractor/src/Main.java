import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.ontologies.populators.OntologyPopulator;
import org.ontologies.populators.Ontopop;
import org.utilities.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;

import org.extractors.stat.travelwiki.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Main {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			final OntologyPopulator tdestOp = new OntologyPopulator("SelchiLocationOntology", "https://raw.github.com/SELCHI/TravelProfiling/master/SelchiLocationOntology.owl");
			ArrayList<String> countries = Utilities.citiesList();
			DefaultHandler handler = new DefaultHandler() {
				//String tmpValue;
				private StringBuilder textBuilder;
				private boolean textExist = false;
				private boolean valid = false;
				private String[] actionNames = {"parsing :"};
				private Populator populator = new Populator();
				private Writer writer = new Writer();
				private Extractor extractor = new Extractor(tdestOp);
				private String avoidPatterns = ".*([I|i]mage|[T|t]alk|[U|u]ser|[W|w]ikitravel).*";
				private String locationName = "";
				@Override
				public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
					if(qName.equals("title")){
						this.textBuilder = new StringBuilder();
						textExist = true;
					}
					else if(qName.equals("text")){
						this.textBuilder = new StringBuilder();
						textExist = true;
					}
				}
				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					if(textExist && qName == "title"){
						if(!this.textBuilder.toString().matches(this.avoidPatterns)){
							this.locationName = Utilities.sentenceToSingle(this.textBuilder.toString()).replaceAll("[^\\w^\\s]", "");
							System.out.println("Parsing : " + this.locationName);
							extractor.popltr.addInstance("City", this.locationName);
							extractor.popltr.curLocation = this.locationName;
							textExist = false;
							valid = true;
						}
						else{
							valid = false;
						}
					}
					else if(textExist && valid){
						extractor.extract(this.textBuilder.toString(), this.locationName);
						valid = false;
					}
				}
				@Override
				public void characters(char ch[], int start, int length) throws SAXException {
					if(textExist){
						this.textBuilder.append(ch, start, length);
					}
				}
				@Override
				public void endDocument(){
					//writer.WriteFile("keywords", this.populator.myMap);
				}
			};
//			for(String s : Utilities.camelToSentence("AsdsdDdfdf").split("\\s")){
//				System.out.println(s);
//			}
//			ArrayList<String> arl = Utilities.citiesList();
//			for(String one : arl){
//				System.out.println(one);
//			}
//			for(String one: arl){
//				System.out.println(one);
//			}
			//testOp.getIndividuals("Safari");
			//System.out.println(Utilities.camelToSentence("AadssdFsdadssaCdsasd"));
			saxParser.parse("res/wikitravelorg_wiki_en-20110605-current.xml", handler);
			//System.out.println("---"+ Utilities.camelToSentence("fsdsds Gdsds") + "---");
//			String[] lines = Utilities.camelToSentence("Aadasd Cadasd").split("\\s");
//			for(String s : lines){
//				System.out.println(s);
//			}
//			System.out.println(tdestOp.getSubClasses("Adventure").size());
//			op.addInstance("Airport", "Heathrew");
//			op.getIndividuals("Airport");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
