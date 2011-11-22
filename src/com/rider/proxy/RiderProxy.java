package com.rider.proxy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.rider.json.ResultMarshaller;
import com.rider.json.ResultMarshallerJson;
import com.rider.model.ServerResult;

/**
 * @author Oren Levitzky
 */
public class RiderProxy { 

	public static final String LINE_RESOURCE = "&resource=lineRS";
	public static final String CHECKIN_RESOURCE = "&resource=checkInRS";
	public static final String NAVIGATE_RESOURCE = "&resource=navigationRS";
	public static final String LOGIN_REQUEST_RESOURCE = "&resource=loginRS";
	public static final String UPDATE_LINE_RESOURCE = "&resource=updateLinesRS";
	public static final String ERROR_APP_REPORT_RESOURCE = "&resource=SendErrorRS";
	
	
	// need to have ? to start parameters
//	private String baseUrl = "http://riderserver.appspot.com/ridergoogle?userAgent=mobile";
	private String baseUrl = "http://riderserverdev.appspot.com/ridergoogle?userAgent=mobile";
	
	// A strategy for unmarshaling the results
	private ResultMarshaller marshaller;
	// a listener for the results coming back from the server
	private RiderProxyListener proxyListener;
	private HttpClient client;

	/**
	 * Constructs a GameControllerProxy
	 */
	public RiderProxy() {
		marshaller = new ResultMarshallerJson();
		client = new DefaultHttpClient();
	}

	/**
	 * sets the listener for the results coming back from the server
	 * @param proxyListener
	 */
	public void setListener(RiderProxyListener proxyListener) {
		this.proxyListener = proxyListener;
	}

	/**
	 * sends a request to the server for logging in or for registration
	 * @param email - the user email address
	 * @param password - the user password address
	 * @param isRegisterRequest - true if the request is to register (new user)
	 */
	public void loginAndRegisterRequestToServer(final String email,final String password, boolean isRegisterRequest){
		// what about ? to start parameters
		final String url = baseUrl + LOGIN_REQUEST_RESOURCE + "&emailAddress=" + email + "&password=" + 
		password + "&registration=" + isRegisterRequest;
		Runnable task = new Runnable() {
			public void run() {
				try {
					ServerResult result = sendRequest(url,LOGIN_REQUEST_RESOURCE);
					if (proxyListener != null) {
						proxyListener.onLoginRegisterResultFromServer(result,email,password);
					}
				} catch (Exception e) {
					// when there is a problem connecting to the server
					notifyFailure(e.toString());
				}
			}
		};
		new Thread(task).start();
	}


	public void locationUpdateToServer(double latitude,double longitude, int userID, int transportID){
		final String url = baseUrl + "&latitude=" + latitude + "&longitude=" + longitude +
		"&userID=" + userID + "&transportID=" + transportID;
		Runnable task = new Runnable() {
			public void run() {
				try {
					//					String result = sendRequest(url);
				} catch (Exception e) {
					// when there is a problem connecting to the server
					notifyFailure(e.toString());
				}
			}
		};
		new Thread(task).start();
	}

	/**
	 * sends a request of navigation to the server and handle the result (showing the details on map)
	 * @param sourceLatitude/sourceLongitude - the source point
	 * @param destLatitude/destLongitude - the destination point
	 * @param userID - the userId given from the server at startup
	 * @param transportID
	 */
	public void navigationRequestToServer(double sourceLatitude,double sourceLongitude,
			double destLatitude,double destLongitude, int userID, int transportID){

		final String url = baseUrl + NAVIGATE_RESOURCE + "&sourceLat=" + sourceLatitude + "&sourceLong=" + sourceLongitude
		+ "&destLat=" + destLatitude + "&destLong=" + destLongitude 
		+ "&userID=" + userID + "&transportationID=" + transportID;
		Runnable task = new Runnable() {
			public void run() {
				try {
					ServerResult result = sendRequest(url,NAVIGATE_RESOURCE);
					
					if (result.getStatusResponse() == false) {
						throw new Exception("Server returned false on Navigation request. Server side");
					}
					
					if (proxyListener != null) {
						proxyListener.onNavigationResultFromServer(result);
					}
				} catch (Exception e) {
					// when there is a problem connecting to the server
					notifyFailure(e.toString());
				}
			}
		};
		new Thread(task).start();
	}

