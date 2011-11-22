package com.rider.model;

import java.util.ArrayList;

public class RiderModel {
	
	private User user;
	private ArrayList<String> lines;
	
	
	public RiderModel() {
		this.user = new User();
		this.lines = new ArrayList<String>();
	}
	
	public User getUser() {
		return user;
	}
	
	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}
	
	public ArrayList<String> getLines() {
		return lines;
	}

	public String[] getLinesAsArray() {
		String[] newLines = new String[this.lines.size()];
		
		for(int i = 0; i< this.lines.size(); i++){
			newLines[i] = this.lines.get(i);
		}
		
		return newLines;
	}
}
