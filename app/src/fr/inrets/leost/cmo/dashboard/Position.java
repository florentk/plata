package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.geolocation.*;

public class Position extends Indicator  {

	private Geolocation geo;
	private WGS84 pos;
	
	
	public Position(Geolocation geo) {
		this.geo = geo;
	}
	
	@Override
	void update() {
		pos = geo.getCurrentPos();
	}
	
	public String toString(){
		return "Position : " + " " +pos.toString();
	}
}
