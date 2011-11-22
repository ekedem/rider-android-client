package com.rider.gps;

import com.rider.view.RiderUi;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {

	private Context context;
	private GpsListener listener;
	private RiderUi ui;
	
	public MyLocationListener(Context context) {
		this.context = context;
	}

	public void setListener(GpsListener listener){
		this.listener = listener;
	}

	public void setUi(RiderUi ui) {
		this.ui = ui;
	}

	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();

		this.listener.OnLocationUpdate(latitude, longitude);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		ui.turnOffGPSNotification(); 
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		ui.turnOnGPSNotification();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}

}
