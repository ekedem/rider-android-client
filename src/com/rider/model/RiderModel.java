package com.rider.model;

import java.util.ArrayList;

public class RiderModel {
	
	private User user;
	private ArrayList<Line> lines;
	
	
	public RiderModel() {
		this.user = new User();
		this.lines = new ArrayList<Line>();
	}
	
	public User getUser() {
		return user;
	}
	
	public void setLines(ArrayList<Line> lines) {
		this.lines = lines;
	}
	
	public ArrayList<Line> getLines() {
		return lines;
	}

	public String[] getLinesAsArray() {
		String[] newLines = new String[this.lines.size()];
		
		for(int i = 0; i< this.lines.size(); i++){
			newLines[i] = this.lines.get(i).getNumber();
		}
		
		return newLines;
	}
	
	public String[] getLinesIDAsArray() {
		String[] newLines = new String[this.lines.size()];
		
		for(int i = 0; i< this.lines.size(); i++){
			newLines[i] = this.lines.get(i).getId();
		}
		
		return newLines;
	}
}
