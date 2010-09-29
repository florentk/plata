package fr.inrets.leost.geolocation;



import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;



/**
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 */
public class Gps  extends Geolocation  {

	
	/** interval (in ms) between two gpsd request*/
	private int updateInterval;
	
	/** current position in WGC84 format */
	private WGS84 currentPos;
	
	/** current speed in meter per second */
	private Double currentSpeed;
	
	/** current orientation in degree (0 to 360) */
	private Double currentTrack;
	

	/** collection of listener for receive a event on position changing*/
	private final Collection<GeolocationListener> gpsListeners = new ArrayList<GeolocationListener>();

	public Fixe(WGS84, pos, speed, track)  {

		
		//init the variable
		setUpdateInterval(DEFAULT_UPDATA_INTERVAL);
		setCurrentPos(pos);
		setCurrentSpeed(speed);
		setCurrentTrack(track);		

	}
	

	
	/**
	 * must be call when the current position change
	 */
	private void positionChanged(){
		//call the method positionChanged of each registered listener
		for (GeolocationListener l : gpsListeners)
			l.positionChanged(getCurrentPos(),getCurrentSpeed(),getCurrentTrack());
		
	}
	

	public void run() {
		
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
	
	
	
	//Unit testing
	public static void main (String[] args) throws IOException{
		Geolocation geo = new Fixe(new WGS84(1.0,2.0,3.0),4.0,5.0);
		
		geo.addPositionListener(new GeolocationListener() {

			public void positionChanged(WGS84 position, Double speed, Double track) {
				System.out.println(position + " Speed : " + speed + " Track : " + track);
			}

		});
		
		geo.start();
		
		geo.join();
	}



}
