package fr.inrets.leost.geolocation;


import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;

import org.json.simple.parser.*;



/**
 * Poll GPS data from gpsd service [1]<br><br>
 * 
 * A class can register as a listener for receive a event when 
 * the  geographical position change <br><br>
 * 
 * The position coordinate use the WGS84 representation 
 * commonly used by the GPS <br><br>
 * 
 * Gpsd --- Json data ---> Gps ---> GeoLocationListener
 * 
 * [1] http://gpsd.berlios.de/
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 */
public class Gps  extends Geolocation  {

	public static final int DEFAULT_UPDATA_INTERVAL = 250;
	public static final int GPSD_PORT = 2947;
	
	/** interval (in ms) between two gpsd request*/
	private int updateInterval;

	/** reader buffer for read the data from gpsd*/
	private BufferedReader gpsBR;
	
	/** current position in WGC84 format */
	private WGS84 currentPos;
	
	/** current speed in meter per second */
	private Double currentSpeed;
	
	/** current orientation in degree (0 to 360) */
	private Double currentTrack;
	

	/** collection of listener for receive a event on position changing*/
	private final Collection<GeolocationListener> gpsListeners = new ArrayList<GeolocationListener>();


	/**
	 * 
	 * configure gpsd session in json data format
	 * @param gpsBW
	 * @throws IOException
	 */
	static private void writeGPSInitJson(BufferedWriter gpsBW) throws IOException {
		gpsBW.write( "?WATCH={\"enable\":true,\"json\":true}\n" );
		gpsBW.flush();		
	}

	/**
	 * Create a socket for the gpsd session. The dest address is localhost and 
	 * the port is {@value #GPSD_PORT}
	 * @return
	 * @throws IOException
	 */
	static private Socket connectToGpsd()throws IOException {
		return  connectToGpsd( InetAddress.getByName("localhost"), GPSD_PORT);	
	}
	
	/**
	 * Create a socket for the gpsd session
	 * @param addr
	 * @param port
	 * @return
	 * @throws IOException
	 */
	static private Socket connectToGpsd(InetAddress addr, int port)throws IOException {
		return new Socket( addr, port);	
	}	
	
	/**
	 * convert a speed in knot unit to SI unit
	 * @param speed in knot
	 * @return speed in meter per second
	 */
	static private Double knotToSI(Double speed){
		return new Double (0.514444 * speed.doubleValue());
	}
	
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
	static private GpsData decodeGPSDataJson(String str){
		
		
		try{
			//convert JSON string in Java Map
			Map dict=(Map)(new JSONParser()).parse(str);
			
			//System.out.println(dict);
			
			//TPV class for get position, speed and track (see gpsd spec)
			String fClass = (String)dict.get("class");
			if (fClass.compareToIgnoreCase("TPV") == 0){
				
				//get the geographical position
				Double lat   = checkDoubleNull((Double)dict.get("lat"));
				Double lon   = checkDoubleNull((Double)dict.get("lon"));		
				Double alt   = checkDoubleNull((Double)dict.get("alt"));	
				Double speed = knotToSI(checkDoubleNull((Double)dict.get("speed")));		
				Double track = checkDoubleNull((Double)dict.get("track"));					
				
				
				return new GpsData(new WGS84(lon,lat,alt),speed,track);
			}
		}
		catch(ParseException pe){
			System.err.println("JSon parsing error: " + pe.getPosition());
			System.err.println(pe);
		}
		
		
		return null;
	}
	

	/**
	 * Connect to gpsd in localhost with 2947  port
	 * @throws IOException
	 */
	public Gps() throws IOException {
		//connection to gpsd
		connectGPS();
		
		//init the variable
		setUpdateInterval(DEFAULT_UPDATA_INTERVAL);
		setCurrentPos(new WGS84());
		setCurrentSpeed(0.0);
		setCurrentTrack(0.0);		

	}
	

	

