package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.utils.Physics;
import fr.inrets.leost.geolocation.Geolocation;

/**
 * indicator for stopping distance (breaking distance + human reaction delay)
 * 
 * @has 1 - - Geolocation
 * @has 1 - - CoefFriction
 * @assoc - - - Physics
 */
public class StoppingDistance implements Indicator {

	private Geolocation geo;
	private CoefFriction cf;	
	private Double dist = null;
	
	
	
	public StoppingDistance(Geolocation geo, CoefFriction cf) {
		this.geo = geo;
		this.cf = cf;		
	}



	@Override
	public void update() {
		// TODO Auto-generated method stub
		dist = Physics.StoppingDistance(geo.getCurrentSpeed(),  cf.getCoef());
	}

	/**
	 * @return the speed
	 */
	public Double getDistance() {
		return dist;
	}

	public String name(){
		return "Stopping distance";
	}


	public String toString(){
		if(getDistance() == null)
			return "N/A";
		
		return String.format("%01.1f m", getDistance()) ;
	}

}
