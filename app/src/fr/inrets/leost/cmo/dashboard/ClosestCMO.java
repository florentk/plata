package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.utils.Physics;
import fr.inrets.leost.geolocation.Geolocation;
import fr.inrets.leost.geolocation.WGS84;

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
	
	public CMOTableEntry closestCMOInFront(Double longitude, Double latitude, Double track){
		CMOTableEntry closest=null;
		Double closestDist= null;
		double lg=longitude.doubleValue(),lt=latitude.doubleValue(),t=track.doubleValue();
		double  dx,dy,dist;
		
		for ( CMOTableEntry e : cmo.getTable().values() ){
			
			if ( Physics.inSameDirection(t, e.getTrack().floatValue()) ){
			
				dx = (e.getLongitude().doubleValue() - lt);
				dy = (e.getLatitude().doubleValue() - lg);

				if(Physics.inFront(dx,dy,t)){
					dist = (float) Math.sqrt(  dx*dx + dy*dy  );
	
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

		if(closestCMO!=null) {
			distance = distanceToClosestCMO();
		
			//no take in account if too far or the track is not accurate (low speed)
			if( distance > 1000.0 ||  geo.getCurrentSpeed() < 3.0)
					closestCMO = null;

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


	

	public String name(){
		return "ClosestCMO";
	}

	public String toString(){

		if(closestCMO == null)
			return "N/A";
		

		return String.format("Closest CMO (%d) at %01.1f m : %s", closestCMO.getCmoType(),distance, closestCMO.getCmoID() ); 

	}

}
