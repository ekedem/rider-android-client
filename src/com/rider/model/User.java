package com.rider.model;


public class User {

	// COORDINATES of center tel aviv
	public static final double TEL_AVIV_LATITUDE = 32.0677778;
	public static final double TEL_AVIV_LONGITUDE = 34.7647222;
	
	private int userID;
	private String name;
	private String email;
	private String password;
	private boolean checkedIn;
	private double lastKnownLatitude;
	private double lastKnownLongitude;
	
	
	public User() {
		this.lastKnownLatitude = TEL_AVIV_LATITUDE;
		this.lastKnownLongitude = TEL_AVIV_LONGITUDE;
		this.checkedIn = false;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public void setLastKnownLatitude(double lastKnownLatitude) {
		this.lastKnownLatitude = lastKnownLatitude;
	}
	
	public void setLastKnownLongitude(double lastKnownLongitude) {
		this.lastKnownLongitude = lastKnownLongitude;
	}
	
	public double getLastKnownLatitude() {
		return lastKnownLatitude;
	}
	
	public double getLastKnownLongitude() {
		return lastKnownLongitude;
	}
	
	public boolean isCheckedIn() {
		return checkedIn;
	}
	
	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void updateUser(String email, String password, int userID) {
		this.email = email;
		this.password = password;
		this.userID = userID;
	}
}
