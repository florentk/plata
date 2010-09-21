/**
 * poll GPS data from gpsd service
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 */


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




public class Gps {

	private static final int DEFAULT_UPDATA_INTERVAL = 250;
	private static final int GPSD_PORT = 2947;
	
	/**
	 * interval (in ms) between two gpsd request
	 */
	private int updateInterval;

	private BufferedReader gpsBR;
	
	/**
	 * current position
	 */
	WGS84 currentPos;


	private final Collection<GpsListener> gpsListeners = new ArrayList<GpsListener>();


	static private void writeGPSInitJson(BufferedWriter gpsBW) throws IOException {
		gpsBW.write( "?WATCH={\"enable\":true,\"json\":true}\n" );
		gpsBW.flush();		
	}

	static private Socket connectToGpsd()throws IOException {
		return  connectToGpsd( InetAddress.getByName("localhost"), GPSD_PORT);	
	}
	
	static private Socket connectToGpsd(InetAddress addr, int port)throws IOException {
		return new Socket( addr, port);	
	}	
	
	static private WGS84 decodeGPSPositionJson(String str){
		
		
		try{
			//convert JSON string in Java Map (in true language term : dictionary)
			Map dict=(Map)(new JSONParser()).parse(str);
			
			//System.out.println(dict);
			
			//TPV class for get position
			String fClass = (String)dict.get("class");
			if (fClass.compareToIgnoreCase("TPV") == 0){
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
	

	public Gps() throws IOException {
		connectGPS();
		
		setUpdateInterval(DEFAULT_UPDATA_INTERVAL);
		setCurrentPos(new WGS84());
		
		initTimer();
	}
	
	private void initTimer(){
		Timer timer = new Timer();
	    timer.schedule(new RequestGPS(), 0, getUpdateInterval());
	}
	

	private void connectGPS() throws IOException {
		Socket gpsSocket;
		
		gpsSocket = connectToGpsd();
		
		gpsBR = new BufferedReader( new 
				InputStreamReader( gpsSocket.getInputStream()) );
		
		writeGPSInitJson(new BufferedWriter( new 
				OutputStreamWriter( gpsSocket.getOutputStream() ) ));
		


	}
	
	private void positionChanged(){
		for (Iterator<GpsListener> i=gpsListeners.iterator();i.hasNext();){
			GpsListener l=  i.next();
			l.positionChanged(getCurrentPos());
		}
	}
	
	private void pollGPS() {
		if (gpsBR == null) return;

		try {
			while  (gpsBR.ready()) {
				WGS84 pos = decodeGPSPositionJson(gpsBR.readLine());
				
				if(pos!= null && !currentPos.equals(pos))
					setCurrentPos(pos);
				
			}
		} catch(IOException ioe) {
			System.err.println("Connection error with gps service");
		}
		
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}  
	
	public WGS84 getCurrentPos() {
		return currentPos;
	}

	private void setCurrentPos(WGS84 currentPos) {
		this.currentPos = currentPos;
		positionChanged();
	}	
	
	
	public void addPositionListener(GpsListener l){
		gpsListeners.add(l);
	}
	
	public void removePositionListener(GpsListener l){
		gpsListeners.remove(l);
	}
	
	

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
