package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.utils.Physics;
import fr.inrets.leost.geolocation.Geolocation;

public class BrakingDistance implements Indicator {

	private Geolocation geo;
	private Double dist = null;
	
	
	
	public BrakingDistance(Geolocation geo) {
		this.geo = geo;
	}



	@Override
	public void update() {
		// TODO Auto-generated method stub
		dist = Physics.BrakingDistance(geo.getCurrentSpeed(), Physics.COEF_FRICTION_AVG);
	}

	/**
	 * @return the speed
	 */
	public Double getDistance() {
		return dist;
	}

	public String name(){
		return "Braking distance";
	}


	public String toString(){
		if(getDistance() == null)
			return "N/A";
		
		return String.format("%01.1f m", getDistance()) ;
	}

}
