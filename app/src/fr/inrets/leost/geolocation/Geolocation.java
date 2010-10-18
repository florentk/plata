package fr.inrets.leost.geolocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;


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
	
	/** velocity in unit by second   */
	private WGS84 velocity = null;
	/** velocity  history  */
	/*private static final int VELOCITIES_SIZE_MAX = 1;
	LinkedList<WGS84> velocities = new LinkedList<WGS84>();*/
	
	/** acceleration in unit by second by second  */
	//private WGS84 acc = null;	
	/** acceleration  history  */
	/*private static final int ACCS_SIZE_MAX = 1;
	LinkedList<WGS84> accs = new LinkedList<WGS84>();	*/
	
	/** current speed in meter per second */
	private Double currentSpeed;
	
	/** current orientation in degree (0 to 360) */
	private Double currentTrack;
	
	/** system time when the data has received*/
	private Date sysTime;
	
	/** time when the device has started*/
	private Date startTime = new Date();	
	
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
			l.positionChanged(currentPos,currentSpeed,currentTrack);
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
	 * get the current position with extrapolation
	 * @return the current position in WGS84 format
	 */
	public WGS84 getCurrentPos() {
		return getPredictPos();
	}
	
	
	private WGS84 getPredictPos() {
		if(velocity == null)
			//no velocity, assume no moving
			return currentPos;
		
		//compute the time since the last data acquisition
		double dt = ((double)((new Date()).getTime() - sysTime.getTime()))/1000.0;	
		
		//if(acc == null)
			//no acceleration, assume a constant velocity
			return new WGS84(
				currentPos.longitude() +  (velocity.longitude() * dt) ,
				currentPos.latitude() +  (velocity.latitude() * dt) , 
				currentPos.h() + (velocity.h() * dt)
				);
		/*
		//full equation with velocity and acceleration
		return new WGS84(
				currentPos.longitude() +  (velocity.longitude() * dt) +  (acc.longitude() * dt * dt),
				currentPos.latitude() +  (velocity.latitude() * dt) +  (velocity.latitude() * dt * dt), 
				currentPos.h() + (velocity.h() * dt) + (velocity.h() * dt * dt)
				);		*/
	}	
	
	//
	
	/**
	 * get the last position recevied by the device
	 * without interpolation
	 * @return the last position
	 */
	public WGS84 getLastPos() {
		return currentPos;
	}
	


	/**
	 * compute the dirivate from WGS84 type. The derivate is smooth with the last computed derivate
	 * The position can be a derivate position, i.e. the velocity for compute a acceleration
	 * @param prevPos position at t0
	 * @param newPos position at t1
	 * @param dt t1 - t0
	 * @param hist last derivate computed. This list will be update with the new derivate
	 * @param nbMaxHist max entry in hist
	 * @return the derivate
	 */
	private WGS84 computeDerivate(WGS84 prevPos, WGS84 newPos, double dt/*, LinkedList<WGS84> hist, int nbMaxHist*/){
		WGS84 d =  new WGS84(
				(newPos.longitude() - prevPos.longitude()) / dt,
				(newPos.latitude() - prevPos.latitude()) / dt,	
				(newPos.h() - prevPos.h()) / dt
		);		
		return d;
		/*
		if(d.longitude()>1.0) return new WGS84();
		
		hist.addLast(d);
		
		if (hist.size() > nbMaxHist)
			hist.removeFirst();

		double slat = 0.0;double slon = 0.0;double sh = 0.0;
		
		for (WGS84 e:hist){
			slat+=e.latitude();
			slon+=e.longitude();
			sh+=e.h();			
		}	
		
		
		
		return  new WGS84(
				slon/hist.size(),
				slat/hist.size(),
				sh/hist.size()
		);		
		*/
		
	}
	
static int n =0;
	/**
	 * set the current position
	 * @param currentPos the current position in WGS84 format
	 */
	protected void setCurrentPos(WGS84 currentPos) {
		
		
		//if no current piosition, can't compute the velocity
		if(this.currentPos!=null){
			
			//System.out.println(n++ + " " + getPredictPos().sub(currentPos) + " " + velocity);

			double dt = ((double)((new Date()).getTime() - sysTime.getTime()))/1000.0;
			//System.out.println(dt);
			WGS84 newVelocity = computeDerivate(this.currentPos,currentPos, dt/*, velocities, VELOCITIES_SIZE_MAX*/);
			
			//if no velocity, can't compute the acceleration
			/*if(velocity != null){
				acc = computeDerivate(velocity,newVelocity, dt);
			}*/
			
			//if(velocity != null) System.out.println(n++ + " " + velocity + " "+ acc);
				
			//update the current velocity
			velocity=newVelocity;
		}		
		
		
		//update the current position
		this.currentPos = currentPos;
		sysTime = new Date();
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
	 *         in millisecond since this devise started
	 * */
	public int getTime() {
		if(sysTime!=null)
			return (int)(sysTime.getTime() - startTime.getTime());
		else 
			return 0;
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
		
	/** set the time when data has acquire
	 *  @param currentTrack orientation in degree (0 to 360) 
	*/
	/*protected void setCurrentTime(Double currentTime) {
		this.devTime = currentTime;
	}		*/
	
	public abstract void dispose();


}
