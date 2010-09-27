package fr.inrets.leost.cmo.management;

import java.util.Date;


public class CMOTableEntry {

	/** CMO identity */
	private String cmoID;	
	
	/** CMO type */
	private short cmoType;
	
	/**
	 * longitude (in ddmm.mmmm)
	 */
	private Float longitude;
	/**
	 * latitude (in ddmm.mmmm)
	 */
	private Float latitude;
	
	/**
	 * altitude (in meters)
	 */
	private Float altitude;
	
	/** speed in meter per second*/
	private Float speed;

	/** orientation in degree (0 to 360) */
	private Float track;	
	
	/**  the time for which CMO considere not accessible  */
	private int lifetime;
	
	private Date dateEntry;
	

	public CMOTableEntry(String cmoID, short cmoType, Float longitude,
			Float latitude, Float altitude, Float speed, Float track,
			int lifetime) {
		super();
		this.cmoID = cmoID;
		this.cmoType = cmoType;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.speed = speed;
		this.track = track;
		this.lifetime = lifetime;
		this.dateEntry = new Date();
	}

	boolean isExpired(){
		Date now = new Date();
		return ( now.getTime() > (getDateEntry().getTime() + getLifetime()) );
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
	public Float getLongitude() {
		return longitude;
	}

	/**
	 * @return the latitude (in ddmm.mmmm)
	 */
	public Float getLatitude() {
		return latitude;
	}

	/**
	 * @return  altitude (in metter)
	 */
	public Float getAltitude() {
		return altitude;
	}

	/**
	 * @return the speed in metter per second)
	 */
	public Float getSpeed() {
		return speed;
	}

	/**
	 * @return orientation in degree (0 to 360)
	 */
	public Float getTrack() {
		return track;
	}

	/**
	 * @return the time for which CMO considere not accessible
	 */
	public int getLifetime() {
		return lifetime;
	}

	/**
	 * @return date which entry is added
	 */
	public Date getDateEntry() {
		return dateEntry;
	}
	
	public String toString(){
		String s="";
		
		s+="CMO Id : "+	getCmoID()+"\n";
		s+="CMO Type : "+ getCmoType() + "\n";
		s+="Longitude : "+getLongitude()+"\n";
		s+="Latitude : "+getLatitude()+"\n";
		s+="Altitude : "+getAltitude()+"\n";
		s+="Speed : "+getSpeed()+"\n";
		s+="Orientation : "+getTrack()+"\n";		
		s+="Entry date : " + getDateEntry();
		
		return s;
	}
}
