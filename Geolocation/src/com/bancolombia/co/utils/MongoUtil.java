package com.bancolombia.co.utils;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
/**
 * 
 * @author Alberto
 *
 * Utility Class that contains all the functions of connection 
 * and other methods related to mongodb.
 */
@SuppressWarnings("unused")
public class MongoUtil {

	// variables
	public MongoClient mongo;
	public DB db;
	public DBCollection collection;
	private static final String DB_NAME = "local";
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 27017;
	private static final String COLLECTION_NAME = "geodb";
	
	/**
	 * Connection with the MongoDB database
	 * 
	 */
	public MongoUtil() {
		try
		{
			this.mongo = new MongoClient(HOST, PORT);
			this.db = mongo.getDB(DB_NAME);
		}
		catch (MongoException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Insert the document from csv file parser to GeoJson
	 * @param totalrecords
	 * @return DBCollection 
	 */
	public DBCollection insertGeoCollection(DBObject[] totalrecords){
		collection.insert(totalrecords);
		return collection;
	}
	
	/**
	 * Find the nearest address all the coordinates
	 * 
	 * @param col
	 * @param longitude
	 * @param latitude
	 * @return String Near Direction
	 */
	public String findNearCoordinate(double longitude, double latitude){
		String result = "";
		collection = db.getCollection(COLLECTION_NAME);
		BasicDBObject nearQuery = new BasicDBObject();
		
		//Example mongoQuery = db.geodb.find({location:{ $near :{$geometry: { type: "Point",  coordinates: [ -75.5540337,6.2438366 ] },$maxDistance: 0.01}}})
		
		final BasicDBObject filter1 = new BasicDBObject("type","Point");
		filter1.put("coordinates", new double[] { longitude,latitude });
        filter1.put("$maxDistance", 0.01);
        final BasicDBObject filter2 = new BasicDBObject("$geometry",filter1);
        final BasicDBObject filter3 = new BasicDBObject("$near",filter2);

        final BasicDBObject query = new BasicDBObject("location", filter3);

        List<DBObject> arrResult = collection.find(query).toArray();
        if(arrResult!=null && arrResult.size()>0){
        	DBObject add= arrResult.get(0);
        	result=(String)add.get("address");
        }
		return result;
	}

	/**
	 * The collection is deleted and created again
	 */
	public void cleanCollection(){
		this.collection = db.getCollection(COLLECTION_NAME);
        this.collection.drop();
    }
	
	/**
	 * Created the collection if not exist
	 */
	public void createCollection(){
		if(!db.collectionExists(COLLECTION_NAME))
		{
			this.collection = db.createCollection(COLLECTION_NAME, new BasicDBObject());
		}
    }
	
	/**
	 * Creates the index required for Geospatial query
	 */
	public void createIndex(){
        collection.createIndex(new BasicDBObject("location","2dsphere"));
    }
	
	public void setCollection(String col){
        this.collection = db.getCollection(col);
    }
	
	public DBCollection getCollection(){
        return collection;
    }
	
}
