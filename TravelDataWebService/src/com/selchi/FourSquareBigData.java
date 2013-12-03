package com.selchi;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FourSquareBigData {
	
	private int others;
	private List<FourSquareDataItem> topFive;
	public int getOthers() {
		return others;
	}
	public void setOthers(int others) {
		this.others = others;
	}
	public List<FourSquareDataItem> getTopFive() {
		return topFive;
	}
	public void setTopFive(List<FourSquareDataItem> topFive) {
		this.topFive = topFive;
	}
	
	

}
