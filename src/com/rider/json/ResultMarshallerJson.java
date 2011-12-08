package com.rider.json;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;

import com.rider.model.Coordinates;
import com.rider.model.ServerResult;
import com.rider.model.Station;
import com.rider.proxy.RiderProxy;

/**
 * A ResultMarshaller that marshal result objects using a binary format
 *
 * @author Oren Levitzky 
 */
public class ResultMarshallerJson implements ResultMarshaller {


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
			result.setStatusResponse("true".equals(json.getString("response").toLowerCase()));
			// only when the status is true there will be lines to grab
			if (result.getStatusResponse()) {
				String company = json.getString("Company");
				result.setClosestStationIndex(json.getString("closestStationIndex"));
				for(int i=0 ; i < Integer.parseInt(json.getString("NumberOfStations")) ; i++) {
					if (i == result.getClosestStationIndex()){
						result.getStations().add(new Station(json.getString("stationName" + i), 
								json.getString("lat" + i), 
								json.getString("long" + i),
								json.getString("lineNumber"),
								company,
								json.getString("timeForClosestStation"),
								json.getString("stationType" + i)));
					}
					else{
						result.getStations().add(new Station(json.getString("stationName" + i), 
								json.getString("lat" + i), 
								json.getString("long" + i), 
								json.getString("lineNumber"),
								company,
								json.getString("stationType" + i)));
					}
				}
			}


		} 
		else if (resource.equals(RiderProxy.CHECKIN_RESOURCE)) {
			result.setCheckinResult("true".equals(json.getString("response").toLowerCase()));
		}
		else if (resource.equals(RiderProxy.NAVIGATE_RESOURCE)) {
			result.setStatusResponse("true".equals(json.getString("response").toLowerCase()));
			// only when the status is true there will be lines to grab
			if (result.getStatusResponse()) {
				for(int i=0 ; i < Integer.parseInt(json.getString("NumberOfStations")) ; i++) {
					result.getStations().add(new Station(json.getString("stationName" + i), 
							json.getString("lat" + i), 
							json.getString("long" + i),
							json.getString("line" + i),
							json.getString("company" + i),
							json.getString("time" + i),
							json.getString("stationType" + i)));
				}
			}

			//			result.setSourceStation(new Station(json.getString("sourceStationName"), json.getString("sourceLat"), json.getString("sourceLong"), json.getString("lineNumber")));
			//			result.setDestStation(new Station(json.getString("destStationName"), json.getString("destLat"), json.getString("destLong"), json.getString("lineNumber")));
		} 
		else if (resource.equals(RiderProxy.LOGIN_REQUEST_RESOURCE)) {
			result.setLoginStatus("true".equals(json.getString("loginStatus").toLowerCase()));
			result.setUserID(Integer.parseInt(json.getString("userID")));
		} 
		else if(resource.equals(RiderProxy.UPDATE_LINE_RESOURCE)) {
			//			numOfLines = Integer.parseInt(json.getString("NumberOfLines"));
			for(int i=0 ; i < Integer.parseInt(json.getString("NumberOfLines")) ; i++) {
				String line = json.getString("Line" + i);
				result.getLines().add(line);
			}
		}

		return result;

	}

}
