

/**
 * Geolocation system interface <br><br>
 * 
 * The position coordinate use the WGS84 representation 
 * commonly used by the GPS <br><br>
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 */

public interface Geolocation {
	
	/**
	 * init the geolocation system
	 */
	//public void init();
	
	/**
	 * get the interval update value of position 
	 * @return interval in ms
	 */
	public int getUpdateInterval();

	/**
	 * set the interval update value of position  
	 * @param updateInterval interval in ms
	 */
	public void setUpdateInterval(int updateInterval);
	
	/**
	 * get the current position
	 * @return the current position in WGS84 format
	 */
	public WGS84 getCurrentPos();


	/**
	 * register a new listener
	 * @param l
	 */
	public void addPositionListener(GeolocationListener l);
	
	/**
	 * remove a registered listener
	 * @param l
	 */
	public void removePositionListener(GeolocationListener l);
	
	
	/**
	 *  get current speed 
	 *  @return speed in meter per second 
	*/
	public Double getCurrentSpeed();


	/** get current orientation 
	 *  @return orientation in degree (0 to 360)
	 */
	public Double getCurrentTrack();

}
