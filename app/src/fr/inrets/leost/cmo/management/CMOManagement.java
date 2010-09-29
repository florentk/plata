package fr.inrets.leost.cmo.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Collection;

import fr.inrets.leost.cmo.beaconning.BeaconRecv;
import fr.inrets.leost.cmo.beaconning.BeaconRecvListener;
import fr.inrets.leost.cmo.beaconning.packet.CMOState;;

/**
 * 
 * CMO state agregator
 * 
 * BeaconRecv --------
 *       ...         |------> CMOManagement -----> CMOStateTable
 * BeaconRecv --------
 * 
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 *
 */
public class CMOManagement implements BeaconRecvListener {
	
	public static final float MAX_ANGLE_SAME_DIRECTION = 90f; 

	/** interval between two expired entry check (in ms) */
	public static final int CHECK_EXPIRED_ENTRY_INTERVAL = 1000;
	
	private CMOTable table;
	

	private final Collection<CMOTableListener> listerners = new ArrayList<CMOTableListener>();

	
	
	public CMOManagement(){
		table = new CMOTable();
		
		new Timer().schedule(new RemoveExpiredEntry() , 0, CHECK_EXPIRED_ENTRY_INTERVAL);
	}
	
	/**
	 * @see CMOStateListener#cmoStatChanged(CMOState)
	 */
	@Override
	public void cmoStatChanged(CMOState newStat) {
	
		if( table.containsKey(newStat.getCmoID()))
			table.remove(newStat.getCmoID());
		

		table.put(newStat.getCmoID(), 
				new CMOTableEntry(
						newStat.getCmoID(),
						newStat.getCmoType(),
						
						new Double (newStat.getLongitude().doubleValue()),
						new Double (newStat.getLatitude()),
						new Double (newStat.getH()),
						new Double (newStat.getSpeed()),
						new Double (newStat.getTrack()),
						newStat.getLifetime()
					));

		//notify the listerners
		for (CMOTableListener l : listerners)
			l.tableChanged(newStat.getCmoID(),table);
			
	}
	
	public void deleteExpiredEntry(){
		for(Iterator<CMOTableEntry> i = table.values().iterator();i.hasNext();){
			CMOTableEntry entry = i.next();
			if(entry.isExpired())
				i.remove();
		}
	}
	
	static public boolean inSameDirection(double track1, double track2){
		return Double(Math.abs( track1 - track2 )).compareTo(MAX_ANGLE_SAME_DIRECTION) < 0;
	}

	static public boolean inFront(double dx, double dy, double track){
		return dx*Math.cos(track) + dy*Math.sin(track) > 0.0;
	}
	
	public CMOTableEntry closestCMOInFront(Double longitude, Double latitude, Double track){
		CMOTableEntry closest=null;
		Double closestDist= null;
		double lg=longitude.doubleValue(),lt=latitude.doubleValue(),t=track.doubleValue();
		double  dx,dy,dist;
		
		for ( CMOTableEntry e : table.values() ){
			
			if ( inSameDirection(t, e.getTrack().floatValue()) ){
			
				dx = (e.getLongitude().doubleValue() - lt);
				dy = (e.getLatitude().doubleValue() - lg);

				if(inFront(dx,dy,t))
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
	
	
	/**
	 * @return the table
	 */
	public CMOTable getTable() {
		return table;
	}	
	
	public void addListener(CMOTableListener l){
		listerners.add(l);
	}
	
	public void removeListener(CMOTableListener l){
		listerners.remove(l);
	}	
	
	class RemoveExpiredEntry extends TimerTask{

		public void run() {
			deleteExpiredEntry();
		}
	}

	public static void main(String[] args) throws Exception {
		
		BeaconRecv recv = BeaconRecv.loopPacketFromDevice(args[0]);
		
		CMOManagement m = new CMOManagement();
		
		m.addListener(new CMOTableListener() {
			@Override
			public void tableChanged(String cmoId, CMOTable table) {
				System.out.println(table);
			}
		});		
		
		recv.addListener(m);
		recv.start();
		recv.join();
	}



}
