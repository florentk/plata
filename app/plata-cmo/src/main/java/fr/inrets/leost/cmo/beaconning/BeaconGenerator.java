package fr.inrets.leost.cmo.beaconning;


import org.apache.log4j.Logger;

import jpcap.*;
import fr.inrets.leost.geolocation.Geolocation;
import fr.inrets.leost.geolocation.GeolocationListener;
import fr.inrets.leost.geolocation.Gps;
import fr.inrets.leost.geolocation.Trace;
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
 * @has 1 - - Geolocation
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

	/** init the logger */
	private static Logger logger = Logger.getLogger(BeaconGenerator.class);		
	
	private String id;
	private short type;
	private int beaconFreq;
	private BeaconSender sender;
	private Geolocation loc;


	/**
	 * 
	 * @param sender Jpcap sender
	 * @param loc localisation system (ex. GPS)
	 * @param id CMO id
	 * @param type CMO type
	 * @param beaconFreq interval between two beacon
	 */
	public BeaconGenerator(BeaconSender sender, Geolocation loc,String id, short type, int beaconFreq) {
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
	public BeaconGenerator(BeaconSender sender, Geolocation loc, String id, short type){
		this(sender, loc, id, type, BEACON_FREQ_DEFAULT);
	}
	
	public static  byte[] createCMOStatPacket(byte ttl, int seq, int lifetime, String cmoID,
			short cmoType,Float longitude, Float latitude, Float h, Float speed,
			Float track, int time){
		
		CMOHeader cmo_header = new CMOHeader(ttl, seq,lifetime, cmoID, cmoType);
		CMOState cmo_stat = new CMOState (cmo_header,longitude, latitude, h, speed, track, time);

		return cmo_stat.toByteArray();		
	}
	
	private byte[] createCMOStatPacket(Float longitude, Float latitude, Float h, Float speed,
			Float track, int time){
		return createCMOStatPacket((byte)TTL_INIT, seq++, beaconFreq * BEACON_LIFETIME, id, type,longitude, latitude, h, speed, track, time);
	}
	
	
	/** 
	 * broadcast a CMOStat packet
	 */
	private void broadcastCMOStatPacket(int t, WGS84 position, Double speed, Double track) {

		logger.info("send_cmo_packet: " + t + " " + position + " " +  speed + " " + track);
		
		byte[] data = createCMOStatPacket(
				new Float(position.longitude()), 
				new Float(position.latitude()), 
				new Float(position.h()),
				new Float(speed),
				new Float(track),
				t);
		
		sender.broadcastData(data);
		
	}	
	
	/** 
	 * broadcast a CMOStat packet with location information
	 */
	private void broadcastCMOStatPacket(){
		broadcastCMOStatPacket(loc.getTime(), loc.getLastPos(), loc.getCurrentSpeed(), loc.getCurrentTrack() );
	}
	
	public void run() {

	    while(true) {
	        try {
	        	sleep(beaconFreq);
	        	if(loc.isReady())
	        		broadcastCMOStatPacket();
	        } catch (InterruptedException ie) {}
	    }		
	}
	
	public void listen(){
		loc.addPositionListener(new GeolocationListener() {

			public void positionChanged(WGS84 position, Double speed, Double track) {
				broadcastCMOStatPacket();
			}

		});
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
	

	public static void runGenerator(JpcapSender sender, Geolocation loc, String strId, short type, int beaconInter){

	    
	    if(type==-1){		    
	    	System.out.println("The CMO type doesn't exist");
	    	System.out.println("\tCMO type available " + CMOHeader.getTypeAvailable());
	    	return;
	    }

		loc.start();

		
    	BeaconGenerator gen = new BeaconGenerator(new BeaconSenderEthernet(sender), loc, strId,type,beaconInter);
    	
    	if(loc instanceof Trace)
    		gen.listen();
    	else
    		gen.run();	  
    	
    	loc.dispose();
	}
	
	
	public static void runGeneratorFromDevice(String strDevice, String traceFile, int waitTime, String strId, short type, int beaconInter) {
		    NetworkInterface device = PcapsTool.toNetworkInterface(strDevice);
		    
		    if(device==null){
		    	System.out.println("The interface " + strDevice + " doesn't exist");
		    	PcapsTool.printDevice();
		    	return;
		    }
			
		    try{
		    	
		    	if (traceFile==null)
		    		runGenerator(JpcapSender.openDevice(device), new Gps(),strId, type, beaconInter);
		    	else
		    		runGenerator(JpcapSender.openDevice(device), Trace.traceFromFile(traceFile,waitTime),strId, type, beaconInter);
		    		
		    }catch (java.io.IOException e){
		    	System.out.println("Cannot open network interface or init the geolocation system : "+e);
		    	return;
		    }
	}


	/**
	 * @param args
	 */
	public static void main(String[] args)   {
		
		if(args.length<4){
			System.out.println("Not enough arguments");
			System.out.println("Usage : java BeaconGenerator <device> <cmo id> <cmo type> <beacon interval> [<trace file> <wait time>]");			
			System.exit(1);
		}
		
		if(args.length>5)
			runGeneratorFromDevice(args[0], args[4], Integer.parseInt(args[5]) ,args[1], Short.parseShort(args[2]), Integer.parseInt(args[3]));
		else
			runGeneratorFromDevice(args[0], null, 0 ,args[1], Short.parseShort(args[2]), Integer.parseInt(args[3]));
			
	}





}
