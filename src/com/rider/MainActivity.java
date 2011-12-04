package com.rider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osmdroid.api.IGeoPoint;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.rider.OSMap.MyOSMapMarkers;
import com.rider.OSMap.OSMapOverlay;
import com.rider.database.DBAdapter;
import com.rider.googleMap.MapOverlay;
import com.rider.googleMap.MyMapMarkers;
import com.rider.gps.GpsListener;
import com.rider.gps.MyLocationListener;
import com.rider.model.Coordinates;
import com.rider.model.RiderModel;
import com.rider.model.ServerResult;
import com.rider.model.Station;
import com.rider.model.User;
import com.rider.proxy.RiderProxy;
import com.rider.proxy.RiderProxyListener;
import com.rider.view.ActivityAsViewOwner;
import com.rider.view.RiderUi;

public class MainActivity extends MapActivity implements OnSharedPreferenceChangeListener {

	private static int RIDER_NOTFICATION_ID = 0;

	// the progress bar of the app
	private ProgressDialog progressDialog;
	// the ui of the app
	private RiderUi ui;
	// the google map view
	private MapView mapView;
	private org.osmdroid.views.MapView osmapView;
	private org.osmdroid.views.MapController osmapController;
	// the google map controller
	private MapController mapControl;
	// handle the markers on the google map
	private MyMapMarkers mapStationsMarkers;
	private MyOSMapMarkers osmapStationsMarkers;
	// gps locations 
	private LocationManager locationManager;
	private MyLocationListener myLocationListener;
	// communicates with the server
	private RiderProxy proxy;
	// notification handlers
	private NotificationManager notificationManager;
	// the models of the game
	private RiderModel model;
	// the user marker on the google map
	private MapOverlay userOverlay;
	// the user marker on the osmdroid map
	private OSMapOverlay osuserOverlay;
	private MapOverlay searchOverlay;
	private DBAdapter riderdb;
	private BroadcastReceiver internetReceiver;
	private SharedPreferences prefs;
	private Drawable red;
	private Drawable blue;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		red = MainActivity.this.getResources().getDrawable(R.drawable.icon_bus);
		blue = MainActivity.this.getResources().getDrawable(R.drawable.icon_bus_blue);
		red.setBounds(0 - red.getIntrinsicWidth() / 2, 0 - red.getIntrinsicHeight(),red.getIntrinsicWidth() / 2, 0);
		blue.setBounds(0 - blue.getIntrinsicWidth() / 2, 0 - blue.getIntrinsicHeight(),blue.getIntrinsicWidth() / 2, 0);
		
		// listener to the preferences screen
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		riderdb = new DBAdapter(this);

		// initializing the progress Dialog
		this.progressDialog = new ProgressDialog(this);
		this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		// creating all the models in the app
		model = new RiderModel();

