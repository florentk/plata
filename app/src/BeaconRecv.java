
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import jpcap.*;
import jpcap.packet.Packet;
import jpcap.packet.EthernetPacket;

/**
 * 
 * JpcapCaptor (raw Ethernet) ----> BeaconRecv ----|CMOState|----> CMOStatListener
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 *
 */

public class BeaconRecv  implements PacketReceiver {
	
	JpcapCaptor jpcap;
	
	private final Collection<CMOStateListener> listerners = new ArrayList<CMOStateListener>();
	

	public BeaconRecv(JpcapCaptor jpcap){
		this.jpcap = jpcap;
	}
	
	
	public void addListener(CMOStateListener l){
		listerners.add(l);
	}
	
	public void removeListener(CMOStateListener l){
		listerners.remove(l);
	}
	
	public void receivePacket(Packet packet) {
		

		if (packet.datalink instanceof EthernetPacket){
			EthernetPacket ether = (EthernetPacket) packet.datalink;
			
			if (ether.frametype == CMOHeader.ETHERTYPE_CMO){

				//decode packet
				CMOState cmo = new CMOState(packet.data);
				
				
				//notify the listerners
				for (Iterator<CMOStateListener> i=listerners.iterator();i.hasNext();){
					CMOStateListener l=  i.next();
					l.cmoStatChanged(cmo);
				}				
			}
				
		}
			
	}
	

	public void init(){
		jpcap.loopPacket(-1, this);	
	}
	

	
	/*--------------------------------------------------------------------------------
	 * Unit testing
	 */

	private static CMOStateListener createPrintListener(){
		return new CMOStateListener() {

			public void cmoStatChanged(CMOState stat) {
				System.out.println(stat);
			}

		};
	}
	
	private static void loopPacketFromDevice(String strDevice){
	    NetworkInterface device = PcapsTool.toNetworkInterface(strDevice);
	    
	    if(device==null){
	    	System.out.println("The interface " + strDevice + " doesn't exist");
	    	PcapsTool.printDevice();
	    	return;
	    }
		
	    try{
	    	BeaconRecv bRecv = new BeaconRecv(JpcapCaptor.openDevice(device, 2000, false, 20));
	    	bRecv.addListener(createPrintListener());
	    	bRecv.init();   
	    	
	    }catch (java.io.IOException e){
	    	System.out.println("Cannot open network interface : "+e);
	    	return;
	    }
	}
	
	private static void loopPacketFromFile(String path){

	    
	    try{
	    	BeaconRecv bRecv = new BeaconRecv(JpcapCaptor.openFile(path));
	    	bRecv.addListener(createPrintListener());
	    	bRecv.init();      	
	    	
	    }catch (java.io.IOException e){
	    	System.out.println("Cannot open file : "+e);
	    	return;
	    }
	}	
	
	private static void printUsage(){
		System.out.println("Usage : java BeaconRecv -i <device> ");		
		System.out.println("        java BeaconRecv -f <file> ");	
	}

	public static void main(String[] args) throws Exception {

		if(args.length<2){
			System.out.println("Not enough arguments");
			printUsage();
			System.exit(1);			
		}
		
		if(args[0].compareTo("-i")==0){
			loopPacketFromDevice(args[1]);
		}else if(args[0].compareTo("-f")==0){
			loopPacketFromFile(args[1]);
		}else{
			System.out.println("Bad argument");
			printUsage();
			System.exit(1);					
		}
		
	}


}
