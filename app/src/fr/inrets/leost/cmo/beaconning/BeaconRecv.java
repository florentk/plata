package fr.inrets.leost.cmo.beaconning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import fr.inrets.leost.cmo.beaconning.packet.CMOState;


/**
 * generalisation of a beacon receiver
 * @author florent kaisser <florent.kaisser@free.fr>
 * @has 0..* - -  BeaconRecvListener
 */
public class BeaconRecv extends Thread{

	private final Collection<BeaconRecvListener> listerners = new ArrayList<BeaconRecvListener>();
	
	
	/** interval between two expired entry check (in ms) */
	public static final int CHECK_EXPIRED_ENTRY_INTERVAL = 1000;	
	
	private Hashtable<PacketForwardedKey, PacketForwardedValue>  packetFwd = new Hashtable<PacketForwardedKey, PacketForwardedValue>();
	
	private static final class PacketForwardedKey{	
		private String CMOId;
		private Integer seq;

		public PacketForwardedKey(String cMOId, Integer seq) {
			super();
			CMOId = cMOId;
			this.seq = seq;
		}

		/**
		 * @return the cMOId
		 */
		public String getCMOId() {
			return CMOId;
		}

		/**
		 * @return the seq
		 */
		public int getSeq() {
			return seq;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((CMOId == null) ? 0 : CMOId.hashCode());
			result = prime * result + ((seq == null) ? 0 : seq.hashCode());
			return result;
		}
		
	
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PacketForwardedKey other = (PacketForwardedKey) obj;
			if (CMOId == null) {
				if (other.CMOId != null)
					return false;
			} else if (!CMOId.equals(other.CMOId))
				return false;
			if (seq == null) {
				if (other.seq != null)
					return false;
			} else if (!seq.equals(other.seq))
				return false;
			return true;
		}

		public String toString(){
			return CMOId + " " + seq.toString();
		}
		
	}	
	
	private static final class PacketForwardedValue{
		private Date dateEntry;
		private int lifetime;
		/**
		 * @return the dateEntry
		 */
		public Date getDateEntry() {
			return dateEntry;
		}
		/**
		 * @return the lifetime
		 */
		public int getLifetime() {
			return lifetime;
		}
		public PacketForwardedValue(int lifetime) {
			this.lifetime = lifetime;
			dateEntry = new Date();
		}
		
		boolean isExpired(){
			Date now = new Date();
			return ( now.getTime() > (getDateEntry().getTime() + getLifetime()) );
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "dateEntry=" + dateEntry
					+ ", lifetime=" + lifetime;
		}	
		
		

	}

	class RemoveExpiredEntry extends TimerTask{

		public void run() {
			deleteExpiredEntry();
		}
	}	
	
	public BeaconRecv(){
		new Timer().schedule(new RemoveExpiredEntry() , 0, CHECK_EXPIRED_ENTRY_INTERVAL);
	}
	
	
	public void addListener(BeaconRecvListener l){
		listerners.add(l);
	}
	
	public void removeListener(BeaconRecvListener l){
		listerners.remove(l);
	}
	
	protected void notifyListener(CMOState stat){
	
		PacketForwardedKey pfk = new PacketForwardedKey(stat.getCmoID(), stat.getSeq());
		

		//not notify twice for same packet
		if(packetFwd.containsKey(pfk))
			return;		

		
		for ( BeaconRecvListener l : listerners )
			l.cmoStatChanged(stat);	
		
		synchronized (packetFwd) {
			packetFwd.put(pfk, new PacketForwardedValue(stat.getLifetime()));	
		}
	
	}
	
	public void deleteExpiredEntry(){
		synchronized (packetFwd) {
			for(Iterator<PacketForwardedValue> i = packetFwd.values().iterator();i.hasNext();){
				PacketForwardedValue entry = i.next();
				if(entry.isExpired()){
					//System.out.println("Remove entry : "+entry);
					i.remove();
				}
			}
		}
	}
			
}