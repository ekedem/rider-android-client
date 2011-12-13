package com.rider.model;

public class Line {
	
	String id;
	String number;
	
	public Line(String lineNumber, String lineId) {
		this.id = lineId;
		this.number = lineNumber;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}

}
