package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.geolocation.Geolocation;

public class ClosestCMO extends Indicator {

	private CMOManagement cmo;
	private Geolocation geo;
	
	private CMOTableEntry closestCMO;
	
	public ClosestCMO(Geolocation geo,CMOManagement cmo) {
		this.cmo=cmo;
		this.geo=geo;
	}
	
	@Override
	void update() {
		
	}

}
