import java.util.Iterator;
import java.util.TimerTask;
import java.util.Timer;


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
public class CMOManagement implements CMOStateListener {

	/** interval betwwen two expired entry check (in ms) */
	public static final int CHECK_EXPIRED_ENTRY_INTERVAL = 1000;
	
	CMOStateTable table;
	
	public CMOManagement(){
		table = new CMOStateTable();
		
		new Timer().schedule(new RemoveExpiredEntry() , 0, CHECK_EXPIRED_ENTRY_INTERVAL);
	}
	
	/**
	 * @see CMOStateListener#cmoStatChanged(CMOState)
	 */
	@Override
	public void cmoStatChanged(CMOState newStat) {
	
		if( table.containsKey(newStat.getCmoID()))
			table.remove(newStat.getCmoID());
		
		table.put(newStat.getCmoID(), new CMOStateTableEntry (newStat));

		System.out.println(table);
	}
	
	public void deleteExpiredEntry(){
		for(Iterator<CMOStateTableEntry> i = table.values().iterator();i.hasNext();){
			CMOStateTableEntry entry = i.next();
			if(entry.isExpired())
				i.remove();
		}
	}
	
	/**
	 * @return the table
	 */
	public CMOStateTable getTable() {
		return table;
	}	
	
	class RemoveExpiredEntry extends TimerTask{

		public void run() {
			deleteExpiredEntry();
		}
	}

	public static void main(String[] args) throws Exception {
		
		BeaconRecv recv = BeaconRecv.loopPacketFromDevice(args[0]);
		
		CMOManagement m = new CMOManagement();
		
		recv.addListener(m);
		recv.start();
		recv.join();
	}



}
