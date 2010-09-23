

import java.net.InetAddress;

import jpcap.*;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

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
		
		Packet p = new Packet();
		
		CMOHeader cmo_header = new CMOHeader((byte)0, 1, 5000, "AB-123-CD", (short)CMOHeader.CMO_TYPE_CAR);
		
		CMOState cmo_stat = new CMOState (cmo_header,5.54f,28.78f,147.0f,1.68f,54.0f);
		
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=CMOHeader.ETHERTYPE_CMO;
		//set source and destination MAC addresses
		ether.src_mac=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
		ether.dst_mac=new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};		
		
		
		p.datalink = ether;
		p.data = cmo_stat.toByteArray();
		
		System.out.println(p.data.length);
		
		sender.sendPacket(p);
	}

}