	/**
	 * sends the check in details to the server
	 * @param line - the line which the user is riding on
	 * @param userID - the id of the user
	 */
	public void checkInRequestToServer(int line,int userID){

		final String url = baseUrl + CHECKIN_RESOURCE + "&line=" + line + "&userID=" + userID;
		Runnable task = new Runnable() {
			public void run() {
				try {
					ServerResult result = sendRequest(url,CHECKIN_RESOURCE);
					if (proxyListener != null) {
						proxyListener.onCheckinResultFromServer(result);
					}
				} catch (Exception e) {
					// when there is a problem connecting to the server
					notifyFailure(e.toString());
				}
			}
		};
		new Thread(task).start();
	}

	/**
	 * send the line request from of the user to the server
	 * @param line - the line the user wants to ride on
	 * @param userID - the user id
	 * @param latitude/longitude - the last known location of the user
	 */
	public void LineRequestToServer(int line,int userID,double latitude,double longitude){

		final String url = baseUrl + LINE_RESOURCE  + "&line=" + line + "&userID=" + userID 
		+ "&lat=" + latitude + "&long=" + longitude ;
		Runnable task = new Runnable() {
			public void run() {
				try {
					ServerResult result = sendRequest(url,LINE_RESOURCE);
					
					if (result.getStatusResponse() == false) {
						throw new Exception("Server returned false on Line request. Server side");
					}
					
					if (proxyListener != null) {
						proxyListener.onLineResultFromServer(result);
					}
				} catch (Exception e) {
					// when there is a problem connecting to the server
					notifyFailure(e.toString());
				}
			}
		};
		new Thread(task).start();
	}

	
	
	/**
	 * request for updating the lines number
	 * @param userID 
	 */
	public void updateLinesRequestToServer(int userID){

		final String url = baseUrl + UPDATE_LINE_RESOURCE + "&userID=" + userID;
		Runnable task = new Runnable() {
			public void run() {
				try {
					ServerResult result = sendRequest(url,UPDATE_LINE_RESOURCE);
					if (proxyListener != null) {
						proxyListener.onUpdateLinesResultFromServer(result.getLines());
					}
				} catch (Exception e) {
					// when there is a problem connecting to the server
					notifyFailure(e.toString());
				}
			}
		};
		new Thread(task).start();
	}
	

	/**
	 * reporting the exception that the app caught
	 * @param userID 
	 */
	public void errorAppReportToServer(int userID, String errorMessage){

		String messageEncoded;
		try {
			messageEncoded = URLEncoder.encode(errorMessage,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			messageEncoded = "ErrorCouldntBeEncoded";
		}
		
		final String url = baseUrl + ERROR_APP_REPORT_RESOURCE + "&userID=" + userID + "&Exception=" + messageEncoded;
		Runnable task = new Runnable() {
			public void run() {
				try {
					ServerResult result = sendRequest(url,ERROR_APP_REPORT_RESOURCE);
					if (proxyListener != null) {
						proxyListener.onErrorAppReportResultFromServer();
					}
				} catch (Exception e) {
					// when there is a problem connecting to the server
					notifyFailure(e.toString());
				}
			}
		};
		new Thread(task).start();
	}
	
	
	/**
	 * Notifies the listener (if one exists) about a failure in performing a certain request
	 * @param message - the error(Exception) message to print 
	 */
	private void notifyFailure(String message) {
		if (proxyListener != null) {
			proxyListener.onErrorConnection(message);
		}
	} 


	// Sends a given request (encoded as a url) to the server and returns the result.
	// Note: this method is synchronous - it blocks until the results is read or an error on the socket occurred
	private ServerResult sendRequest(String url, String resource) throws Exception {
		HttpGet request = new HttpGet(URI.create(url));
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException("Failed to perform request: " + response.getStatusLine());
		}
//		DataInputStream input = new DataInputStream(response.getEntity().getContent());
//		EntityUtils.toString(response.getEntity(),"UTF-8");
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		return marshaller.unmarshal(reader,resource);
	}
}
