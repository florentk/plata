package fr.inrets.leost.cmo.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Collection;

import fr.inrets.leost.cmo.beaconning.BeaconRecv;
import fr.inrets.leost.cmo.beaconning.BeaconRecvFake;
import fr.inrets.leost.cmo.beaconning.BeaconRecvListener;
import fr.inrets.leost.cmo.beaconning.packet.CMOState;
import fr.inrets.leost.cmo.utils.Physics;

import fr.inrets.leost.cmo.beaconning.packet.CMOHeader;
import fr.inrets.leost.cmo.beaconning.packet.CMOState;

/**
 * 
 * CMO state agregator
 * 
 * 
 * 
 * BeaconRecv --------
 *       ...         |------> CMOManagement -----> CMOTable
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
	
	
	
	private void notifyListenerChanged(CMOTableEntry cmo){
		//notify the listerners
		for (CMOTableListener l : listerners)
			l.tableChanged(cmo);		
	}
	
	private void notifyListenerRemove(CMOTableEntry cmo){
		//notify the listerners
		for (CMOTableListener l : listerners)
			l.tableCMORemoved(cmo);		
	}	
	
	private void notifyListenerAdd(CMOTableEntry cmo){
		//notify the listerners
		for (CMOTableListener l : listerners)
			l.tableCMOAdded(cmo);		
	}		
	
	/**
	 * @see CMOStateListener#cmoStatChanged(CMOState)
	 */
	@Override
	public void cmoStatChanged(CMOState newStat) {
		CMOTableEntry entry;
		boolean newEntry = false;
		
		
		if( table.containsKey(newStat.getCmoID()))
			table.remove(newStat.getCmoID());
		else
			newEntry = true;
		

		table.put(newStat.getCmoID(), 
				entry = new CMOTableEntry(
						newStat.getCmoID(),
						newStat.getCmoType(),
						
						new Double (newStat.getLongitude().doubleValue()),
						new Double (newStat.getLatitude()),
						new Double (newStat.getH()),
						new Double (newStat.getSpeed()),
						new Double (newStat.getTrack()),
						newStat.getLifetime()
					));

		if(newEntry)
			notifyListenerAdd(entry);
		else
			notifyListenerChanged(entry);
			
	}
	
	/**
	 * check in regular interval the expired entry
	 */
	public void deleteExpiredEntry(){
		for(Iterator<CMOTableEntry> i = table.values().iterator();i.hasNext();){
			CMOTableEntry entry = i.next();
			
			//expired entry ?
			if(entry.isExpired()){
				//notify the listener of the removed entry
				notifyListenerRemove(entry);				
				i.remove();
			}
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
		
		//BeaconRecv recv = BeaconRecv.loopPacketFromDevice(args[0]);
		BeaconRecvFake recv = new BeaconRecvFake();
		
		recv.addFixedCMO(new CMOState(
				new CMOHeader((byte)100, 0, 5000, "CC",CMOHeader.CMO_TYPE_SPOT ),
				3.12892007828f,
				50.6190795898f,
				0.0f,
				1.0f,
				0.0f));
		
		CMOManagement m = new CMOManagement();
		
		m.addListener(new CMOTableListener() {
			@Override
			public void tableChanged(CMOTableEntry cmo) {
				System.out.println("Change : " + cmo);
			}
			
			public void tableCMORemoved(CMOTableEntry cmo) {
				System.out.println("Remove : " + cmo);
			}
			
			public void tableCMOAdded(CMOTableEntry cmo) {
				System.out.println("Add : " + cmo);
			}		
		});		
		
		recv.addListener(m);
		recv.start();
		recv.join();
	}



}
