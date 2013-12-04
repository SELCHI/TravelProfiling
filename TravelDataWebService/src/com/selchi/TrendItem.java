package com.selchi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TrendItem {
	
	private String date;
	private String trendValue;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTrendValue() {
		return trendValue;
	}
	public void setTrendValue(String trendValue) {
		this.trendValue = trendValue;
	}
	
	

}
