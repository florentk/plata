package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.utils.Physics;
import fr.inrets.leost.geolocation.Geolocation;
import fr.inrets.leost.geolocation.WGS84;

public class ClosestCMO extends Indicator {

	private CMOManagement cmo;
	private Geolocation geo;
	
	private CMOTableEntry closestCMO=null;
	private double distance=0.0;
	
	private double bDistance;
	private double sDistance;
	private String cmt="None";
	

	
	
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
				cmt = "Warning";
			else if(distance < sDistance)
				cmt = "Hazard";	
			else 
				cmt = "None";
				
		}
	}
	
	private double distanceToClosestCMO(){
		double dx = (closestCMO.getLongitude().doubleValue() -  geo.getCurrentPos().longitude().doubleValue());
		double dy = (closestCMO.getLatitude().doubleValue() -  geo.getCurrentPos().latitude().doubleValue());
		return (double) Math.sqrt(  dx*dx + dy*dy  ) * ((Math.PI * WGS84.a / 180.0)) ;			
	}
	
	public String toString(){
		String s="";

		s+=String.format("Braking distance : %01.1f m Stopping distance : %01.1f m\n", bDistance, sDistance);
		if(closestCMO != null){
			s+=String.format("Closet CMO (%d) at %01.1f m : %s \n", closestCMO.getCmoType(),distance, closestCMO.getCmoID() ); 
			s+=String.format("Comment : %s\n", cmt);
		}

		return s;
	}

}
