package com.rider;

import org.osmdroid.api.IMapView;

import com.google.android.maps.MapView;

public interface UiListener {
	
	/**
	 * after the main app was loaded
	 */
	public void onAppLoadScreen();
	/**
	 * initial loading and creating of the google map view
	 * @param mapView - the map view of the app
	 */
	public void onLoadGoogleMapRequest(MapView mapView);
	/**
	 * when returning to the map view. saves the last state
	 */
	public void onBackToMapRequest();
	public void onLoginRequest(String email,String password, boolean isRegister);
	public void onSearchAddressRequest(String address);
	public void onExitRequest();
	public void onNavigateRequest(String source,String destination);
	public void onBusNumberChooser(String lineNumber,boolean isCheckInRequest);
	public void onWakeUpRequest();
	public void onReportRequest();
	public void onErrorRequest(String errorMessage);
	public void onLoadOSMapRequest(org.osmdroid.views.MapView mapView);
}
