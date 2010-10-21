package fr.inrets.leost.cmo.beaconning;



import jpcap.*;
import jpcap.packet.Packet;
import jpcap.packet.EthernetPacket;


import fr.inrets.leost.cmo.utils.PcapsTool;
import fr.inrets.leost.cmo.beaconning.packet.*;

/**
 * 
 * JpcapCaptor (raw Ethernet) ----> BeaconRecv ----|CMOState|----> CMOStatListener
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 *
 */

public class BeaconRecvEthernet extends  BeaconRecv implements PacketReceiver {
	
	JpcapCaptor jpcap;
	int delay;
	String myCmoId;
	
	

	public BeaconRecvEthernet(JpcapCaptor jpcap, int delay, String myCmoId){
		super();
		this.jpcap = jpcap;
		this.delay = delay;
		this.myCmoId = myCmoId;
	}
	
	/**
	 * 
	 * @param jpcap capture interface
	 * @param myCmoId my cmo ID, for drop packet send by me
	 */
	public BeaconRecvEthernet(JpcapCaptor jpcap, String myCmoId){
		this(jpcap, 0, myCmoId);
	}	
	

	
	//receive a packet from layer 2 (Ethernet)
	public void receivePacket(Packet packet) {
		
		if(delay!=0){
			try{sleep(100);}catch(InterruptedException e){}
		}

		if (packet.datalink instanceof EthernetPacket){
			EthernetPacket ether = (EthernetPacket) packet.datalink;

			if (ether.frametype == CMOHeader.ETHERTYPE_CMO){

				//decode packet
				CMOState cmo = new CMOState(packet.data);
				
				//drop packet from me and expired packet
				if(cmo.getCmoID().compareTo(myCmoId)!=0){
					//notify the listerners
					notifyListener(cmo);
				}
							
			}
				
		}
			
	}
	

	public void run(){
		jpcap.loopPacket(-1, this);	
	}
	

	
	/* BeaconRecv factory */
	
	/**
	 * BeaconRecv factory. Create a BeaconRecv from a device
	 * @param strDevice interface name
	 * @return
	 */
	public static BeaconRecvEthernet loopPacketFromDevice(String strDevice){
	    NetworkInterface device = PcapsTool.toNetworkInterface(strDevice);
	    
	    if(device==null){
	    	System.out.println("The interface " + strDevice + " doesn't exist");
	    	PcapsTool.printDevice();
	    	return null;
	    }
		
	    try{
	    	return new BeaconRecvEthernet(JpcapCaptor.openDevice(device, 2000, false, 20),"");
	    }catch (java.io.IOException e){
	    	System.out.println("Cannot open network interface : "+e);
	    	return null;
	    }
	}
	
	/**
	 * BeaconRecv factory. Create a BeaconRecv from a pcaps file
	 * @param path file name
	 * @return
	 */
	public static BeaconRecvEthernet loopPacketFromFile(String path){
	    try{
	    	return new BeaconRecvEthernet(JpcapCaptor.openFile(path),100,"");
	    }catch (java.io.IOException e){
	    	System.out.println("Cannot open file : "+e);
	    	return null;
	    }
	}
	
	
	/*--------------------------------------------------------------------------------
	 * Unit testing
	 */

	private static BeaconRecvListener createPrintListener(){
		return new BeaconRecvListener() {

			public void cmoStatChanged(CMOState stat) {
				System.out.println(stat);
			}

		};
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
		
		BeaconRecv bRecv=null;
		
		if(args[0].compareTo("-i")==0)
			bRecv = loopPacketFromDevice(args[1]);
		else if(args[0].compareTo("-f")==0)
			bRecv = loopPacketFromFile(args[1]);
		else{
			System.out.println("Bad argument");
			printUsage();
			System.exit(1);					
		}
		
    	bRecv.addListener(createPrintListener());
    	bRecv.start();  
    	bRecv.join();
    	 	
		
	}


}
