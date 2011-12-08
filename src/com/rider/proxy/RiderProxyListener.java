package com.rider.proxy;

import java.util.ArrayList;

import com.rider.model.ServerResult;


/**
 * this is an interface which listens to the results coming back from the server and updates
 * the UI according to the results
 * @author Oren Levitzky 
 *
 */
public interface RiderProxyListener {

	/**
	 * @param message - the message to print on the screen on connection problem with the server  
	 */
	public void onErrorConnection(String message);
	public void onLineResultFromServer(ServerResult result);
	public void onCheckinResultFromServer(ServerResult result);
	public void onNavigationResultFromServer(ServerResult result);
	public void onLoginRegisterResultFromServer(ServerResult result, String email, String password);
	public void onUpdateLinesResultFromServer(ArrayList<String> lines);
	public void onErrorAppReportResultFromServer();
	public void onNavigationNotFoundResultFromServer();
}
