package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.geolocation.Geolocation;

/**
 * Indicator for shwo a hazard
 * 
 * @author florent kaisser
 *
 */
public class Alert implements Indicator {

	/**none hazard*/
	public static final int DECISION_NONE = 0;
	/**warning*/
	public static final int DECISION_WARNING = 1;	
	/**hazard !*/
	public static final int DECISION_HAZARD = 2;	
	
	private Geolocation geo;
	private ClosestCMO closestCMO;
	private StoppingDistance stoppingDistance;
	private BrakingDistance brakingDistance;	
	
	private int decision=DECISION_NONE;



	public Alert(Geolocation geo, ClosestCMO closestCMO,
			StoppingDistance sDistance, BrakingDistance bDistance) {
		super();
		this.geo = geo;
		this.closestCMO = closestCMO;
		this.stoppingDistance = sDistance;
		this.brakingDistance = bDistance;		
	}

	/**
	 * compute the hazard from distance with the CMO, stopping distance and breaking distance
	 * @param distance distance with the CMO
	 * @param sDistance stopping distance
	 * @param bDistance breaking distance
	 * @return the decision
	 */
	public static int computeDecision(double distance, double sDistance, double bDistance){
		
	    if(distance <= bDistance)
			return DECISION_HAZARD;	

	    if(distance <= sDistance)
			return DECISION_WARNING;				
		
	    return  DECISION_NONE;
	}

	@Override
	public void update() {
		if(closestCMO.getClosestCMO() !=null)
			
			decision = computeDecision(
					closestCMO.getDistance(), 
					stoppingDistance.getDistance(), 
					brakingDistance.getDistance());
	}
	
	/**
	 * @return the decision
	 */
	public int getDecision() {
		return decision;
	}

	
	public String name(){
		return "Alert";
	}


	public String toString(){
		switch(decision){
		case DECISION_WARNING : return "Warning";
		case DECISION_HAZARD : return "Hazard";
		}
		
		return "None";
	}

}
