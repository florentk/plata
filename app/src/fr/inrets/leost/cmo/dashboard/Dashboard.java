package fr.inrets.leost.cmo.dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import jpcap.JpcapCaptor;


import fr.inrets.leost.cmo.beaconning.BeaconRecv;
import fr.inrets.leost.cmo.beaconning.BeaconRecvFake;
import fr.inrets.leost.cmo.beaconning.packet.CMOHeader;
import fr.inrets.leost.cmo.beaconning.packet.CMOState;
import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTable;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.management.CMOTableListener;
import fr.inrets.leost.cmo.ui.GpsMonitor;
import fr.inrets.leost.geolocation.*;

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
		for (Indicator i : indicators) 
			i.update();
		
		// notify the listener
		for (DashboardListener l : listeners) l.dashboardUpdate();
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
			s.append(i.toString()+";");
			
		return s.toString();
	}
	
	
	
	

	static final Dashboard db = new Dashboard();
	
	
	public static void startDashboard(String device)  throws IOException,InterruptedException {
		
		//Geolocation geo = new Fixe(new WGS84(),1.0,45.0);
		Geolocation geo = new Gps();
		BeaconRecv recv = BeaconRecv.loopPacketFromDevice(device);
		
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
		geo.addPositionListener(db);
		
		//link the  dashboard with the CMO Mangement		
		cmoMgt.addListener(db);
		
		//create the indicator of the dashboard
		StoppingDistance sDistance = new StoppingDistance(geo);
		BrakingDistance bDistance = new BrakingDistance(geo);		
		ClosestCMO closestCMO = new ClosestCMO(geo, cmoMgt);
		db.addIndicator(new Position(geo));
		db.addIndicator(new Speed(geo));
		db.addIndicator(new Track(geo));
		db.addIndicator(bDistance);
		db.addIndicator(sDistance);
		db.addIndicator(closestCMO);   
		db.addIndicator(new Alert(geo, closestCMO, sDistance, bDistance));     
		
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
		geo.start();
		
		//GpsMonitor.gpsGUI(geo);
		
		//wait the end
		geo.join();recv.join();		
		
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
