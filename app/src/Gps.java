


import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
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
 * [1] http://gpsd.berlios.de/
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 */
public class Gps {

	private static final int DEFAULT_UPDATA_INTERVAL = 250;
	private static final int GPSD_PORT = 2947;
	
	/**
	 * interval (in ms) between two gpsd request
	 */
	private int updateInterval;

	/**
	 * reader buffer for read the data from gpsd
	 */
	private BufferedReader gpsBR;
	
	/**
	 * current position in WGC84 format
	 */
	private WGS84 currentPos;


	/**
	 * Set of listener for receive a event on position changing
	 */
	private final Collection<GpsListener> gpsListeners = new ArrayList<GpsListener>();


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
	 * decode a json gps data.
	 * @param str json string
	 * @return the geographical position in WGS84 format
	 */
	static private WGS84 decodeGPSPositionJson(String str){
		
		
		try{
			//convert JSON string in Java Map (in true language term : dictionary)
			Map dict=(Map)(new JSONParser()).parse(str);
			
			//System.out.println(dict);
			
			//TPV class for get position (see gpsd spec)
			String fClass = (String)dict.get("class");
			if (fClass.compareToIgnoreCase("TPV") == 0){
				
				//get the geographical position
				Double lat = (Double)dict.get("lat");
				Double lon = (Double)dict.get("lon");		
				Double alt = (Double)dict.get("alt");	
				
				return new WGS84(lon,lat,alt);
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
		
		//init the timer
		initTimer();
	}
	
	/**
	 * init the timer 
	 */
	private void initTimer(){
		Timer timer = new Timer();
	    timer.schedule(new RequestGPS(), 0, getUpdateInterval());
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
		for (Iterator<GpsListener> i=gpsListeners.iterator();i.hasNext();){
			GpsListener l=  i.next();
			l.positionChanged(getCurrentPos());
		}
	}
	
	/**
	 * read a line from gpsd and decode the data
	 */
	private void pollGPS() {
		if (gpsBR == null) return;

		// for each line in the buffer
		try {
			while  (gpsBR.ready()) {
				//decode the data
				WGS84 pos = decodeGPSPositionJson(gpsBR.readLine());
				
				//if needed, update the current position
				if(pos!= null && !currentPos.equals(pos))
					setCurrentPos(pos);
				
			}
		} catch(IOException ioe) {
			System.err.println("Connection error with gps service");
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
	public void addPositionListener(GpsListener l){
		gpsListeners.add(l);
	}
	
	/**
	 * remove a registered listener
	 * @param l
	 */
	public void removePositionListener(GpsListener l){
		gpsListeners.remove(l);
	}
	
	

	/**
	 * call when the timer expire (every update interval value)
	 * @author florent
	 *
	 */
	class RequestGPS extends TimerTask {
		public void run() {
			pollGPS();
		}
	}	
	

	public static void main (String[] args) throws IOException{
		Gps gps = new Gps();
		
		gps.addPositionListener(new GpsListener() {

			public void positionChanged(WGS84 position) {
				System.out.println(position);
			}

		});
		
	}

}
