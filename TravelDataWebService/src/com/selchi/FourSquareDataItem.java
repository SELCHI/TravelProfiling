package com.selchi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FourSquareDataItem {
	
	private String name;
	private String ontotag;
	private int checkins;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOntotag() {
		return ontotag;
	}
	public void setOntotag(String ontotag) {
		this.ontotag = ontotag;
	}
	public int getCheckins() {
		return checkins;
	}
	public void setCheckins(int checkins) {
		this.checkins = checkins;
	}
	
	
	

}
