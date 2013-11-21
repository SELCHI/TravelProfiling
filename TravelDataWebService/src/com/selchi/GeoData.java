package com.selchi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GeoData {
	
	private String region;
	
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	private double longitude;
	private double latitude;
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	
	

}
