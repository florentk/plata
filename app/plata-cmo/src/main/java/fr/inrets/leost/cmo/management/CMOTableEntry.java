package fr.inrets.leost.cmo.management;

import java.util.Date;

import fr.inrets.leost.cmo.beaconning.packet.CMOHeader;
import fr.inrets.leost.cmo.utils.Physics;

/**
 * Entry of a CMO (mutable)
 * @author florent kaisser
 *
 */
public class CMOTableEntry {

	/** CMO identity */
	private String cmoID;	
	
	/** CMO type */
	private short cmoType;
	
	/**
	 * longitude (in ddmm.mmmm)
	 */
	private Double longitude;
	/**
	 * latitude (in ddmm.mmmm)
	 */
	private Double latitude;
	
	/**
	 * altitude (in meters)
	 */
	private Double altitude;
	
	
	private Double lastLong=null, lastLati=null, lastAlti=null;
	
	/** speed in meter per second*/
	private Double speed;

	/** orientation in degree (0 to 360) */
	private Double track;	
	
	/**  the time for which CMO considere not accessible  */
	private int lifetime;
	
	private Date dateEntry;
	
	private Date sysDateLastState;
	private int dateLastState;	
	
	private Double vLongitude=null, vLatitude=null, vAltitude=null;
	

	public CMOTableEntry(String cmoID, short cmoType, Double longitude,
			Double latitude, Double altitude, Double speed, Double track,
			int lifetime, int dateLastState) {
		super();
		sysDateLastState = new Date();
		setEntry(cmoID,cmoType,longitude, latitude, altitude, speed, track, lifetime, dateLastState);
	}
	
	private void setEntry(String cmoID, short cmoType, Double longitude,
			Double latitude, Double altitude, Double speed, Double track,
			int lifetime, int dateLastState){
		lastLong = this.longitude;
		lastLati = this.latitude;		
		lastAlti = this.altitude;			
	
		this.cmoID = cmoID;
		this.cmoType = cmoType;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.speed = speed;
		this.track = track;
		this.lifetime = lifetime;
		this.dateEntry = new Date();	
		this.dateLastState = dateLastState;
	}
	
	private double stateOlder(){
		return ((double)((new Date()).getTime() - sysDateLastState.getTime()))/1000.0;
	}
	
	private void computeVelocity(double longitude, double latitude, double altitude, double dt) {		
		vLongitude = new Double((longitude - this.longitude) / dt);
		vLatitude  = new Double((latitude - this.latitude) / dt);
		vAltitude  = new Double((altitude - this.altitude) / dt);
		sysDateLastState = new Date();
	}
	
	public void updateEntry(Double longitude,
			Double latitude, Double altitude, Double speed, Double track,
			int lifetime, int dateLastState) {
		
		//System.out.println(longitude + " " + latitude);
		
		//if a new state : update velocity
		if (dateLastState != this.dateLastState)
		{
			double dt = ((double)(dateLastState - this.dateLastState))/1000.0;
			computeVelocity(longitude, latitude, altitude, dt);
		}
			
		setEntry(cmoID,cmoType,longitude, latitude, altitude, speed, track, lifetime, dateLastState);
		
	}

	boolean isExpired(){
		Date now = new Date();
		return ( now.getTime() > (getDateEntry().getTime() + getLifetime()) );
	}


	
	public double crossPosX(double longitude, double latitude, 
			double lastLongitude, double lastLatitude){
		

		
		if(this.lastLong == null) return 0.0;


		
		double xCross = Physics.CrossPosX(
				lastLongitude, lastLatitude, 
				longitude, latitude, 
				this.lastLong, this.lastLati, 
				this.longitude, this.latitude);
		
		System.out.println(
				"xa1 " + lastLongitude + " " +
				"ya1 " + lastLatitude + " " +
				"xa2 " + longitude + " " +
				"ya2 " + latitude + " " +
				"xb1 " + this.lastLong + " " +
				"yb1 " + this.lastLati + " " + 
				"xb2 " + this.longitude + " " +
				"yb2 " + this.latitude + " " +
				" = " + xCross
				);			
		
		/*if(xCross==0.0) return 0.0;
		
		return (xCross - lastLongitude) / vLongitude;*/
		
		return xCross;
	}	

	/**
	 * @return the CMO identity
	 */
	public String getCmoID() {
		return cmoID;
	}

	/**
	 * @return the CMO type
	 */
	public short getCmoType() {
		return cmoType;
	}

	/**
	 * @return the longitude (in ddmm.mmmm)
	 */
	public Double getLongitude() {
		if(vLongitude == null)
			return longitude;
		else
			return longitude + vLongitude *  stateOlder();
	}

	/**
	 * @return the latitude (in ddmm.mmmm)
	 */
	public Double getLatitude() {
		if(vLatitude == null)
			return latitude;
		else
			return latitude + vLatitude *  stateOlder();		
	}

	/**
	 * @return  altitude (in metter)
	 */
	public Double getAltitude() {
		if(vAltitude == null)
			return altitude;
		else
			return altitude + vAltitude *  stateOlder();		
	}

	/**
	 * @return the speed in metter per second)
	 */
	public Double getSpeed() {
		return speed;
	}

	/**
	 * @return orientation in degree (0 to 360)
	 */
	public Double getTrack() {
		return track;
	}

	/**
	 * @return the time for which CMO considere not accessible
	 */
	protected int getLifetime() {
		return lifetime;
	}

	/**
	 * @return date which entry is added
	 */
	protected Date getDateEntry() {
		return dateEntry;
	}
	
	public String toString(){
		String s="";
		
		s+="CMO Id : "+	getCmoID()+",";
		s+="CMO Type : "+ CMOHeader.typeToString(getCmoType()) + ",";
		s+="Longitude : "+getLongitude()+",";
		s+="Latitude : "+getLatitude()+",";
		s+="Altitude : "+getAltitude()+",";
		s+="Speed : "+getSpeed()+",";
		s+="Orientation : "+getTrack()+",";		
		s+="Entry date : " + getDateEntry();
		
		return s;
	}
}
