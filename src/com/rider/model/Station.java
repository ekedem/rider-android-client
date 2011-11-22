package com.rider.model;

public class Station {

	Coordinates location;
	String name;
	String latitude;
	String longitude;
	String lineNumber;
	String companyName;
	String time;
	String type;	
	
	public Station(String name, String latitude, String longitude, String lineNumber, String companyName, String type) {
		this.latitude = latitude;
		this.name = name;
		this.longitude = longitude;
		this.lineNumber = lineNumber;
		this.companyName = companyName;
		location = new Coordinates(Double.parseDouble(latitude), Double.parseDouble(longitude));
		this.time = "";
		this.type = type;
	}
	
	public Station(String name, String latitude, String longitude, String lineNumber, String companyName, String time, String type) {
		this(name, latitude, longitude, lineNumber, companyName, type);
		this.time = time;
	}
	
	public Coordinates getLocation() {
		return location;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	
	public void setLocation(Coordinates location) {
		this.location = location;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getLineNumber() {
		return lineNumber;
	}
	
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
}
