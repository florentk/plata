package fr.inrets.leost.cmo.beaconning;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

import fr.inrets.leost.cmo.beaconning.packet.*;


/**
 * 
 * JpcapCaptor (raw Ethernet) ----> BeaconRecv ----|CMOState|----> CMOStatListener
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 *
 */

public class BeaconRecvUDP extends  BeaconRecv {

    DatagramSocket socket = null;
	int delay;
	String myCmoId;
	
	/** init the logger */
	private static Logger logger = Logger.getLogger(BeaconRecvUDP.class);	

	public BeaconRecvUDP(DatagramSocket socket, int delay, String myCmoId){
		super();
		this.delay = delay;
		this.myCmoId = myCmoId;
		this.socket = socket;
	}
	
	
	public BeaconRecvUDP(DatagramSocket socket,String myCmoId){
		this(socket, 0,myCmoId);
	}	


	//receive a packet from UDP
	public void receivePacket(byte[] data) {
		
		if(delay!=0){
			try{sleep(100);}catch(InterruptedException e){}
		}

		//decode packet
		CMOState cmo = new CMOState(data);
		
		logger.info("receive_cmo_packet: "+cmo);
		
		//drop packet from me and expired packet
		if(cmo.getCmoID().compareTo(myCmoId)!=0){
			//notify the listerners
			notifyListener(cmo);
		}
	}
	

	public void run(){
		byte[] data = new byte[256];
		
        if (socket == null) {
        	System.err.println("Socket is not initialized");
            return;
        }		
		
		try{
			while(true)
			{
				DatagramPacket packet = new DatagramPacket(data, data.length);
				socket.receive(packet);
				receivePacket(packet.getData());
			}
		} catch (IOException e) {
			logger.error("Could not receive packet: " + e);
		}
	}
	

	
	/* BeaconRecv factory */
	
	/**
	 * BeaconRecv factory. Create a BeaconRecv for UDP
	 * @param strDevice interface name
	 * @return
	 */
	public static BeaconRecvUDP loopPacketForUDP(){
		return new BeaconRecvUDP(BeaconSenderUDP.initUDP() , "");
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
	

	public static void main(String[] args) throws Exception {

		BeaconRecv bRecv=loopPacketForUDP();
  	bRecv.addListener(createPrintListener());
  	bRecv.start();  
  	bRecv.join();
    	 	
	}


}
