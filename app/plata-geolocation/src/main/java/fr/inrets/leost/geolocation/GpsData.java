package fr.inrets.leost.geolocation;

import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * GpsData data of a Gps
 * 
 * @has - - - WGS84
 */
public  class GpsData{
	
	private Double t;
	private WGS84 position;
	private Double speed;
	private Double track;

	public GpsData(Double t,WGS84 position, Double speed, Double track) {
		super();
		this.t = t;
		this.position = position;
		this.speed = speed;
		this.track = track;
	}	
	
	/**
	 * convert a speed in knot unit to SI unit
	 * @param speed in knot
	 * @return speed in meter per second
	 */
	/*static private Double knotToSI(Double speed){
		return new Double (0.514444 * speed.doubleValue());
	}*/
	
	static private Double checkDoubleNull(Double val){
		if (val == null)
			return new Double(0.0);
		
		return val;
	}
	
	/**
	 * decode a json gps data.
	 * @param str json string
	 * @return the geographical position in WGS84 format
	 */
	static public GpsData decodeGPSDataJson(String str){

		try{
			//convert JSON string in Java Map
			Map dict=(Map)(new JSONParser()).parse(str);
			
			//TPV class for get position, speed and track (see gpsd spec)
			String fClass = (String)dict.get("class");
			if (fClass.compareToIgnoreCase("TPV") == 0){
				
				//get the geographical position
				Double lat   = checkDoubleNull((Double)dict.get("lat"));
				Double lon   = checkDoubleNull((Double)dict.get("lon"));		
				Double alt   = checkDoubleNull((Double)dict.get("alt"));	
				Double speed = checkDoubleNull((Double)dict.get("speed"));		
				Double track = checkDoubleNull((Double)dict.get("track"));	
				Double t = checkDoubleNull((Double)dict.get("time"));	
				
				
				return new GpsData(t,new WGS84(lon,lat,alt),speed,track);
			}
		}
		catch(ParseException pe){
			System.err.println("JSon parsing error: " + pe.getPosition());
			System.err.println(pe);
		}
		
		
		return null;
	}	
	
	/** current position in WGC84 format */
	public WGS84 getPosition() {
		return position;
	}
	
	/** current speed in meter per second */
	public Double getSpeed() {
		return speed;
	}
	
	/** current orientation in degree (0 to 360) */
	public Double getTrack() {
		return track;
	}

	/**
	 * @return the t
	 */
	public Double getTime() {
		return t;
	}	
	
	
	
}