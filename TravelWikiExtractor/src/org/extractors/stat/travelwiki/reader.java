package org.extractors.stat.travelwiki;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class reader {
	public File fXmlFile;
	public String FileName;
	private static int i;

	public reader(String filename) {
		this.FileName = filename;
		// TODO Auto-generated constructor stub
	}
	
	public NodeList read() {
		NodeList nList =null;
		try {
			fXmlFile = new File(this.FileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			System.out.println("parsing started");
			Document doc = dBuilder.parse(fXmlFile);
			System.out.println("normalizing started");
			doc.getDocumentElement().normalize();
			
			nList = doc.getElementsByTagName("page"); 
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nList;
	}

}
