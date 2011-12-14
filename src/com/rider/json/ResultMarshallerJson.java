package com.rider.json;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;

import com.rider.model.Coordinates;
import com.rider.model.Line;
import com.rider.model.ServerResult;
import com.rider.model.Station;
import com.rider.proxy.RiderProxy;

/**
 * A ResultMarshaller that marshal result objects using a binary format
 *
 * @author Oren Levitzky 
 */
public class ResultMarshallerJson implements ResultMarshaller {

	private static final String LATITUDE = "lat";
	private static final String LONGITUDE = "long";
	private static final String TIME = "time";
	private static final String COMPANY_LINE = "Company";
	private static final String COMPANY_NAV = "company";
	private static final String NUM_OF_STATIONS = "NumberOfStations";
	private static final String NUM_OF_LINES = "NumberOfLines";
	private static final String STATION_NAME = "stationName";
	private static final String LINE_NUMBER_LINE = "lineNumber";
	private static final String LINE_NUMBER_NAV = "line";
	private static final String LINE_NUMBER_UPDATELINES = "Line";
	private static final String LINE_ID_UPDATELINES = "LineDBID";
	private static final String STATION_TYPE = "stationType";
	private static final String CLOSEST_STATION_INDEX = "closestStationIndex";
	private static final String RESPONSE = "response";
	private static final String LOGIN_STATUS = "loginStatus";
	private static final String USER_ID = "userID";
	private static final String TIME_CLOSEST_STATION_LINE = "timeForClosestStation";
	
	/**
	 * Reads from an input stream a MoveRequest object that was written using the marshal(OutputStream) method
	 * @throws Exception 
	 */
	public ServerResult unmarshal(BufferedReader input,String resource) throws IOException {
		String jsonString;
		//JSONArray json = null;
		JSONObject json = null;
		jsonString = input.readLine();
		json = new JSONObject(jsonString.substring(2, jsonString.length()));
		return resolveJson(json,resource);
	}

	/**
	 * Resolve from JSONOBject to a String object.
	 * @param json - the JSONObject to resolve
	 * @return String - the String object that resloved from the json object.
	 * @throws JSONException
	 */
	protected ServerResult resolveJson(JSONObject json, String resource) throws JSONException {

		ServerResult result = new ServerResult();
 

		if (resource.equals(RiderProxy.LINE_RESOURCE)) {
			result.setStatusResponse("true".equals(json.getString(RESPONSE).toLowerCase()));
			// only when the status is true there will be lines to grab
			if (result.getStatusResponse()) {
				String company = json.getString(COMPANY_LINE);
				result.setClosestStationIndex(json.getString(CLOSEST_STATION_INDEX));
				for(int i=0 ; i < Integer.parseInt(json.getString(NUM_OF_STATIONS)) ; i++) {
					if (i == result.getClosestStationIndex()){
						result.getStations().add(new Station(json.getString(STATION_NAME + i), 
								json.getString(LATITUDE + i), 
								json.getString(LONGITUDE + i),
								json.getString(LINE_NUMBER_LINE),
								company,
								json.getString(TIME_CLOSEST_STATION_LINE),
								json.getString(STATION_TYPE + i)));
					}
					else{
						result.getStations().add(new Station(json.getString(STATION_NAME + i), 
								json.getString(LATITUDE + i), 
								json.getString(LONGITUDE + i), 
								json.getString(LINE_NUMBER_LINE),
								company,
								json.getString(STATION_TYPE + i)));
					}
				}
			}


		} 
		else if (resource.equals(RiderProxy.CHECKIN_RESOURCE)) {
			result.setCheckinResult("true".equals(json.getString(RESPONSE).toLowerCase()));
		}
		else if (resource.equals(RiderProxy.NAVIGATE_RESOURCE)) {
			result.setStatusResponse("true".equals(json.getString(RESPONSE).toLowerCase()));
			// only when the status is true there will be lines to grab
			if (result.getStatusResponse()) {
				for(int i=0 ; i < Integer.parseInt(json.getString(NUM_OF_STATIONS)) ; i++) {
					result.getStations().add(new Station(json.getString(STATION_NAME + i), 
							json.getString(LATITUDE + i), 
							json.getString(LONGITUDE + i),
							json.getString(LINE_NUMBER_NAV + i),
							json.getString(COMPANY_NAV + i),
							json.getString(TIME + i),
							json.getString(STATION_TYPE + i)));
				}
			}
		} 
		else if (resource.equals(RiderProxy.LOGIN_REQUEST_RESOURCE)) {
			result.setLoginStatus("true".equals(json.getString(LOGIN_STATUS).toLowerCase()));
			if (result.isLoginStatus()) {
				result.setUserID(Integer.parseInt(json.getString(USER_ID)));
			}
		} 
		else if(resource.equals(RiderProxy.UPDATE_LINE_RESOURCE)) {
			for(int i=0 ; i < Integer.parseInt(json.getString(NUM_OF_LINES)) ; i++) {
				String lineNumber = json.getString(LINE_NUMBER_UPDATELINES + i);
				String lineID = json.getString(LINE_ID_UPDATELINES + i);
				System.out.println("Line id = " + lineID + ", Line number = " + lineNumber);
				result.getLines().add(new Line(lineNumber, lineID));
			}
		}

		return result;

	}

}
