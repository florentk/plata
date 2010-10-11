package fr.inrets.leost.geolocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Geolocation system interface <br><br>
 * 
 * The position coordinate use the WGS84 representation 
 * commonly used by the GPS <br><br>
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 * @has 1 - - WGS84
 * @has 0..* - - GeolocationListener
 * 
 */

public abstract class  Geolocation extends Thread {
	
	public static final int DEFAULT_UPDATA_INTERVAL = 250;	
	
	/** interval (in ms) between two gpsd request*/
	private int updateInterval;

	
	/** current position in WGC84 format */
	private WGS84 currentPos;
	
	/** current speed in meter per second */
	private Double currentSpeed;
	
	/** current orientation in degree (0 to 360) */
	private Double currentTrack;
	
	/** current time when the data has received*/
	private Double currentTime;
	

	/** collection of listener for receive a event on position changing*/
	private final Collection<GeolocationListener> gpsListeners = new ArrayList<GeolocationListener>();

	
	
	/**
	 * Connect to gpsd in localhost with 2947  port
	 * @throws IOException
	 */
	public Geolocation() {

		
		//init the variable
		setUpdateInterval(DEFAULT_UPDATA_INTERVAL);
		setCurrentPos(new WGS84());
		setCurrentSpeed(0.0);
		setCurrentTrack(0.0);		

	}	
	
	/**
	 * must be call when the current position change
	 */
	private void positionChanged(){
		//call the method positionChanged of each registered listener
		for (GeolocationListener l : gpsListeners)
			l.positionChanged(getCurrentTime(),getCurrentPos(),getCurrentSpeed(),getCurrentTrack());
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
	protected void setCurrentPos(WGS84 currentPos) {
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
	
	
	/** get current time when the data has received
	 * @return current time when the data has received
	 * */
	public Double getCurrentTime() {
		return currentTime;
	}
	
	/**
	 *  set current speed 
	 *  @param currentSpeed in meter per second 
	*/	
	protected void setCurrentSpeed(Double currentSpeed) {
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
	protected void setCurrentTrack(Double currentTrack) {
		this.currentTrack = currentTrack;
	}	
		
	public abstract void dispose();


}
