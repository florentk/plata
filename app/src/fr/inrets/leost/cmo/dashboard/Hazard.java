package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.geolocation.Geolocation;

public class Hazard implements Indicator {

	public static final int DECISION_NONE = 0;
	public static final int DECISION_WARNING = 1;	
	public static final int DECISION_HAZARD = 2;	
	
	private Geolocation geo;
	private ClosestCMO closestCMO;
	private StoppingDistance stoppingDistance;
	private BrakingDistance brakingDistance;	
	
	private int decision=DECISION_NONE;

	
	

	public Hazard(Geolocation geo, ClosestCMO closestCMO,
			StoppingDistance sDistance, BrakingDistance bDistance) {
		super();
		this.geo = geo;
		this.closestCMO = closestCMO;
		this.stoppingDistance = sDistance;
		this.brakingDistance = bDistance;		
	}




	@Override
	public void update() {
		if(closestCMO.getClosestCMO() !=null){
		
			double distance = closestCMO.getDistance();
			double sDistance = stoppingDistance.getDistance();
			double bDistance = brakingDistance.getDistance();
	
			decision = DECISION_NONE;

		    if(distance <= sDistance)
				decision = DECISION_WARNING;				
			
		    if(distance <= bDistance)
				decision = DECISION_HAZARD;		
		    
    
		
		}
	}
	
	
	

	public String name(){
		return "Comment";
	}


	public String toString(){
		switch(decision){
		case DECISION_WARNING : return "Warning";
		case DECISION_HAZARD : return "Hazard";
		}
		
		return "None";
	}

}
