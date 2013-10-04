package fr.inrets.leost.geolocation;


import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Map;

import org.json.simple.parser.*;
import org.apache.log4j.Logger;


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
public class Gps  extends GpsJSON {


	public static final int GPSD_PORT = 2947;
	
	
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
	

	
	public Gps()  throws IOException{
		super(0);
		
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

	

	
	//Unit testing
	public static void main (String[] args) throws IOException{
		Geolocation geo = new Gps();
		
		//init logger
		org.apache.log4j.BasicConfigurator.configure();
		logger.setLevel(org.apache.log4j.Level.DEBUG);
		
		//point separator for real
		java.util.Locale.setDefault(java.util.Locale.US);
		
		geo.addPositionListener(new GeolocationListener() {

			public void positionChanged(WGS84 position, Double speed, Double track) {
				logger.debug("position_changed: "+ position + " " + speed + " "+ track );
			}

		});
		
		geo.run();
		geo.dispose();
	}



}
