package com.rider.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.opengl.Visibility;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.rider.R;
import com.rider.UiListener;
import com.rider.model.RiderModel;

/**
 * @author Oren Levitzky 
 */
public class RiderUi {

	// the map screen layout with status bar
	FrameLayout mapLayout;
	// a listener to users requests
	private UiListener listener;
	// The id of the screen currently displayed.
	private int currentViewId;
	// A limited scope reference to the platform context of the UI which allows it to change screens
	private ViewOwner viewOwner;
	private RiderModel model;
	private boolean googleMap;

	public RiderUi(ViewOwner viewOwner) {
		this.viewOwner = viewOwner;
	}

	public void setGoogleMap(boolean googleMap) {
		this.googleMap = googleMap;
	}
	
	/**
	 * sets the screen view of the application according to the given screen id.
	 * @param screenId - the given screen id
	 * @return - true if the screen was set succefuly
	 */
	private boolean setCurrentScreenView(int screenId) {
		if (currentViewId == screenId) {
			return false;
		}
		viewOwner.setContentView(screenId);
		this.currentViewId = screenId;
		return true;
	}

	/**
	 * @return - the currently shown layout id
	 */
	public int getCurrentViewId() {
		return currentViewId;
	}
	/**
	 * set the listener for the user requests from the UI
	 * @param listener - the listener to set
	 */
	public void setListener(UiListener listener) {
		this.listener = listener;
	}

	public void setModel(RiderModel model){
		this.model = model;
	}

	/**
	 * returns the activity of the application using the view owner interface.
	 * @return
	 */
	public Activity getActivity(){
		return viewOwner.getActivity();
	}

	/**
	 * Displays the opening screen
	 */
	public void showOpeningScreen() {
		if (setCurrentScreenView(R.layout.startup_screen)) {
			this.listener.onAppLoadScreen();
		}
	}

