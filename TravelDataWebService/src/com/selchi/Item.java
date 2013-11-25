package com.selchi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Item {
	
	private String name;
	private boolean isTrending;
	private int trend;
	
	
	public int getTrend() {
		return trend;
	}
	public void setTrend(int trend) {
		this.trend = trend;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isTrending() {
		return isTrending;
	}
	public void setTrending(boolean isTrending) {
		this.isTrending = isTrending;
	}
	
}
