package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.utils.Physics;
import fr.inrets.leost.geolocation.Geolocation;

/**
 * indicator for braking distance 
 * @has 1 - - Geolocation
 * @assoc - - - Physics
 */
public class ReactionDistance implements Indicator {

	private Geolocation geo;
	private Double dist = null;
	
	
	
	public ReactionDistance(Geolocation geo) {
		this.geo = geo;
	}



	@Override
	public void update() {
		// TODO Auto-generated method stub
		dist = Physics.ReactionDistance(geo.getCurrentSpeed());
	}

	/**
	 * @return the speed
	 */
	public Double getDistance() {
		return dist;
	}

	public String name(){
		return "Reaction distance";
	}


	public String toString(){
		if(getDistance() == null)
			return "N/A";
		
		return String.format("%01.1f m", getDistance()) ;
	}

}
