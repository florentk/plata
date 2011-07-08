package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.geolocation.*;

/**
 * indicator for the current position
 * 
 * @author florent kaisser
 *
 * @has 1 - - Geolocation
 */
public class Position implements Indicator  {

	private Geolocation geo;
	/**
	 * longitude (in ddmm.mmmm)
	 */
	private Double longitude;
	/**
	 * latitude (in ddmm.mmmm)
	 */
	private Double latitude;
	/**
	 * ellipsoidal elevation (in meters)
	 */
	private Double altitude;
	
	
	public Position(Geolocation geo) {
		this.geo = geo;
	}
	
	@Override
	public void update() {
		WGS84 pos = geo.getCurrentPos();
		
		longitude = pos.longitude();
		latitude = pos.latitude();
		altitude = pos.h();
		
	}
	
	public String name(){
		return "Position";
	}
	
	/**
	 * returns longitude angle in radian
	 */
	public Double longitude() {
		return longitude;
	}

	/**
	 * returns latitude angle in radian
	 */
	public Double latitude() {
		return latitude;
	}

	/**
	 * returns ellipsoidal elevation in meters
	 */
	public Double altitude() {
		return altitude;
	}
	
	public String toString(){
		return String.format("%01.6f %01.6f %01.1f m", longitude, latitude, altitude);
	}

	
}
