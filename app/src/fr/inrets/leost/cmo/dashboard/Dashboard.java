package fr.inrets.leost.cmo.dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


import fr.inrets.leost.cmo.beaconning.BeaconRecv;
import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTable;
import fr.inrets.leost.cmo.management.CMOTableListener;
import fr.inrets.leost.geolocation.*;


public class Dashboard implements CMOTableListener, GeolocationListener{

	private Collection<Indicator> indicators =new ArrayList<Indicator>();
	private Collection<DashboardListener> listeners =new ArrayList<DashboardListener>();	
	
	public void addIndicator(Indicator indicator){
		indicators.add(indicator);
	}
	
	public void removeIndicator(Indicator indicator){
		indicators.remove(indicator);		
	}	
	
	public void addListener(DashboardListener l){
		listeners.add(l);
	}
	
	public void removeListerner(DashboardListener l){
		listeners.remove(l);		
	}	
	
	/**
	 * process the update
	 */
	public void setUpdate(){
		// compute the new data of indicators
		for (Indicator i : indicators) i.update();
		
		// notify the listener
		for (DashboardListener l : listeners) l.update();
	}
	
	
	/**
	 * @see fr.inrets.leost.geolocation.GeolocationListener#positionChanged(fr.inrets.leost.geolocation.WGS84, java.lang.Double, java.lang.Double)
	 */
	@Override
	public void positionChanged(WGS84 position, Double speed, Double track) {
		setUpdate();
	}

	/**
	 * @see fr.inrets.leost.cmo.management.CMOTableListener#tableChanged(java.lang.String, fr.inrets.leost.cmo.management.CMOTable)
	 */
	@Override
	public void tableChanged(String cmoId, CMOTable table) {
		setUpdate();
	}

	public String toString(){
		StringBuffer s=new StringBuffer();
		
		for (Indicator i : indicators)
			s.append(i.toString()+"\n");
			
		return s.toString();
	}
	
	

	static final Dashboard db = new Dashboard();
	public static void main(String[] args) throws IOException,InterruptedException {
		Geolocation geo = new Gps();
		BeaconRecv recv = BeaconRecv.loopPacketFromDevice(args[0]);
		CMOManagement cmoMgt = new CMOManagement();
		
		//link the CMO Management with the beaconning receiver
		recv.addListener(cmoMgt);
		
		//link the  dashboard with the geolocation system
		geo.addPositionListener(db);
		
		//link the  dashboard with the CMO Mangement		
		cmoMgt.addListener(db);
		
		//create the indicator of the dashboard
		db.addIndicator( new Speed(geo));
		db.addIndicator( new Position(geo));		
		db.addIndicator( new ClosestCMO(geo,cmoMgt));	
		
		
		//event when dashboard updated
		db.addListener(new DashboardListener() {
				public void update(){
					System.out.print("\033[2J");
					System.out.println(db);
				}
		});
		
		//start the beaconning receiver
		recv.start();
		
		//start the geolocation system 
		geo.start();
		
		//wait the end
		geo.join();recv.join();		
	}
	

}
