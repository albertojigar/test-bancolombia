package com.bancolombia.co.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;

import au.com.bytecode.opencsv.CSVReader;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 
 * @author Alberto
 *
 * This class is responsible for parsing the csv file 
 * and populate the GeoDB collection on local DB.
 */
public class PopulateGeoDB {

	public static void main(String[] args) {

		
		Path path = Paths.get("./resources/medellin100k.csv");
		byte[] data;
		try {
			
			data = Files.readAllBytes(path);
			InputStream ISCoor = new ByteArrayInputStream(data);
	    	List<String[]> parserCoordinates = parserCoordinates(ISCoor);

	    	DBObject[] totalGeoRecords = new BasicDBObject[parserCoordinates.size()];
	    	int count = 0;
	    	for(String[] currentData : parserCoordinates){
	            BasicDBObject point1 = new BasicDBObject();
	            point1.append("type", "Point");
	            //Specify coordinates in this order: “longitude, latitude.”
	            BasicDBList coord1 = new BasicDBList();
	            coord1.add(Double.parseDouble(currentData[2]));
	            coord1.add(Double.parseDouble(currentData[1]));
	            point1.append("coordinates", coord1);
	            BasicDBObject geoDocument = new BasicDBObject();
	            geoDocument.append("location", point1);
	            geoDocument.append("address", currentData[0]);
	            
	            totalGeoRecords[count]=geoDocument;
	            count++;
	    	}
	    	/** Insert to MongoDB database **/
	    	MongoUtil mongo = new MongoUtil();
	    	mongo.cleanCollection();
	    	mongo.createCollection();
	    	mongo.createIndex();
	    	mongo.insertGeoCollection(totalGeoRecords);
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}catch (JSONException ex) {
			ex.printStackTrace();
		}
		System.out.println("Populate is Ok");
	}
	
	/**
	 * parserCoordinates - parser the csv file to List<String[]>
	 * @param ISCoor
	 * @return
	 * @throws IOException
	 */
	public static List<String[]> parserCoordinates(InputStream ISCoor) throws IOException{
		List<String[]> parserCoor = new ArrayList<String[]>();
		CSVReader reader = null;
		String seperatedBy = "COMMA";
        try {
            if (seperatedBy.equals("TAB")) {
                reader = new CSVReader(new InputStreamReader(ISCoor, "UTF-8"), '\t');
            } else if (seperatedBy.equals("HASH")) {
                reader = new CSVReader(new InputStreamReader(ISCoor, "UTF-8"), '#');
            } else if (seperatedBy.equals("SLASH")) {
                reader = new CSVReader(new InputStreamReader(ISCoor, "UTF-8"), '/');
            } else if (seperatedBy.equals("BACK SLASH")) {
                reader = new CSVReader(new InputStreamReader(ISCoor, "UTF-8"), '\\');
            } else if (seperatedBy.equals("COMMA")) {
                reader = new CSVReader(new InputStreamReader(ISCoor, "UTF-8"), ',');
            } else if (seperatedBy.equals("ASTERISK")) {
                reader = new CSVReader(new InputStreamReader(ISCoor, "UTF-8"), '*');
            } else if (seperatedBy.equals("PIPE")) {
                reader = new CSVReader(new InputStreamReader(ISCoor, "UTF-8"), '|');
            } else {
                reader = new CSVReader(new InputStreamReader(ISCoor, "UTF-8"), ',');
            }
            
            String[] coorLine;
            while ((coorLine = reader.readNext()) != null) {
            	parserCoor.add(coorLine);
            }
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FindNearestAddress.class.getName()).log(Level.SEVERE, null, ex);
        }
        return parserCoor;
	}

}
