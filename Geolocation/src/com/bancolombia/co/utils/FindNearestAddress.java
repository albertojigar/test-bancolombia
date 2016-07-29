package com.bancolombia.co.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

/**
 * 
 * @author Alberto
 *
 * This class is responsible to find the nearest address into all coordinates. 
 */
public class FindNearestAddress {
	
	// variables
	public static double longitude;
	public static double latitude;
	 
	/**
	 * Main method
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter the Address: ");
			String s = br.readLine();

			if(s!=null && !s.isEmpty()){
				String testAddress = s.replace(" ", "+");

				// Test Address:
				//"CL 52a 33 2" - Result - CL 51 32 21,6.24301520672,-75.55393453712
				//"Calle 45a #77-1" - Result - CL 45 77 51 APT 201,6.25114910327,-75.59628939298

				/** Using Google services **/
				GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyANtnE6bbBwWwjyNR_5iX8QVOdnnGV-Mg0");
				GeocodingResult[] results = GeocodingApi.geocode(context,
						"Medellín,+Antioquia"+testAddress).await();

				latitude = results[0].geometry.location.lat;
				longitude = results[0].geometry.location.lng;
				if(latitude==6.253040800000001 && longitude==-75.5645737){
					System.out.println("Not is a correct address");
				}else{
					System.out.println("Latitude: "+latitude);
					System.out.println("Longitude: "+longitude);
					MongoUtil mongo = new MongoUtil();
					System.out.println("NEAREST ADDRESS: "+mongo.findNearCoordinate(longitude,latitude));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