	/**
	 * Connection to gpsd
	 * @throws IOException
	 */
	private void connectGPS() throws IOException {
		Socket gpsSocket;
		
		//create the socket
		gpsSocket = connectToGpsd();
		
		//get a reader buffer
		gpsBR = new BufferedReader( new 
				InputStreamReader( gpsSocket.getInputStream()) );
		
		//configure the gpsd session in json data
		writeGPSInitJson(new BufferedWriter( new 
				OutputStreamWriter( gpsSocket.getOutputStream() ) ));
		


	}
	
	/**
	 * must be call when the current position change
	 */
	private void positionChanged(){
		//call the method positionChanged of each registered listener
		for (GeolocationListener l : gpsListeners)
			l.positionChanged(getCurrentPos(),getCurrentSpeed(),getCurrentTrack());
		
	}
	
	/**
	 * read a line from gpsd and decode the data
	 */
	public void run() {
		if (gpsBR == null) return;

		
		while(true) {
			// for each line in the buffer
			try {
				//reads a line (passive wait) and decodes
				GpsData data = decodeGPSDataJson(gpsBR.readLine());

				//System.out.println("Read a line : " + data);
				
				//if needed, update the current position
				if(data!= null  && (   !getCurrentPos().equals(data.getPosition())
						|| !getCurrentSpeed().equals(data.getSpeed())
						|| !getCurrentTrack().equals(data.getTrack())))
				{
					setCurrentPos(data.getPosition());
					setCurrentSpeed(data.getSpeed());	
					setCurrentTrack(data.getTrack());						
				}


			} catch(IOException ioe) {
				System.err.println("Connection error with gps service");
			}
		}
		
	}

	/**
	 * get the interval update value of position 
	 * @return interval in ms
	 */
	public int getUpdateInterval() {
		return updateInterval;
	}

	/**
	 * set the interval update value of position  
	 * @param updateInterval interval in ms
	 */
	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}  
	
	/**
	 * get the current position
	 * @return the current position in WGS84 format
	 */
	public WGS84 getCurrentPos() {
		return currentPos;
	}

	/**
	 * set the current position
	 * @param currentPos the current position in WGS84 format
	 */
	private void setCurrentPos(WGS84 currentPos) {
		this.currentPos = currentPos;
		positionChanged();
	}	
	
	/**
	 * register a new listener
	 * @param l
	 */
	public void addPositionListener(GeolocationListener l){
		gpsListeners.add(l);
	}
	
	/**
	 * remove a registered listener
	 * @param l
	 */
	public void removePositionListener(GeolocationListener l){
		gpsListeners.remove(l);
	}
	
	
	/**
	 *  get current speed 
	 *  @return speed in meter per second 
	*/
	public Double getCurrentSpeed() {
		return currentSpeed;
	}

	/**
	 *  set current speed 
	 *  @param currentSpeed in meter per second 
	*/	
	private void setCurrentSpeed(Double currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	/** get current orientation 
	 *  @return orientation in degree (0 to 360)
	 */
	public Double getCurrentTrack() {
		return currentTrack;
	}

	/** set current orientation
	 *  @param currentTrack orientation in degree (0 to 360) 
	*/
	private void setCurrentTrack(Double currentTrack) {
		this.currentTrack = currentTrack;
	}	
	
	
	
	/**
	 * GpsData for return type of decodeGPSDataJson
	 */
	private static final class GpsData{
		
		private WGS84 position;
		private Double speed;
		private Double track;

		public GpsData(WGS84 position, Double speed, Double track) {
			super();
			this.position = position;
			this.speed = speed;
			this.track = track;
		}	
		
		/** current position in WGC84 format */
		private WGS84 getPosition() {
			return position;
		}
		
		/** current speed in meter per second */
		private Double getSpeed() {
			return speed;
		}
		
		/** current orientation in degree (0 to 360) */
		private Double getTrack() {
			return track;
		}		
	}
	
	
	//Unit testing
	public static void main (String[] args) throws IOException{
		Geolocation geo = new Gps();
		
		geo.addPositionListener(new GeolocationListener() {

			public void positionChanged(WGS84 position, Double speed, Double track) {
				System.out.println(position + " Speed : " + speed + " Track : " + track);
			}

		});
		
		geo.start();
		
		
	}



}
