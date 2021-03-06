package fr.inrets.leost.cmo.management;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Collection;

import org.apache.log4j.Logger;

import fr.inrets.leost.cmo.beaconning.BeaconRecvEthernet;
import fr.inrets.leost.cmo.beaconning.BeaconRecvListener;
import fr.inrets.leost.cmo.beaconning.packet.CMOState;
import fr.inrets.leost.geolocation.Gps;



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
 * @has 1 - - CMOTable
 * @has 0..* - - CMOTableListener
 */
public class CMOManagement implements BeaconRecvListener {

	/** interval between two expired entry check (in ms) */
	public static final int CHECK_EXPIRED_ENTRY_INTERVAL = 1000;
	
	/** init the logger */
	private static Logger logger = Logger.getLogger(CMOManagement.class);
	
	
	private CMOTable table;
	
	private final Collection<CMOTableListener> listerners = new ArrayList<CMOTableListener>();

	
	
	public CMOManagement(){
		table = new CMOTable();
		
		new Timer().schedule(new RemoveExpiredEntry() , 0, CHECK_EXPIRED_ENTRY_INTERVAL);
	}
	
	
	
	private void notifyListenerChanged(CMOTableEntry cmo){
		logger.info("entry_chaged: "+cmo);
		
		//notify the listerners
		for (CMOTableListener l : listerners)
			l.tableChanged(cmo);		
	}
	
	private void notifyListenerRemove(CMOTableEntry cmo){
		logger.info("entry_removed: "+cmo);
		
		//notify the listerners
		for (CMOTableListener l : listerners)
			l.tableCMORemoved(cmo);		
	}	
	
	private void notifyListenerAdd(CMOTableEntry cmo){
		logger.info("entry_added: "+cmo);
		
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

		
			if( table.containsKey(newStat.getCmoID())){
				synchronized (table){
					entry = table.get(newStat.getCmoID());
				
					entry.updateEntry(
									new Double (newStat.getLongitude()),
									new Double (newStat.getLatitude()),
									new Double (newStat.getH()),
									new Double (newStat.getSpeed()),
									new Double (newStat.getTrack()),
									newStat.getLifetime(),
									newStat.getTime());
				}
				notifyListenerChanged(entry);
			}else{
				synchronized (table){
					table.put(newStat.getCmoID(), 
							entry = new CMOTableEntry(
									newStat.getCmoID(),
									newStat.getCmoType(),
									
									new Double (newStat.getLongitude()),
									new Double (newStat.getLatitude()),
									new Double (newStat.getH()),
									new Double (newStat.getSpeed()),
									new Double (newStat.getTrack()),
									newStat.getLifetime(),
									newStat.getTime()
								));
				}
				notifyListenerAdd(entry);
		}
	}
	
	/**
	 * check in regular interval the expired entry
	 */
	public void deleteExpiredEntry(){
		ArrayList<CMOTableEntry> entryDeleted = new ArrayList<CMOTableEntry>();
		
		synchronized (table){
			for(Iterator<CMOTableEntry> i = table.values().iterator();i.hasNext();){
				CMOTableEntry entry = i.next();
				
				//expired entry ?
				if(entry.isExpired()){
					i.remove();
					entryDeleted.add(entry);
				}
			}
		}
		
		//notify the listener of the removed entry				
		for (CMOTableEntry entry:entryDeleted)
			notifyListenerRemove(entry);
	}
	

	public Collection<CMOTableEntry> getTable() {
		synchronized (table){
			return new ArrayList<CMOTableEntry>(table.values());
		}
	}	
	
	public CMOTableEntry getEntry(String id) {
		return table.get(id);
	}		
	
	public boolean cmoInTable(String id){
		return table.containsKey(id);
	}
	
	public Set<String> getCMOIds(){
		synchronized (table){
			return new HashSet<String>(table.keySet());
		}
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
		
		//init logger
		org.apache.log4j.BasicConfigurator.configure();
		logger.setLevel(org.apache.log4j.Level.DEBUG);
		
		BeaconRecvEthernet recv = BeaconRecvEthernet.loopPacketFromDevice(args[0]);
		/*BeaconRecvFake recv = new BeaconRecvFake();
		
		recv.addFixedCMO(new CMOState(
				new CMOHeader((byte)100, 0, 5000, "CC",CMOHeader.CMO_TYPE_SPOT ),
				3.12892007828f,
				50.6190795898f,
				0.0f,
				1.0f,
				0.0f,0));*/
		
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
