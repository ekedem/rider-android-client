package com.rider.model;

import java.util.ArrayList;

public class ServerResult {
	
	// the status when the user try to login or register
	private boolean statusResponse;
	// the id of the user which made the request
	private int userID;
	// line station
	private Station station;
	private Station sourceStation;
	private Station destStation;
	private boolean checkinResult;
	private boolean loginStatus;
	private ArrayList<String> lines;
	private ArrayList<Station> stations;
	
	public ServerResult() {
		this.lines = new ArrayList<String>();
		this.stations = new ArrayList<Station>();
		// if not getting this value, prevent from changing direction when not needed (cant be a number grater than 0)
	}
	
	public ArrayList<Station> getStations() {
		return stations;
	}
	
	public void setStation(Station station) {
		this.station = station;
	}
	
	public Station getStation() {
		return station;
	}
	
	public boolean getStatusResponse() {
		return statusResponse;
	}
	
	public void setStatusResponse(boolean statusResponse) {
		this.statusResponse = statusResponse;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public Station getDestStation() {
		return destStation;
	}
	
	public Station getSourceStation() {
		return sourceStation;
	}
	
	public void setDestStation(Station destStation) {
		this.destStation = destStation;
	}
	
	public void setSourceStation(Station sourceStation) {
		this.sourceStation = sourceStation;
	}
	
	public void setCheckinResult(boolean checkinResult) {
		this.checkinResult = checkinResult;
	}
	
	public boolean isCheckinResult() {
		return checkinResult;
	}
	
	public void setLoginStatus(boolean loginStatus) {
		this.loginStatus = loginStatus;
	}
	
	public boolean isLoginStatus() {
		return loginStatus;
	}
	
	public ArrayList<String> getLines() {
		return lines;
	}
	
	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}
}
