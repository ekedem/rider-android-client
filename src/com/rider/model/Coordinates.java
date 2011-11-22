package com.rider.model;

import org.osmdroid.api.IGeoPoint;

import com.google.android.maps.GeoPoint;

public class Coordinates {

	private double longitude;
	private double latitude;
	
	public Coordinates(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public GeoPoint toGeo(){
		return new GeoPoint((int) (latitude*1E6), (int) (longitude*1E6));
	}
	
	public IGeoPoint toIGeo(){
		return new IGeoPoint() {
			
			@Override
			public int getLongitudeE6() {
				return (int) (longitude*1E6);
			}
			
			@Override
			public int getLatitudeE6() {
				return (int) (latitude*1E6);
			}
		};
	}
}