		// --------- UI PART -----------
		ui = new RiderUi(new ActivityAsViewOwner(this));
		ui.setModel(model);
		ui.setListener(new UiListener() { 

			/**
			 * called after the main app was loaded
			 */
			public void onAppLoadScreen() {
				Thread thread =  new Thread(){
					@Override
					public void run(){
						// waiting 4 seconds
						try {
							synchronized(this){
								wait(4000);
							}
						}
						catch(InterruptedException ex){                    
						}

						// calling the map screen after 4 seconds.
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ui.showLoginScreen();
							}
						});
					}
				};
				thread.start();
			}

			/**
			 * initial loading and creating of the google map view
			 * @param mapView - the map view of the app
			 */
			public void onLoadGoogleMapRequest(MapView newMapView) {
				// updates the mapView instance 
				mapView = newMapView;
				// setting the center of the map to be in Tel Aviv
				mapControl = mapView.getController();
				findLocation();
				mapControl.setCenter(locationToGeo(model.getUser().getLastKnownLatitude(), model.getUser().getLastKnownLongitude()));
				mapControl.setZoom(14); 
				// setting the properties of the map
				mapView.setBuiltInZoomControls(true);
				mapView.setSatellite(false);
				mapView.invalidate();

				// google map markers
				final MapOverlay tempUserOverlay = new MapOverlay(MainActivity.this,R.drawable.showme_green_icon);
				final MapOverlay tempSearchOverlay = new MapOverlay(MainActivity.this,R.drawable.showme_green_icon);
				userOverlay = tempUserOverlay;
				searchOverlay = tempSearchOverlay;
				mapStationsMarkers = new MyMapMarkers(red,MainActivity.this);
				mapStationsMarkers.setUi(ui);

				ui.setGoogleMap(true);

				locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
				updateNotificationStatus();
			}

			public void onLoadOSMapRequest(org.osmdroid.views.MapView mapView) {
				osmapView = mapView;

				//				osmapView.setTileSource(TileSourceFactory.MAPNIK);
				osmapView.setBuiltInZoomControls(true);
				osmapView.setMultiTouchControls(true);
				osmapView.setClickable(true);

				osmapController = osmapView.getController();
				osmapController.setZoom(14);
				findLocation();
				osmapController.setCenter(locationToIGeo(model.getUser().getLastKnownLatitude(), model.getUser().getLastKnownLongitude()));
				mapView.invalidate();

				final OSMapOverlay tempUserOverlay = new OSMapOverlay(MainActivity.this,R.drawable.showme_green_icon);
				osuserOverlay = tempUserOverlay;
				osmapStationsMarkers = new MyOSMapMarkers(MainActivity.this.getResources().getDrawable(R.drawable.icon_bus),MainActivity.this);
				osmapStationsMarkers.setUi(ui);
				osmapStationsMarkers.setEnabled(true);

				ui.setGoogleMap(false);

				updateNotificationStatus();
				locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
			}

			/**
			 * called when returning to the map view from other views. saves the last state
			 */
			public void onBackToMapRequest() {
				mapView.invalidate();
			}

			/**
			 * A user login request to server (registered user)
			 */
			public void onLoginRequest(final String email, final String password, final boolean isRegister) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progressDialog.setMessage("Connecting to server...");
						progressDialog.show();

						proxy.loginAndRegisterRequestToServer(email,password,isRegister);

						//						ui.showMapScreen(mapView);

						//ui.showOSMapScreen();
					}
				});
			}


			/**
			 * converts the given address to a point on the screen and show it
			 */
			public void onSearchAddressRequest(String address) {
				try {
					Coordinates result = addressToLocation(address);
					// when no result found
					if (result == null) throw new Exception();

					GeoPoint location_result = new GeoPoint((int) (result.getLatitude()*1E6), (int) (result.getLongitude()*1E6));
					// showing and flying to the search result point
					searchOverlay.setPoint(location_result);
					List<Overlay> listOfOverlays = mapView.getOverlays();
					listOfOverlays.remove(searchOverlay);
					listOfOverlays.add(searchOverlay); 
					mapControl.animateTo(location_result); 
					mapView.invalidate();

				} catch (Exception e) {
					ui.displayMessage("No result found");
				}
			}

			/**
			 * exits the app
			 */
			public void onExitRequest() {
				finish();
			}

			/**
			 * sends a navigation request from the user to the server to find buses  
			 */
			public void onNavigateRequest(String source, String destination) {
				progressDialog.setMessage("Loading...");
				progressDialog.show();

				// the converted addresses as coordinates
				Coordinates sourcePoint = new Coordinates(model.getUser().getLastKnownLatitude(), model.getUser().getLastKnownLongitude());
				Coordinates destinationPoint = addressToLocation(destination);

				// if the source location is not the default "my current location", convert
				if (!source.equals("")) {
					sourcePoint = addressToLocation(source);
				}

				try {
					proxy.navigationRequestToServer(sourcePoint.getLatitude(), sourcePoint.getLongitude(),
							destinationPoint.getLatitude(), destinationPoint.getLongitude(),
							model.getUser().getUserID(), 0);
				} catch (Exception e) {
					ui.showNavAddressErrorDialog();
					progressDialog.dismiss();
				}
			}

			/**
			 * when a bus line number was choose, a request is sent to the server based on the request type
			 * @param isCheckInRequest - if true then the request is for CheckIn
			 */
			public void onBusNumberChooser(String lineNumber,boolean isCheckInRequest) {
				progressDialog.setMessage("Loading...");
				progressDialog.show();

				try {
					int line = Integer.parseInt(lineNumber);

					if (isCheckInRequest) {
						// need to set top false sometime
						model.getUser().setCheckedIn(true);
						proxy.checkInRequestToServer(line, model.getUser().getUserID());
					} 
					else {
						proxy.LineRequestToServer(line, model.getUser().getUserID(), model.getUser().getLastKnownLatitude(),
								model.getUser().getLastKnownLongitude());
					}

				} catch (Exception e) {
					ui.displayMessage("Incorrect bus line number");
				}
			}

			@Override
			public void onReportRequest() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onWakeUpRequest() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onErrorRequest(String errorMessage) {
				progressDialog.setMessage("Reporting to server...");
				progressDialog.show();
				proxy.errorAppReportToServer(model.getUser().getUserID(),errorMessage);
			}

			@Override
			public void onUpdateLinesRequest() {
				// TODO Auto-generated method stub
				//				progressDialog.setMessage("Updating bus lines...");
				//				progressDialog.show();
				//				proxy.updateLinesRequestToServer(model.getUser().getUserID());
			}
		});


		// ----------- GPS PART -------------
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		myLocationListener = new MyLocationListener(this);
		myLocationListener.setUi(ui);
		//		locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
		myLocationListener.setListener(new GpsListener() {

			/**
			 * when there is a change in the GPS location (checked every few second as defined
			 * the map will animate to the location marked with the user marker
			 * @param latitude - the updated latitude point 
			 * @param longitude - the updated longitude point
			 */
			public void OnLocationUpdate(double latitude, double longitude) {
				// updating the model to save the last known location of the user
				model.getUser().setLastKnownLatitude(latitude);
				model.getUser().setLastKnownLongitude(longitude);

				if (mapView != null) {
					// painting the user location on the screen
					GeoPoint newLocation = locationToGeo(latitude, longitude);
					userOverlay.setPoint(newLocation);
					List<Overlay> listOfOverlays = mapView.getOverlays();
					listOfOverlays.remove(userOverlay);
					listOfOverlays.add(userOverlay); 
					// animating to the given location
					mapView.invalidate();
				}
				else {
					// painting the user location on the screen
					IGeoPoint newLocation = locationToIGeo(latitude, longitude);
					osuserOverlay.setPoint(newLocation);
					List<org.osmdroid.views.overlay.Overlay> listOfOverlays = osmapView.getOverlays();
					listOfOverlays.remove(osuserOverlay);
					listOfOverlays.add(osuserOverlay); 
					// animating to the given location
					osmapView.invalidate();
				}

				// ----- updating the server for the location ------
				// needs to set the chekedIn field to false sometime!
				//				if (model.getUser().isCheckedIn()) {
				//					//proxy.locationUpdateToServer(latitude, longitude,model.getUser().getUserID(),0);
				//				}
				//updateNotificationStatus();
			}
		});


		//------------ SERVER PART -----------
		proxy = new RiderProxy();
		proxy.setListener(new RiderProxyListener() {

			/**
			 * displaying the error connecting to server message as Toast
			 */
			public void onErrorConnection(final String message) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progressDialog.dismiss();
						ui.displayMessage(message);
						ui.showErrorDialog(message);
					}
				});

			}

			@Override
			public void onLineResultFromServer(ServerResult result) {
				ArrayList<Station> stations = result.getStations();
				boolean showAllStations = prefs.getBoolean("showAllStations", false);;

				try {
					Station closestStation = stations.get(result.getClosestStationIndex());

					// new api which shows all stations
					if (mapView != null) {
						clearGoogleMapMarkers();
						ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
						GeoPoint geo = null;
						Station station = null;
						String currentType = stations.get(0).getType();
						Drawable currentStationIcon = red;
						for(int i=0 ; i < stations.size() ; i++) {
							if (showAllStations || (i == result.getClosestStationIndex())) {
								station = stations.get(i);
								geo = station.getLocation().toGeo();
								overlays.add(new OverlayItem(geo, station.getName() , station.getLineNumber()));
								System.out.println("station " + i + ",type = " + station.getType());
								if (!currentType.equals(station.getType())){
									currentType = station.getType();
									currentStationIcon = blue;
								}
								overlays.get(i).setMarker(currentStationIcon);
							}
						}

						addOverlayToMap(overlays);
					}
					else {
						ArrayList<org.osmdroid.views.overlay.OverlayItem> overlays = new ArrayList<org.osmdroid.views.overlay.OverlayItem>();
						org.osmdroid.util.GeoPoint geo = null;
						Station station = null;
						String currentType = closestStation.getType();
						Drawable currentStationIcon = red;
						for(int i=0 ; i < stations.size() ; i++) {
							if (showAllStations || (i == result.getClosestStationIndex())) {
								station = stations.get(i);
								geo = new org.osmdroid.util.GeoPoint(Double.parseDouble(station.getLatitude()), Double.parseDouble(station.getLongitude()));
								overlays.add(new org.osmdroid.views.overlay.OverlayItem(station.getName() , station.getLineNumber(),geo));
								if (!currentType.equals(station.getType())){
									currentType = station.getType();
									currentStationIcon = blue;
								}
								overlays.get(i).setMarker(currentStationIcon);
							}
						}

						addOSOverlayToMap(overlays);
					}

					ui.setBarTextHeader(closestStation.getLineNumber());
					progressDialog.dismiss();
					showLocation(Double.parseDouble(closestStation.getLatitude()), Double.parseDouble(closestStation.getLongitude()));
					// returning the default marker
				} catch (Exception e) {
					progressDialog.dismiss();
					ui.showErrorDialog("problem parsing the stations from the line request. Client side");
				}

			}

			@Override
			public void onCheckinResultFromServer(ServerResult result) {
				progressDialog.dismiss();
				ui.displayMessage("CheckedIn =" + result.isCheckinResult());
			}

			@Override
			public void onNavigationResultFromServer(ServerResult result) {
				ArrayList<Station> stations = result.getStations();
				Station sourceStation = stations.get(0);
				Station destStation = stations.get(stations.size() - 1);
				boolean showAllStations = prefs.getBoolean("showAllStations", false);

				try {

					if (mapView != null) {
						clearGoogleMapMarkers();
						ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
						GeoPoint geo = null;
						Station station = null;
						String currentType = stations.get(0).getType();
						Drawable currentStationIcon = red;
						for(int i=0 ; i < stations.size() ; i++) {
							if (showAllStations || (i == 0) || (i == stations.size()-1)) {
								station = stations.get(i);
								geo = station.getLocation().toGeo();
								overlays.add(new OverlayItem(geo, station.getName() , station.getLineNumber() + " " + station.getTime()));
								System.out.println("station " + i + ",type = " + station.getType());
								if (!currentType.equals(station.getType())){
									currentType = station.getType();
									currentStationIcon = blue;
								}
								overlays.get(i).setMarker(currentStationIcon);
								
							}
						}

						addOverlayToMap(overlays);
					}
					else {
						ArrayList<org.osmdroid.views.overlay.OverlayItem> overlays = new ArrayList<org.osmdroid.views.overlay.OverlayItem>();
						org.osmdroid.util.GeoPoint geo = null;
						Station station = null;

						for(int i=0 ; i < stations.size() ; i++) {
							if (showAllStations || (i == 0) || (i == stations.size()-1)) {
								station = stations.get(i);
								geo = new org.osmdroid.util.GeoPoint(Double.parseDouble(station.getLatitude()), Double.parseDouble(station.getLongitude()));
								overlays.add(new org.osmdroid.views.overlay.OverlayItem(station.getName() , station.getLineNumber(),geo));
							}
						}

						addOSOverlayToMap(overlays);
					}
					
					ui.setBarTextHeader(destStation.getLineNumber());
					progressDialog.dismiss();
					showLocation(Double.parseDouble(sourceStation.getLatitude()), Double.parseDouble(sourceStation.getLongitude()));
				} catch (Exception e) {
					progressDialog.dismiss();
					ui.showErrorDialog("problem parsing the stations from the Navigation request. Client side");
				}
			}

			@Override
			public void onLoginRegisterResultFromServer(final ServerResult result, final String email, final String password) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progressDialog.dismiss();

						if (result.isLoginStatus() == true) {
							model.getUser().setEmail(email);
							model.getUser().setPassword(password);
							model.getUser().setUserID(0);
							updateUserTable();

							//ui.showOSMapScreen();
							ui.showMapScreen(mapView);
						}
						else {
							ui.showLoginErrorDialog();
						}
					}
				});

			}

			@Override
			public void onUpdateLinesResultFromServer(ArrayList<String> lines) {
				progressDialog.dismiss();
				model.setLines(lines);
				updateLinesTable();
				ui.displayMessage("Lines were successfully updated");
			}

			@Override
			public void onErrorAppReportResultFromServer() {
				progressDialog.dismiss();
			}
		});


		//-------- INTERNET LISTENER PART
		this.internetReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
				NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
				NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo( ConnectivityManager.TYPE_MOBILE );
				if ( activeNetInfo != null ) {
					ui.turnOnInternetNotification();
				}
				else {
					if( mobNetInfo != null ) {
						ui.turnOffInternetNotification();
					}
				}
			}
		};

		createNotification();
		loadDatabaseToModel();
	}

	/**
	 * sets the correct notification images for both GPS and INTERNET
	 */
	protected void updateNotificationStatus() {
		try {
			IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
			this.registerReceiver(internetReceiver, intentFilter);

			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				ui.turnOnGPSNotification();
			} 
			else {
				ui.turnOffGPSNotification();
			}
		} catch (Exception e){

		}
	}

	/**
	 * update the internet connectivity notification
	 */
	public void checkConnectivity(){

	}

	public void loadDatabaseToModel() {
		riderdb.open();
		Cursor c =  riderdb.getAllUsers();
		Cursor cLine =  riderdb.getAllLines();
		boolean knownUser = false;

		while (cLine.moveToNext()) {
			String lineNumber = cLine.getString(1);
			model.getLines().add(lineNumber);
		}

		// when there is a register user already in the local database
		if (c.moveToFirst()) {
			String email = c.getString(1);
			String password = c.getString(2);
			int userID = Integer.parseInt(c.getString(3));

			// updates the model from the database
			model.getUser().updateUser(email, password, userID);
			knownUser = true;
		}

		riderdb.close();

		if (knownUser) {
			ui.showLoginScreen();
			progressDialog.setMessage("Connecting to server...");
			progressDialog.show();
			proxy.loginAndRegisterRequestToServer(model.getUser().getEmail(),model.getUser().getPassword(),false);
		}
		else {
			ui.showLoginScreen();
		}

	}


	/**
	 * converts the latitude / longitude coordinates to a GeoPoint
	 */
	private GeoPoint locationToGeo(double latitude,double longitude) {
		return new GeoPoint((int) (latitude*1E6), (int) (longitude*1E6));
	}

	/**
	 * converts the latitude / longitude coordinates to a IGeoPoint
	 */
	private org.osmdroid.api.IGeoPoint locationToIGeo(final double latitude,final double longitude) {
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

	/** 
	 * add the given overlays to the station overlay on the google map
	 * @param overlays - each overlay is a point of a bus station
	 */
	private void addOverlayToMap(OverlayItem ... overlays){
		List<Overlay> mapOverlays = mapView.getOverlays();
		for (OverlayItem overlay : overlays) {
			mapStationsMarkers.addOverlay(overlay);
		}
		mapOverlays.add(mapStationsMarkers);
	}


	/** 
	 * add the given overlays to the station overlay on the google map 
	 * @param overlays - each overlay is a point of a bus station(arraylist)
	 */
	private void addOverlayToMap(ArrayList<OverlayItem> overlays){
		List<Overlay> mapOverlays = mapView.getOverlays();
		for (int i=0; i < overlays.size() ; i++) {
			mapStationsMarkers.addOverlay(overlays.get(i));
		}
		mapOverlays.add(mapStationsMarkers);
	}


	/** 
	 * add the given overlays to the station overlay on the google map
	 * @param overlays - each overlay is a point of a bus station
	 */
	private void addOSOverlayToMap(org.osmdroid.views.overlay.OverlayItem ... osOverlays){
		List<org.osmdroid.views.overlay.Overlay> osmapOverlays = osmapView.getOverlays();
		for (org.osmdroid.views.overlay.OverlayItem overlay : osOverlays) {
			osmapStationsMarkers.addOverlay(overlay);
		}
		osmapOverlays.add(osmapStationsMarkers);
	}

	/** 
	 * add the given overlays to the station overlay on the google map
	 * @param overlays - each overlay is a point of a bus station(arraylist)
	 */
	private void addOSOverlayToMap(ArrayList<org.osmdroid.views.overlay.OverlayItem> overlays){
		List<org.osmdroid.views.overlay.Overlay> osmapOverlays = osmapView.getOverlays();
		for (int i=0; i < overlays.size() ; i++) {
			osmapStationsMarkers.addOverlay(overlays.get(i));
		}
		osmapOverlays.add(osmapStationsMarkers);
	}

	/**
	 * convert the string address to a location(latitude and longitude)
	 * @param address - the address to be parsed to coordinates
	 * @return - the coordinates of the given address. null if no result was found
	 */
	private Coordinates addressToLocation(String address) {
		// in new thread
		Coordinates location_result = null;

		try {
			Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());  
			List<Address> addresses = geoCoder.getFromLocationName(address, 5);
			if (addresses.size() > 0) {
				location_result = new Coordinates(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
			}    
		} catch (IOException e) {
			ui.displayMessage(e.getMessage());
		} finally {
			return location_result;
		}
	}

	/**
	 * creates the notification details for this app
	 */
	private void createNotification() {
		// the notification icon in the status bar
		final Notification riderNotification = new Notification(R.drawable.icon ,"Welcome To Rider",System.currentTimeMillis());
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		// the details to show in the notification area
		Context context = getApplicationContext();
		CharSequence contentTitle = "Rider";
		CharSequence contentText = "return to Rider";
		// the action to perform when clicking the notification. (returning to the running activity)
		Intent notifyIntent = new Intent(MainActivity.this,MainActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
		PendingIntent intent = 
			PendingIntent.getActivity(MainActivity.this, 0, 
					notifyIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

		riderNotification.setLatestEventInfo(context, contentTitle, contentText, intent);
		riderNotification.flags |=  Notification.FLAG_NO_CLEAR;
		notificationManager.notify(RIDER_NOTFICATION_ID, riderNotification);
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * creating the option menu.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		setMenuBackground();
		return true;
	}

	/**
	 * controls the background color and the text color of the options menu 
	 */
	protected void setMenuBackground(){

		getLayoutInflater().setFactory( new Factory() {

			@Override
			public View onCreateView ( String name, Context context, AttributeSet attrs ) {

				if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {

					try { // Ask our inflater to create the view
						LayoutInflater f = getLayoutInflater();
						final View view = f.createView( name, null, attrs );
						/* 
						 * The background gets refreshed each time a new item is added the options menu. 
						 * So each time Android applies the default background we need to set our own 
						 * background. This is done using a thread giving the background change as runnable
						 * object
						 */
						new Handler().post( new Runnable() {
							public void run () {
								view.setBackgroundColor(Color.rgb(230, 230 , 230));
								((TextView) view).setTextColor(Color.BLACK);
							}
						} );
						return view;
					}
					catch ( InflateException e ) {}
					catch ( ClassNotFoundException e ) {}
				}
				return null;
			}
		});
	}



	/**
	 * when choosing an item from the option menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.checkInItem:
			ui.showLinesChooser(true);
			return true;
		case R.id.navigationItem:
			ui.showNavigationPopup();
			return true;
		case R.id.lineItem:
			ui.showLinesChooser(false);
			return true;
		case R.id.showMeItem:
			showCurrentLocation();
			return true;
		case R.id.settingsItem:
			Intent myIntent = new Intent(this, MyPreferences.class);
			startActivityForResult(myIntent, 0);
			return true; 
			//		case R.id.exitItem:
			//			ui.showExitDialog();
			//			return true;
			//		case R.id.layersItem:
			//			return true;
			//		case R.id.mapLayoutItem:
			//			// if the regular map view was clicked - disabling the satellite option
			//			item.setChecked(true);
			//			mapView.setSatellite(false);
			//			mapView.invalidate();
			//			return true;
			//		case R.id.sateliteLayoutItem:
			//			// setting the satellite option
			//			item.setChecked(true);
			//			mapView.setSatellite(true);
			//			mapView.invalidate();
			//			return true;
			//		case R.id.helpItem:
			//			ui.showHelpScreen();
			//			return true;
			//		case R.id.reportItem:
			//			ui.showReportDialog();
			//			return true;
			//		case R.id.searchItem:
			//			ui.showSearchDialog();
			//			return true;
			//		case R.id.clearMapItem:
			//			// clear the search markers from the map. the user marker will always stay
			//			List<Overlay> listOfOverlays = mapView.getOverlays();
			//			listOfOverlays.remove(searchOverlay);
			//			listOfOverlays.remove(mapStationsMarkers);
			//			mapView.invalidate();
			//			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * try to update the user location to be the last known location known to the device
	 */
	private void findLocation() {
		try{
			Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			model.getUser().setLastKnownLatitude(lastKnownLocation.getLatitude());
			model.getUser().setLastKnownLongitude(lastKnownLocation.getLongitude());
		} catch (Exception e) {
		}
	}

	/**
	 * fly to the last known location on the map and show the user marker
	 */
	private void showCurrentLocation() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {

					if (mapView != null) {
						// the last location
						GeoPoint location = locationToGeo(model.getUser().getLastKnownLatitude(), model.getUser().getLastKnownLongitude());
						mapControl = mapView.getController();
						mapControl.animateTo(location);
						// paint the marker
						userOverlay.setPoint(location);
						List<Overlay> listOfOverlays = mapView.getOverlays();
						listOfOverlays.remove(userOverlay);
						listOfOverlays.add(userOverlay); 
						mapView.invalidate();
					}
					else {
						// the last location
						IGeoPoint location = locationToIGeo(model.getUser().getLastKnownLatitude(), model.getUser().getLastKnownLongitude());
						//osmapController = osmapView.getController();
						osmapController.animateTo(location);
						// paint the marker
						osuserOverlay.setPoint(location);
						List<org.osmdroid.views.overlay.Overlay> listOfOverlays = osmapView.getOverlays();
						listOfOverlays.remove(osuserOverlay);
						listOfOverlays.add(osuserOverlay); 
						osmapView.invalidate();
					}

				} catch(Exception e) {
					ui.displayMessage("Problem getting GPS info");
				}
			}
		});
	}

	private void showLocation(final double latitude, final double longitude) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					if (mapView != null) {
						// the last location
						GeoPoint location = locationToGeo(latitude, longitude);
						mapControl.animateTo(location);
						mapView.invalidate();
					}
					else {
						// the last location
						IGeoPoint location = locationToIGeo(latitude, longitude);
						//osmapController = osmapView.getController();
						osmapController.animateTo(location);
						// paint the marker
						osmapView.postInvalidate();
					}
				} catch(Exception e) {
					ui.displayMessage("Problem getting GPS info");
				}
			}
		});
	}

	public void clearGoogleMapMarkers(){
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				//				List<Overlay> listOfOverlays = mapView.getOverlays();
				//				listOfOverlays.remove(mapStationsMarkers);
				mapStationsMarkers.clear();
				mapStationsMarkers = new MyMapMarkers(red,MainActivity.this);
				mapStationsMarkers.setUi(ui);
				//				mapView.invalidate();
			}
		});
	}

	/**
	 * listening to Key events
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int current_layout = ui.getCurrentViewId();

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// switching on the running layout
			switch (current_layout) {
			case R.layout.settings_screen:
				// returning to the map view with its last state
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mapView != null) {
							ui.showMapScreen(mapView);
						}
						else {
							ui.showOSMapScreen();
						}
					}
				});
				break;
			case R.layout.login_screen:
				finish();
				break;
			default:
				ui.showExitDialog();
				break;
			}
			return true;
		case KeyEvent.KEYCODE_MENU:
			// enabling the MENU key only in map view
			if ((current_layout == R.layout.osmap_screen) || (current_layout == R.layout.map_screen)){
				return super.onKeyDown(keyCode, event);
			}
			return true;
		default: 
			return super.onKeyDown(keyCode, event);
		}
	}


	/**
	 * update the user local database if the user logged in successfully
	 */
	public void updateUserTable(){
		try{
			riderdb.open();
			User user = model.getUser();
			riderdb.deleteTable(DBAdapter.Tabels.USERS.toString());
			riderdb.insertNewUser(user.getEmail(), user.getPassword(),user.getUserID());
			riderdb.close();
		} catch (Exception e){
		}
	}

	/**
	 * update the user local database if the user logged in successfully
	 */
	public void updateLinesTable(){
		try {
			riderdb.open();
			riderdb.deleteTable(DBAdapter.Tabels.LINES.toString());
			for(int i = 0; i < model.getLines().size(); i++){
				riderdb.insertNewLine(model.getLines().get(i));
			}
			riderdb.close();
		} catch (Exception e){
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		super.finish();
		try {
			notificationManager.cancel(RIDER_NOTFICATION_ID);
			locationManager.removeUpdates(myLocationListener);
			this.unregisterReceiver(internetReceiver);
		} catch (Exception e){
			// some of the recievers are not register yet
		}
		
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (key.equals("updateLines")) {
			progressDialog.setMessage("Updating bus lines...");
			progressDialog.show();
			proxy.updateLinesRequestToServer(model.getUser().getUserID());
		}
	}

}