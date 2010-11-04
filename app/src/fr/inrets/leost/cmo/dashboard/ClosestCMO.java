package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.beaconning.packet.CMOHeader;
import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.utils.Physics;
import fr.inrets.leost.geolocation.Geolocation;
import fr.inrets.leost.geolocation.WGS84;

/**
 * indicator for show the closest CMO in front and same direction
 * 
 * @author florent kaisser
 * @has 1 - - CMOManagement
 * @has 1 - - Geolocation
 * @depend 1 closest - CMOTableEntry
 * @assoc - - - Physics
 */
public class ClosestCMO implements Indicator {

	/**none hazard*/
	public static final int DECISION_NONE = 0;
	/**warning*/
	public static final int DECISION_WARNING = 1;	
	/**hazard !*/
	public static final int DECISION_HAZARD = 2;	
	
	private int decision=DECISION_NONE;
		
	private CMOManagement cmo;
	private Geolocation geo;
	
	private CMOTableEntry closestCMO=null;
	private double distance=0.0;
	
	
	public ClosestCMO(Geolocation geo, CMOManagement cmo) {
		super();
		this.cmo = cmo;
		this.geo = geo;
	}
	
	/**
	 * compute the closest CMO in front and in same direction from the table of CMO
	 * @param longitude longitude of CMO
	 * @param latitude latitude of CMO
	 * @param track track  of CMO
	 * @return the table entry of closest CMO
	 */
	synchronized public void updateClosestCMOInFront(Double longitude, Double latitude, Double track){
		CMOTableEntry closest=null;
		Double closestDist= null;
		double lg=longitude.doubleValue(),lt=latitude.doubleValue(),t=track.doubleValue();
		double  dx,dy,dist;
		
		//for each value in CMO table
		for ( CMOTableEntry e : cmo.getTable() ){
			
			//same direction of CMO candidate ?
			if ( Physics.inSameDirection(t, e.getTrack().floatValue()) ){
			
				//position difference with the CMO candidate
				dx = (e.getLongitude().doubleValue() - lg);
				dy = (e.getLatitude().doubleValue() - lt);

				// CMO candidate in front ?
				if(Physics.inFront(dx,dy,t)){
					//compute the Cartesian distance
					dist = Physics.cartesianDistance(dx,dy);
	
					//if the CMO candidate is the closest update the actual closest CMO
					if(closest == null || closestDist.compareTo( dist ) > 0 ){
						closest = e;
						closestDist = dist;
					}
				}
			}
		}
		
		if(closest != null)
			distance = closestDist;
		
		closestCMO = closest;
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
	

	public String decisionToString(){
		switch(decision){
		case DECISION_WARNING : return "Warning";
		case DECISION_HAZARD : return "Hazard";
		}
		
		return "None";
	}	
	

	
	@Override
	public void update() {
		updateClosestCMOInFront( geo.getCurrentPos().longitude(), geo.getCurrentPos().latitude(), geo.getCurrentTrack());

		if(closestCMO !=null)
			decision = computeDecision(
					distance, 
					Physics.StoppingDistance(geo.getCurrentSpeed(), Physics.COEF_FRICTION_AVG), 
					Physics.BrakingDistance(geo.getCurrentSpeed(), Physics.COEF_FRICTION_AVG));
		else
			decision =  DECISION_NONE;
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
	 * @return the decision
	 */
	public int getDecision() {
		return decision;
	}
	

	public String name(){
		return "ClosestCMO";
	}

	public String toString(){

		if(closestCMO == null)
			return "N/A";
		

		return String.format("%s (%s) at %01.1f m (%s)",closestCMO.getCmoID(), CMOHeader.typeToString(closestCMO.getCmoType()),distance, decisionToString()  ); 

	}

}
