package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.utils.Physics;
import fr.inrets.leost.geolocation.Geolocation;
import fr.inrets.leost.geolocation.WGS84;

public class ClosestCMO extends Indicator {

	public static final int DECISION_NONE = 0;
	public static final int DECISION_WARNING = 1;	
	public static final int DECISION_HAZARD = 2;		
	
	private CMOManagement cmo;
	private Geolocation geo;
	
	private CMOTableEntry closestCMO=null;
	private double distance=0.0;
	
	private double bDistance;
	private double sDistance;
	private int decision=DECISION_NONE;
		
	public ClosestCMO(Geolocation geo,CMOManagement cmo) {
		this.cmo=cmo;
		this.geo=geo;
	}
	
	@Override
	void update() {
		closestCMO = cmo.closestCMOInFront( geo.getCurrentPos().longitude(), geo.getCurrentPos().latitude(), geo.getCurrentTrack());
		bDistance = Physics.BrakingDistance(geo.getCurrentSpeed(), Physics.COEF_FRICTION_AVG);
		sDistance = Physics.StoppingDistance(geo.getCurrentSpeed(), Physics.COEF_FRICTION_AVG);

		if(closestCMO!=null) {
			distance = distanceToClosestCMO();
		
			//no take in account if too far or the track is not accurate (low speed)
			if( distance > 1000.0 ||  geo.getCurrentSpeed() < 3.0)
					closestCMO = null;
			

			
			if(distance < sDistance)
				decision = DECISION_WARNING;
			else if(distance < sDistance)
				decision = DECISION_HAZARD;	
			else 
				decision = DECISION_NONE;
				
		}
	}
	
	private double distanceToClosestCMO(){
		double dx = (closestCMO.getLongitude().doubleValue() -  geo.getCurrentPos().longitude().doubleValue());
		double dy = (closestCMO.getLatitude().doubleValue() -  geo.getCurrentPos().latitude().doubleValue());
		return (double) Math.sqrt(  dx*dx + dy*dy  ) * ((Math.PI * WGS84.a / 180.0)) ;			
	}
	
	/**
	 * @return the closestCMO
	 */
	public CMOTableEntry getClosestCMO() {
		return closestCMO;
	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @return the bDistance
	 */
	public double getBakingDistance() {
		return bDistance;
	}

	/**
	 * @return the sDistance
	 */
	public double getStoppingDistance() {
		return sDistance;
	}
	
	/**
	 * @return the decision
	 */
	public int getDecision() {
		return decision;
	}
	
	public static String decisionToString(int decesion){
		switch(decesion){
		case DECISION_WARNING : return "Waring";
		case DECISION_HAZARD : return "Hazard";
		}
		
		return "None";
	}

	public String toString(){
		String s="";

		s+=String.format("Braking distance : %01.1f m Stopping distance : %01.1f m\n", bDistance, sDistance);
		if(closestCMO != null){
			s+=String.format("Closest CMO (%d) at %01.1f m : %s \n", closestCMO.getCmoType(),distance, closestCMO.getCmoID() ); 
			s+=String.format("Comment : %s\n", decisionToString(decision));
		}

		return s;
	}

}
