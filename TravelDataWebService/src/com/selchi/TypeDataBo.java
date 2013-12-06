package com.selchi;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TypeDataBo {

	private List<Item> instenses;
	private List<String> subclasses;
	
	public List<Item> getInstenses() {
		return instenses;
	}
	public void setInstenses(List<Item> instenses) {
		this.instenses = instenses;
	}
	public List<String> getSubclasses() {
		return subclasses;
	}
	public void setSubclasses(List<String> subclasses) {
		this.subclasses = subclasses;
	}
	
	
	

}
