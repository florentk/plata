package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.geolocation.Geolocation;

public class Track extends Indicator {

	private Geolocation geo;
	private Double track = new Double(0.0);
	
	
	
	public Track(Geolocation geo) {
		this.geo = geo;
	}



	@Override
	void update() {
		// TODO Auto-generated method stub
		track = geo.getCurrentTrack();
	}
	
	
	
	/**
	 * @return the speed
	 */
	public Double getTrack() {
		return track;
	}



	public String toString(){
		return "Track : " + getTrack().toString();
	}

}
