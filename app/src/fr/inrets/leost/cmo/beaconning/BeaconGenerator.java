package fr.inrets.leost.cmo.beaconning;


import jpcap.*;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

import fr.inrets.leost.geolocation.Geolocation;
import fr.inrets.leost.geolocation.Gps;
import fr.inrets.leost.geolocation.WGS84;
import fr.inrets.leost.cmo.utils.PcapsTool;
import fr.inrets.leost.cmo.beaconning.packet.*;

/**
 * 
 * broadcast CMO stat beacon from geolocation data
 * 
 *                    CMO config ----
 *                                  |
 *                                 \|/
 * Geolocation (GPS) --------> BeaconGenerator ----|CMOState|----> JPcap (row ethernet)
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 *
 */
public class BeaconGenerator extends Thread{
	/** freqency interval between each packet send (ms)*/
	public static final int BEACON_FREQ_DEFAULT = 500;
	
	/** beacon lifetime in number of interval value
	 *  between each beacon*/
	public static final int BEACON_LIFETIME = 4;
	
	
	/**
	 * Value for initialize the TTL (number of hop maximum)
	 */
	public static final byte TTL_INIT = 16;
	
	
	private static int seq=0;

	private String id;
	private short type;
	private int beaconFreq;
	
	private Geolocation loc;
	private JpcapSender sender;

	/**
	 * 
	 * @param sender Jpcap sender
	 * @param loc localisation system (ex. GPS)
	 * @param id CMO id
	 * @param type CMO type
	 * @param beaconFreq interval between two beacon
	 */
	public BeaconGenerator(JpcapSender sender, Geolocation loc,String id, short type, int beaconFreq) {
		this.id = id;
		this.type = type;
		this.beaconFreq = beaconFreq;
		this.sender = sender;
		this.loc = loc;
	}

	/**
	 * The beacon frequency is {@value #BEACON_FREQ_DEFAULT}
	 * @param sender Jpcap sender
	 * @param id CMO id
	 * @param type CMO type
	 */
	public BeaconGenerator(JpcapSender sender, Geolocation loc, String id, short type){
		this(sender, loc, id, type, BEACON_FREQ_DEFAULT);
	}
	
	private Packet createCMOStatPacket(Float longitude, Float latitude, Float h, Float speed,
			Float track){
		
		Packet p = new Packet();

		CMOHeader cmo_header = new CMOHeader((byte)TTL_INIT, seq++, beaconFreq * BEACON_LIFETIME, id, type);
		
		CMOState cmo_stat = new CMOState (cmo_header,longitude, latitude, h, speed, track);
		
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=CMOHeader.ETHERTYPE_CMO;
		//set source and destination MAC addresses
		ether.src_mac=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
		ether.dst_mac=new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};		
		
		
		p.datalink = ether;
		p.data = cmo_stat.toByteArray();
		
		return p;
	}
	
	
	/** 
	 * broadcast a CMOStat packet
	 */
	private void broadcastCMOStatPacket(WGS84 position, Double speed, Double track) {

		System.out.println("Beacon " + position + " " +  speed + " " + track);
		
		sender.sendPacket(createCMOStatPacket(
				new Float(position.longitude()), 
				new Float(position.latitude()), 
				new Float(position.h()),
				new Float(speed),
				new Float(track)));
		
	}	
	
	/** 
	 * broadcast a CMOStat packet with location information
	 */
	private void broadcastCMOStatPacket(){
		broadcastCMOStatPacket( loc.getCurrentPos(), loc.getCurrentSpeed(), loc.getCurrentTrack() );
	}
	
	public void run() {

	    while(true) {
	        try {
	        	sleep(beaconFreq);

	    		broadcastCMOStatPacket();
	        } catch (InterruptedException ie) {}
	    }		
	}

	/**
	 * @return the curent sequence number
	 */
	public static int getSeq() {
		return seq;
	}

	/**
	 * @return the CMO identity
	 */
	public String getCMOId() {
		return id;
	}

	/**
	 * @return the type of CMO
	 */
	public short getCMOType() {
		return type;
	}

	/**
	 * @return the beaconning freqency
	 */
	public int getBeaconFreq() {
		return beaconFreq;
	}
	
	
	
	/*--------------------------------------------------------------------------------
	 * Unit testing / IHM
	 */

	public static void runGenerator(JpcapSender sender,String strId, String strType, String strBeaconInter){

	    short type=CMOHeader.typeFromString(strType);
	    
	    if(type==-1){		    
	    	System.out.println("The CMO type " + strType + " doesn't exist");
	    	System.out.println("\tCMO type available " + CMOHeader.getTypeAvailable());
	    	return;
	    }
	    
	    int beaconInter = Integer.parseInt(strBeaconInter);
	    
		try{
			Geolocation loc = new Gps();
			loc.setUpdateInterval((int)((float)beaconInter/2.0));
			loc.start();

			
	    	BeaconGenerator gen = new BeaconGenerator(sender, loc, strId,type,beaconInter);
	    	gen.run();	  
		}catch(java.io.IOException e){
			System.out.println("Cannot init the geolocation system : " + e);
		}
	    
  
	}
	
	
	public static void runGeneratorFromDevice(String strDevice, String strId, String strType, String strBeaconInter) {
		    NetworkInterface device = PcapsTool.toNetworkInterface(strDevice);
		    
		    if(device==null){
		    	System.out.println("The interface " + strDevice + " doesn't exist");
		    	PcapsTool.printDevice();
		    	return;
		    }
			
		    try{

		    	runGenerator(JpcapSender.openDevice(device), strId, strType, strBeaconInter);
		    	
		    }catch (java.io.IOException e){
		    	System.out.println("Cannot open network interface : "+e);
		    	return;
		    }
	}


	/**
	 * @param args
	 */
	public static void main(String[] args)   {
		
		if(args.length<4){
			System.out.println("Not enough arguments");
			System.out.println("Usage : java BeaconGenerator <device> <cmo id> <cmo tpye> <beacon interval>");			
			System.exit(1);
		}
		
		runGeneratorFromDevice(args[0],args[1],args[2],args[3]);

	}





}
