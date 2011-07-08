package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.geolocation.Geolocation;

/**
 * indicator for show the track
 * @author florent kaisser
 *
 * @has 1 - - Geolocation
 */
public class Track implements Indicator {

	private Geolocation geo;
	private Double track = new Double(0.0);
	
	
	
	public Track(Geolocation geo) {
		this.geo = geo;
	}



	@Override
	public void update() {
		// TODO Auto-generated method stub
		track = geo.getCurrentTrack();
	}
	
	
	
	/**
	 * @return the speed
	 */
	public Double getTrack() {
		return track;
	}

	public String name(){
		return "Track";
	}


	public String toString(){
		return String.format("%01.0f°", getTrack()) ;
	}

}
