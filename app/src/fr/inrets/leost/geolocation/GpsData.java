package fr.inrets.leost.geolocation;

/**
 * GpsData data of a Gps
 * 
 * @has - - - WGS84
 */
public  class GpsData{
	
	private Double t;
	private WGS84 position;
	private Double speed;
	private Double track;

	public GpsData(Double t,WGS84 position, Double speed, Double track) {
		super();
		this.position = position;
		this.speed = speed;
		this.track = track;
	}	
	
	/** current position in WGC84 format */
	public WGS84 getPosition() {
		return position;
	}
	
	/** current speed in meter per second */
	public Double getSpeed() {
		return speed;
	}
	
	/** current orientation in degree (0 to 360) */
	public Double getTrack() {
		return track;
	}

	/**
	 * @return the t
	 */
	public Double getTime() {
		return t;
	}	
	
	
	
}