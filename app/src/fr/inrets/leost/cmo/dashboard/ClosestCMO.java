package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.geolocation.Geolocation;

public class ClosestCMO extends Indicator {

	private CMOManagement cmo;
	private Geolocation geo;
	
	private CMOTableEntry closestCMO=null;
	
	public ClosestCMO(Geolocation geo,CMOManagement cmo) {
		this.cmo=cmo;
		this.geo=geo;
	}
	
	@Override
	void update() {
		closestCMO = cmo.closestCMOInFront( geo.getCurrentPos().longitude(), geo.getCurrentPos().latitude(), geo.getCurrentTrack());
	}
	
	private double distanceToClosestCMO(){
		double dx = (closestCMO.getLongitude().doubleValue() -  geo.getCurrentPos().longitude().doubleValue());
		double dy = (closestCMO.getLatitude().doubleValue() -  geo.getCurrentPos().latitude().doubleValue());
		return (float) Math.sqrt(  dx*dx + dy*dy  );			
	}
	
	public String toString(){
		if(closestCMO == null)
			return  "No close CMO";
		

		return "Closet CMO : " + closestCMO.getCmoID()  + "(" + closestCMO.getCmoType() +  ") at " + distanceToClosestCMO(); 
		
		
	}

}
