package fr.inrets.leost.geolocation;


import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
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
 * @depend - - - GpsData
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 */
public class Gps  extends Geolocation  {


	public static final int GPSD_PORT = 2947;
	

	/** reader buffer for read the data from gpsd*/
	private BufferedReader gpsBR;
	/** writer buffer for read the data from gpsd*/
	private BufferedWriter gpsBW;	
	


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
	 * 
	 * configure gpsd session in json data format
	 * @param gpsBW
	 * @throws IOException
	 */
    private void writeGPSInitJson() throws IOException {
		gpsBW.write( "?WATCH={\"enable\":true,\"json\":true}\n" );
		gpsBW.flush();		
	}
	
	/**
	 * 
	 * stop gpsd session 
	 * @param gpsBW
	 * @throws IOException
	 */
	private void writeGPSStop() throws IOException {
		if(gpsBW != null){
			gpsBW.write( "?WATCH={\"enable\":false}\n" );
			gpsBW.flush();		
		}
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
	
	public Gps()  throws IOException{
		super();
		
		//connection to gpsd
		connectGPS();

	}



	

	/**
	 * Connection to gpsd
	 * @throws IOException
	 */
	private void connectGPS()  {
		Socket gpsSocket;
		
		try {

		
		//create the socket
		gpsSocket = connectToGpsd();
		
		//get a reader buffer
		gpsBR = new BufferedReader( new 
				InputStreamReader( gpsSocket.getInputStream()) );
		
		gpsBW = new BufferedWriter( new 
				OutputStreamWriter( gpsSocket.getOutputStream()) );
		
		//configure the gpsd session in json data
		writeGPSInitJson();
		
		}catch (IOException e){
			System.err.println("Unable connect to GPS daemon, is it started ?");
		}

	}
	
	public void dispose(){
		try{
			writeGPSStop();
		}catch (IOException e){
			System.err.println("Unable write to GPS daemon");
		}
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
				if(data!= null  && (   !getLastPos().equals(data.getPosition())
						|| !getCurrentSpeed().equals(data.getSpeed())
						|| !getCurrentTrack().equals(data.getTrack())))
				{
					//setCurrentTime(data.getTime());
					setCurrentPos(data.getPosition());
					setCurrentSpeed(data.getSpeed());	
					setCurrentTrack(data.getTrack());						
				}


			} catch(IOException ioe) {
				System.err.println("Connection error with gps service");
			}
		}
		
	}
	
	//Unit testing
	public static void main (String[] args) throws IOException{
		Geolocation geo = new Gps();
		
		java.util.Locale.setDefault(java.util.Locale.US);
		
		geo.addPositionListener(new GeolocationListener() {

			public void positionChanged(WGS84 position, Double speed, Double track) {
				//System.out.println(position + " Speed : " + speed + " Track : " + track);
			}

		});
		
		geo.run();
		geo.dispose();
		
	}



}
