package fr.inrets.leost.cmo.beaconning;

import jpcap.*;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import fr.inrets.leost.cmo.beaconning.packet.CMOHeader;

public class BeaconSenderEthernet implements BeaconSender {

	private JpcapSender sender;
	
	public BeaconSenderEthernet(JpcapSender sender) {
		super();
		this.sender = sender;
	}

	@Override
	public void broadcastData(byte[] data) {

		EthernetPacket ether=new EthernetPacket();
		ether.frametype=CMOHeader.ETHERTYPE_CMO;
		//set source and destination MAC addresses
		ether.src_mac=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
		ether.dst_mac=new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};		
		
		Packet p = new Packet();
		p.datalink = ether;
		p.data = data;
		
		sender.sendPacket(p);
	}
	


}