	/**
	 * Displays the login and registration screen 
	 */
	public void showLoginScreen(){
		if (setCurrentScreenView(R.layout.login_screen)) {

			final RelativeLayout layout =(RelativeLayout) viewOwner.findViewById(R.id.loginScreenLayout);
			final EditText email = (EditText) viewOwner.findViewById(R.id.email);
			final EditText password = (EditText) viewOwner.findViewById(R.id.password);
			final Button logInButton = (Button) viewOwner.findViewById(R.id.loginButton);
			final Button registerButton = (Button) viewOwner.findViewById(R.id.registerButton);

			logInButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onLoginRequest(email.getText().toString(), password.getText().toString(),false);
				}
			});


			registerButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onLoginRequest(email.getText().toString(), password.getText().toString(),true);
				}
			});

			email.setText(model.getUser().getEmail());
			password.setText(model.getUser().getPassword());
		}
	}

	/**
	 * Displays the Google Map screen
	 * @param mapView - the current map view in the app
	 */
	public void showMapScreen(MapView mapView) {
		// if there is no map running on the activity
		if (mapView == null) {
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.map_screen, (ViewGroup) getActivity().findViewById(R.id.map_screen)); 
			activeBarEditing(layout, true);
			mapView = new MapView(getActivity(), getActivity().getString(R.string.APIMapKey));
			mapView.setClickable(true);
			mapView.setEnabled(true);

			this.mapLayout = (FrameLayout) layout;
			this.mapLayout.addView(mapView,0);
			viewOwner.setContentView(this.mapLayout);
			this.currentViewId = R.layout.map_screen;
			this.listener.onLoadGoogleMapRequest(mapView);
		} 
		// if there is a map already running just load it with no changes
		else {
			viewOwner.setContentView(this.mapLayout);
			this.currentViewId = R.layout.map_screen;
			this.listener.onBackToMapRequest();
		}


	} 

	
	/**
	 * Displays the openStreetMap screen
	 * @param mapView - the current map view in the app
	 */
	public void showOSMapScreen() {
		if (setCurrentScreenView(R.layout.osmap_screen)) {
			 org.osmdroid.views.MapView osmapView = (org.osmdroid.views.MapView) getActivity().findViewById(R.id.osmapview);
			 activeBarEditing(null, false);
		     this.listener.onLoadOSMapRequest(osmapView);
		}
	} 
	
	/**
	 * listen to the activebar
	 * @param layout
	 */
	private void activeBarEditing(View layout, boolean gooleMap) {
		final ImageView internet;
		final ImageView gps;
		final ImageView exit;
		final ImageView info;
		
		if (gooleMap) {
			internet = (ImageView) layout.findViewById(R.id.internetButtonBarGoogle);
			gps = (ImageView) layout.findViewById(R.id.connectionButtonBarGoogle);
			exit = (ImageView) layout.findViewById(R.id.exitButtonBarGoogle);
			info = (ImageView) layout.findViewById(R.id.infoButtonBarGoogle);
		}
		else{
			internet = (ImageView) viewOwner.findViewById(R.id.internetButtonBarOS);
			gps = (ImageView) viewOwner.findViewById(R.id.connectionButtonBarOS);
			exit = (ImageView) viewOwner.findViewById(R.id.exitButtonBarOS);
			info = (ImageView) viewOwner.findViewById(R.id.infoButtonBarOS);
		}

		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showExitDialog();
			}
		});

		info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showInfoScreen();
			}
		});
	}
	
	public void setBarTextHeader(final String text){
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (RiderUi.this.currentViewId == R.layout.map_screen){
					final Button header = (Button) viewOwner.findViewById(R.id.textHeaderGoogle);
					header.setText(getActivity().getResources().getString(R.string.linePrefixTopBar) + " " + text);
				}
			}
		});
	}

	public void setCurrentLocationStatus(final boolean enable){
		final TextView status = (TextView) viewOwner.findViewById(R.id.currentLocationStatus);
		if (enable) {
			status.setEnabled(true);
			status.setVisibility(View.VISIBLE);
		}
		else {
			status.setEnabled(false);
			status.setVisibility(View.INVISIBLE);
		}
	}
	
	public void turnOnGPSNotification() {
		final ImageView gps;
		if (googleMap) {
			gps = (ImageView) mapLayout.findViewById(R.id.connectionButtonBarGoogle);
		}
		else {
			gps = (ImageView) viewOwner.findViewById(R.id.connectionButtonBarOS);
		}
		
		gps.setImageResource(R.drawable.connection_on);
	}

	public void turnOffGPSNotification() {
		final ImageView gps;
		if (googleMap) {
			gps = (ImageView) mapLayout.findViewById(R.id.connectionButtonBarGoogle);
		}
		else {
			gps = (ImageView) viewOwner.findViewById(R.id.connectionButtonBarOS);
		}
		
		gps.setImageResource(R.drawable.connection_off);
		showConnectivityErrorDialog(true);
	}

	public void turnOnInternetNotification() {
		final ImageView internet;
		if (googleMap) {
			internet = (ImageView) mapLayout.findViewById(R.id.internetButtonBarGoogle);
		}
		else {
			internet = (ImageView) viewOwner.findViewById(R.id.internetButtonBarOS);
		}
		
		internet.setImageResource(R.drawable.earth_on);
	}

	public void turnOffInternetNotification() {
		final ImageView internet;
		if (googleMap) {
			internet = (ImageView) mapLayout.findViewById(R.id.internetButtonBarGoogle);
		}
		else {
			internet = (ImageView) viewOwner.findViewById(R.id.internetButtonBarOS);
		}
		
		internet.setImageResource(R.drawable.earth_off);
		showConnectivityErrorDialog(false);
	}
	
	
	/**
	 * Displays the help screen
	 */
	public void showInfoScreen() {
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.info_screen, (ViewGroup) getActivity().findViewById(R.id.infoLayout));
		
		final Button cancelButton = (Button) layout.findViewById(R.id.cancelButtonInfo);
		final TextView text = (TextView) layout.findViewById(R.id.infoText);
		text.setTextColor(Color.BLACK);
		text.setText(
					Html.fromHtml(
			                "<b>Rider Ltd.</b> " +
			                "<br/>" +
			                "<br/>" +
			                "Social public transportation navigation" +
			                "<br/>" +
			                "Version 1.000 Build 10" +
			                "<br/>" +
			                "<br/>" +
			                "<a href=\"http://www.thesocialrider.com\">www.thesocialrider.com</a>"));
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		
		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}

	/**
	 * show the search dialog to the user and forward it to be converted to coordinates
	 */
	public void showSearchDialog(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		final EditText searchInput = new EditText(getActivity());
		searchInput.setWidth(100);
		alert.setView(searchInput);
		alert.setPositiveButton(R.string.send_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String address = searchInput.getText().toString().trim();
				listener.onSearchAddressRequest(address);
			}
		});

		alert.setNegativeButton(R.string.cancel_button,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	/**
	 * shows lines numbers to choose from.
	 */
	public void showLinesChooser(final boolean isCheckInRequest){
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.line_popup, (ViewGroup) getActivity().findViewById(R.id.line_layout)); 

		// setting the values for the spinner
		final Spinner linesSpiner = (Spinner) layout.findViewById(R.id.linesSpinner);
		String[] lines = model.getLinesAsArray();
		final String[] linesID = model.getLinesIDAsArray();
		ArrayAdapter<CharSequence> m_adapterForSpinner = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item,lines);
		m_adapterForSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		linesSpiner.setAdapter(m_adapterForSpinner);

		// listening to the buttons
		final Button okButton = (Button) layout.findViewById(R.id.okButtonLine);
		final Button cancelButton = (Button) layout.findViewById(R.id.cancelButtonLine);
		final ImageView mainImage = (ImageView) layout.findViewById(R.id.imageLinePopup);
		
		if (isCheckInRequest){
			mainImage.setImageResource(R.drawable.checkin_popup);
		}
		else{
			mainImage.setImageResource(R.drawable.bus_line_popup_with_bar);
		}

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listener.onBusNumberChooser((String) linesSpiner.getSelectedItem(),linesID[linesSpiner.getSelectedItemPosition()], isCheckInRequest);
				dialog.dismiss();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}

	/**
	 * shows the navigation pop-up to fill. sends the details to the server
	 */
	public void showNavigationPopup(){
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.navigate_popup, (ViewGroup) getActivity().findViewById(R.id.navigate_layout)); 

		final EditText sourceInput = (EditText) layout.findViewById(R.id.source_editext); 
		final EditText destInput = (EditText) layout.findViewById(R.id.destionation_editext); 

		final Button okButton = (Button) layout.findViewById(R.id.okButtonNav);
		final Button cancelButton = (Button) layout.findViewById(R.id.cancelButtonNav);

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.onNavigateRequest(sourceInput.getText().toString(), destInput.getText().toString());
				dialog.dismiss();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}

	/**
	 * show the exit dialog for exiting the app
	 */
	public void showExitDialog(){
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.exit_popup, (ViewGroup) getActivity().findViewById(R.id.exitLayout)); 

		final Button okButton = (Button) layout.findViewById(R.id.okButton);
		final Button cancelButton = (Button) layout.findViewById(R.id.cancelButton);

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.onExitRequest();
				dialog.dismiss();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}

	public void showWakeUpDialog() {
		//Preparing views
//		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View layout = inflater.inflate(R.layout.wakeup_screen, (ViewGroup) getActivity().findViewById(R.id.wakeup_layout)); 
//
//		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//		alert.setView(layout);
//		alert.setPositiveButton(R.string.send_button, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				listener.onWakeUpRequest();
//			}
//		});
//
//		alert.setNegativeButton(R.string.cancel_button,
//				new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				dialog.cancel();
//			}
//		});
//		alert.show();
	}

	public void showReportDialog() {
		//Preparing views
//		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View layout = inflater.inflate(R.layout.report_screen, (ViewGroup) getActivity().findViewById(R.id.report_layout)); 
//
//		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//		alert.setView(layout);
//		alert.setPositiveButton(R.string.send_button, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				listener.onReportRequest();
//			}
//		});
//
//		alert.setNegativeButton(R.string.cancel_button, 
//				new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				dialog.cancel();
//			}
//		});
//		alert.show();
	}

	
	/**
	 * on login failure
	 */
	public void showNavigationNotFoundDialog() {
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.nav_not_found_popup, (ViewGroup) getActivity().findViewById(R.id.navNotFoundLayout)); 

		final Button cancelButton = (Button) layout.findViewById(R.id.cancelButtonErrorNavNotFound);

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}
	
	
	
	/**
	 * on login failure
	 */
	public void showLoginErrorDialog() {
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.login_failed, (ViewGroup) getActivity().findViewById(R.id.loginFailedLayout)); 

		final Button cancelButton = (Button) layout.findViewById(R.id.cancelButtonErrorLoginFailed);

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}
	
	/**
	 * on login failure
	 */
	public void showConnectivityErrorDialog(boolean gps_error) {
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.connectivity_popup, (ViewGroup) getActivity().findViewById(R.id.connectivityLayout)); 

			
		final Button cancelButton = (Button) layout.findViewById(R.id.cancelButtonConnectivity);
		final ImageView image = (ImageView) layout.findViewById(R.id.connectivityImage);
		
		if (gps_error){
			image.setImageResource(R.drawable.error_no_sat);
		}
		else {
			image.setImageResource(R.drawable.error_no_network);
		}
		
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}

	/**
	 * show the exit dialog for exiting the app
	 */
	public void showErrorDialog(final String errorMessage){
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.error_popup, (ViewGroup) getActivity().findViewById(R.id.errorLayout)); 

		final Button sendButton = (Button) layout.findViewById(R.id.sendButtonError);
		final Button cancelButton = (Button) layout.findViewById(R.id.cancelButtonError);

		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.onErrorRequest(errorMessage);
				dialog.dismiss();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}

	
	/**
	 * show the exit dialog for exiting the app
	 */
	public void showNavAddressErrorDialog(){
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.nav_error_address, (ViewGroup) getActivity().findViewById(R.id.navAddressFailedLayout)); 

		final Button cancelButton = (Button) layout.findViewById(R.id.cancelButtonErrorNavAddressFailed);

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}
	
	
	
	/**
	 * show the exit dialog for exiting the app
	 * @param stationTitle 
	 */
	public void showBusDialog(String stationTitle){
		final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Light_Panel);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.bus_popup, (ViewGroup) getActivity().findViewById(R.id.busLayout)); 

		final Button okButton = (Button) layout.findViewById(R.id.okButtonBus);
		final TextView stationName = (TextView) layout.findViewById(R.id.stationNameText);

		stationName.setText(stationTitle);

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		// showing the dialog
		dialog.setContentView(layout);
		dialog.show();
	}

	/**
	 * displays a message telling the user when it is his turn and when it is not 
	 * @param context - the context which hold the message (as Toast)
	 * @param message - the message to display
	 */
	public void displayMessage(final String message){
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
			}
		});
	}
}