package fr.inrets.leost.cmo.beaconning;

import java.util.Date;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import fr.inrets.leost.cmo.beaconning.packet.CMOState;
import fr.inrets.leost.cmo.utils.PcapsTool;

/**
 * 
 * forward CMO stat beacon
 * 
 * BeaconRecvListener --|CMOState|--> BeaconForward ----|CMOState|----> JPcap (row ethernet)
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 *
 */
public class BeaconForward implements BeaconRecvListener {

	/** interval between two expired entry check (in ms) */
	public static final int CHECK_EXPIRED_ENTRY_INTERVAL = 1000;
	
	private JpcapSender sender;
	
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
	
	public BeaconForward(JpcapSender sender) {
		this.sender = sender;
		
		new Timer().schedule(new RemoveExpiredEntry() , 0, CHECK_EXPIRED_ENTRY_INTERVAL);
	}

	/**
	 * @see fr.inrets.leost.cmo.beaconning.BeaconRecvListener#cmoStatChanged(fr.inrets.leost.cmo.beaconning.packet.CMOState)
	 */
	@Override
	public void cmoStatChanged(CMOState stat) {
		PacketForwardedKey pfk = new PacketForwardedKey(stat.getCmoID(), stat.getSeq());
		
		if(packetFwd.containsKey(pfk))
			return;
		
		if(stat.getTTL()==0)
			return;
		
		sender.sendPacket(BeaconGenerator.createCMOStatPacket( (byte)((int)stat.getTTL()-1), stat.getSeq(), stat.getLifetime(), stat.getCmoID(), stat.getCmoType(), stat.getLongitude(), stat.getLatitude(), stat.getH(), stat.getSpeed(), stat.getTrack(), stat.getTime()));
	
		packetFwd.put(pfk, new PacketForwardedValue(stat.getLifetime()));
		
		System.out.println("Forward packet : "+pfk);
	}
	
	public void deleteExpiredEntry(){
		for(Iterator<PacketForwardedValue> i = packetFwd.values().iterator();i.hasNext();){
			PacketForwardedValue entry = i.next();
			if(entry.isExpired()){
				//System.out.println("Remove entry : "+entry);
				i.remove();
			}
		}
	}
		

	public static void startForwarder(String strDevice){
	    NetworkInterface device = PcapsTool.toNetworkInterface(strDevice);
	    
	    if(device==null){
	    	System.out.println("The interface " + strDevice + " doesn't exist");
	    	PcapsTool.printDevice();
	    	return;
	    }
		
	    try{
	    	 
	    	 
	    	BeaconRecv recv =  new BeaconRecvEthernet(JpcapCaptor.openDevice(device, 2000, false, 20), "");
	    	BeaconForward f = new BeaconForward(JpcapSender.openDevice(device));
	    	
			recv.addListener(f);
			recv.start();
			
			try{
				recv.join();	   
			}catch (InterruptedException e){}
	    	
	    }catch (java.io.IOException e){
	    	System.out.println("Cannot open network interface : "+e);
	    }

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("Not enough arguments");
			System.out.println("Usage : java BeaconForward <device>");			
			System.exit(1);
		}		
		
		startForwarder(args[0]);
	}

}
