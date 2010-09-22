

import java.net.InetAddress;

import jpcap.*;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;


public class BeaconGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws java.io.IOException {
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		if(args.length<1){
			System.out.println("Usage: java BeaconGenerator <device index (e.g., 0, 1..)>");
			for(int i=0;i<devices.length;i++)
				System.out.println(i+":"+devices[i].name+"("+devices[i].description+")");
			System.exit(0);
		}
		int index=Integer.parseInt(args[0]);
		JpcapSender sender=JpcapSender.openDevice(devices[index]);
		
		

	}

}
