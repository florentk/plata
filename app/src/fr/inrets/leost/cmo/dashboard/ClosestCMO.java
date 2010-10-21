package fr.inrets.leost.cmo.dashboard;

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
	synchronized public CMOTableEntry closestCMOInFront(Double longitude, Double latitude, Double track){
		CMOTableEntry closest=null;
		Double closestDist= null;
		double lg=longitude.doubleValue(),lt=latitude.doubleValue(),t=track.doubleValue();
		double  dx,dy,dist;
		
		//for each value in CMO table
		for ( CMOTableEntry e : cmo.getTable() ){
			
			//same direction of CMO candidate ?
			if ( Physics.inSameDirection(t, e.getTrack().floatValue()) ){
			
				//position difference with the CMO candidate
				dx = (e.getLongitude().doubleValue() - lt);
				dy = (e.getLatitude().doubleValue() - lg);

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
		
		
		return closest;
	}	
	

	
	@Override
	public void update() {
		closestCMO = closestCMOInFront( geo.getCurrentPos().longitude(), geo.getCurrentPos().latitude(), geo.getCurrentTrack());

		/*if(closestCMO!=null) {
		
			//no take in account if too far or the track is not accurate (low speed)
			if( distance > 1000.0 ||  geo.getCurrentSpeed() < 3.0)
					closestCMO = null;
		}*/
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


	

	public String name(){
		return "ClosestCMO";
	}

	public String toString(){

		if(closestCMO == null)
			return "N/A";
		

		return String.format("Closest CMO (%d) at %01.1f m : %s", closestCMO.getCmoType(),distance, closestCMO.getCmoID() ); 

	}

}
