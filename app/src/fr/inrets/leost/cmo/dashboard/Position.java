package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.geolocation.*;

/**
 * indicator for the current position
 * 
 * @author florent kaisser
 *
 * @has 1 - - Geolocation
 * @depend 1 - - WGS84
 */
public class Position implements Indicator  {

	private Geolocation geo;
	private WGS84 pos;
	
	
	public Position(Geolocation geo) {
		this.geo = geo;
	}
	
	@Override
	public void update() {
		pos = geo.getCurrentPos();
	}
	
	public String name(){
		return "Position";
	}
	
	public WGS84 getCurrentPos(){
		return pos;
	}

	
	public String toString(){
		return  pos.toString();
	}
}
