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
						newStat.getLongitude(),
						newStat.getLatitude(),
						newStat.getH(),
						newStat.getSpeed(),
						newStat.getTrack(),
						newStat.getLifetime()
					));

		//notify the listerners
		for (Iterator<CMOTableListener> i=listerners.iterator();i.hasNext();){
			CMOTableListener l=  i.next();
			l.tableChanged(newStat.getCmoID(),table);
		}		
	}
	
	public void deleteExpiredEntry(){
		for(Iterator<CMOTableEntry> i = table.values().iterator();i.hasNext();){
			CMOTableEntry entry = i.next();
			if(entry.isExpired())
				i.remove();
		}
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
