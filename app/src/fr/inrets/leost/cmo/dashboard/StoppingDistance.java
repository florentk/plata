package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.utils.Physics;
import fr.inrets.leost.geolocation.Geolocation;

public class StoppingDistance implements Indicator {

	private Geolocation geo;
	private Double dist = null;
	
	
	
	public StoppingDistance(Geolocation geo) {
		this.geo = geo;
	}



	@Override
	public void update() {
		// TODO Auto-generated method stub
		dist = Physics.StoppingDistance(geo.getCurrentSpeed(), Physics.COEF_FRICTION_AVG);
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
