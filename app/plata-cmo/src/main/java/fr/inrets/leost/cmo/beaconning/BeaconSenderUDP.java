package fr.inrets.leost.cmo.beaconning;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class BeaconSenderUDP implements BeaconSender {
	/** constants */
	public static final int DEFAULT_PORT = 50123;
	public static final String DEFAULT_BROADCAST_ADDRESS =  "192.168.0.255";

	/** init the logger */
	private static Logger logger = Logger.getLogger(BeaconSender.class);	
	
	private DatagramSocket socket = null;
	private int port = DEFAULT_PORT;
	
	private InetAddress broacastAddress = null;

    public BeaconSenderUDP(DatagramSocket socket, int port, String address) {
		super();
		this.port = port;
		this.socket = socket;
		try {
			this.broacastAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			logger.error(e);
		}
	}
    
    public BeaconSenderUDP(DatagramSocket socket) {
    	this(socket, DEFAULT_PORT,DEFAULT_BROADCAST_ADDRESS);
	}
    
    public BeaconSenderUDP(DatagramSocket socket, int port) {
    	this(socket, port, DEFAULT_BROADCAST_ADDRESS);
	}   

	
    private InetAddress getBroadcastAddress() throws IOException {
        return broacastAddress;
    }
	
	@Override
	public void broadcastData(byte[] data) {
        if (socket == null) {
        	System.err.println("Socket is not initialized");
            return;
        }

        try{
            DatagramPacket packet = new DatagramPacket(data, data.length, getBroadcastAddress(), port);
            socket.send(packet);
        } catch (IOException e) {
            logger.error("Could not send discovery request " + e);
        }
	}
	
	public static DatagramSocket initUDP(int port){
        try{
        	DatagramSocket socket = new DatagramSocket(port);
            socket.setBroadcast(true);
            return socket;
        } catch (IOException e) {
        	System.err.println("Could not create UDP socket " + port);
        	return null;
        }
    }
	
	public static DatagramSocket initUDP(){
        try{
        	DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            return socket;
        } catch (IOException e) {
        	System.err.println("Could not create UDP socket " + e);
        	return null;
        }
    }	  	

}
