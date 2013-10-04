package fr.inrets.leost.cmo.dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


import fr.inrets.leost.cmo.beaconning.BeaconRecv;
import fr.inrets.leost.cmo.beaconning.BeaconRecvEthernet;
import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.management.CMOTableListener;
import fr.inrets.leost.geolocation.*;
import fr.inrets.leost.weather.*;

/**
 * Collection of Indicator and update notification
 * 
 *          Indicator 1        .....           Indicator N 
 *               |               |                  |
 *               |----------------------------------|
 *                             |   /|\  Indicator.update()
 *                             |    |
 * DashboardListener.update() \|/   |
 *                        ---------------
 * CMOTableListener ----->|             |
 *                        |  Dashboard  |------------> DashboardListener.dashboardUpdate()
 * GeolocationListener -->|             |
 *                        ---------------
 *
 * @author florent kaisser
 * @has 0..* - - Indicator
 * @has 0..* - - DashboardListener
 *
 */
public class Dashboard implements CMOTableListener, GeolocationListener, WeatherListener {

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
		for (Indicator i : indicators) 
			i.update();
		
		// notify the listener
		for (DashboardListener l : listeners) l.dashboardUpdate();
	}
	
	
	public void weatherChanged(Weather data) {
		setUpdate();
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
	public void tableChanged(CMOTableEntry table) {
		setUpdate();
	}
	
	/**
	 * @see fr.inrets.leost.cmo.management.CMOTableListener#tableCMOAdded(java.lang.String, fr.inrets.leost.cmo.management.CMOTable)
	 */
	@Override
	public void tableCMOAdded(CMOTableEntry table) {
		setUpdate();
	}

	/** 
	 * @see fr.inrets.leost.cmo.management.CMOTableListener#tableCMORemoved(java.lang.String, fr.inrets.leost.cmo.management.CMOTable)
	 */
	@Override
	public void tableCMORemoved(CMOTableEntry table) {
		setUpdate();
	}	
	
	/**
	 * @return the indicators
	 */
	public Collection<Indicator> getIndicators() {
		return indicators;
	}	

	public String toString(){
		StringBuffer s=new StringBuffer();
		
		for (Indicator i : indicators)
			s.append(i.name() + " : " + i.toString()+"\n");
			
		return s.toString();
	}
	
	
	
	

	static final Dashboard db = new Dashboard();
	
	
	public static void startDashboard(String device)  throws IOException,InterruptedException {
		
		//Geolocation geo = new Fixe(new WGS84(),1.0,45.0);
		//Geolocation loc = new Gps();
		Geolocation loc = Trace.traceFromFile("/home/florent/tmp/v1",500);
		BeaconRecv recv = BeaconRecvEthernet.loopPacketFromDevice(device);
		Weather weather = new Fake("XXXX 191400Z 30005KT 250V320 9999 FEW046 BKN250 24/11 Q1020 NOSIG");
		
		/*recv.addFixedCMO(new CMOState(
				new CMOHeader((byte)100, 0, 5000, "CC",CMOHeader.CMO_TYPE_SPOT ),
				3.12892007828f,
				50.6190795898f,
				0.0f,
				1.0f,
				0.0f));*/

		/*recv.addFixedCMO(new CMOState(
				new CMOHeader((byte)100, 0, 5000, "AZ-197-UY",CMOHeader.CMO_TYPE_CAR ),
				3.12586784363f,
				50.6021995544f,
				0.0f,
				1.0f,
				0.0f));*/
				
		
		CMOManagement cmoMgt = new CMOManagement();
		
		//link the CMO Management with the beaconning receiver
		recv.addListener(cmoMgt);
		
		//link the  dashboard with the geolocation system
		loc.addPositionListener(db);
		
		//link the  dashboard with the CMO Mangement		
		cmoMgt.addListener(db);
		
		weather.addWeatherListener(db);
		
		//create the indicator of the dashboard	
		CoefFriction cf = new CoefFriction(weather);
		ClosestCMO closestCMO =  new ClosestCMO(loc, cmoMgt, cf);
		CrossingCMO crossingCMO = new CrossingCMO(loc,cmoMgt);
			
		db.addIndicator(new Position(loc));
		db.addIndicator(new Speed(loc));
		db.addIndicator(new Track(loc));
		db.addIndicator(cf);						
		db.addIndicator(new ReactionDistance(loc));
		db.addIndicator(new StoppingDistance(loc,cf));
		db.addIndicator(new WeatherIndicator(weather));
		db.addIndicator(closestCMO);  
		db.addIndicator(crossingCMO);  		
		
  
		
		for (Indicator id : db.getIndicators())
			System.out.print(id.name()+";");		
		System.out.println();	
		//event when dashboard updated
		db.addListener(new DashboardListener() {
				public void dashboardUpdate(){
					//System.out.print("\033[2J");
					System.out.println(db);
				}
		});
		
		//start the beaconning receiver
		recv.start();
		
		//start the geolocation system 
		loc.start();
		
		weather.start();
		
		//GpsMonitor.gpsGUI(geo);
		
		//wait the end
		
		loc.join();
		recv.join();
		weather.join();
		
		loc.dispose();
		weather.dispose();
	}
	
	
	
	public static void main(String[] args) throws IOException,InterruptedException {
		
		if(args.length<1){
			System.out.println("Not enough arguments");
			System.out.println("Usage : java Dashboard <device>");			
			System.exit(1);
		}	
		
		
		startDashboard(args[0]);
	}
	

}
