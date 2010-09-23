
import jpcap.*;
import jpcap.packet.Packet;
import jpcap.packet.EthernetPacket;


public class BeaconRecv  implements PacketReceiver {
	public void receivePacket(Packet packet) {
		

		if (packet.datalink instanceof EthernetPacket){
			EthernetPacket ether = (EthernetPacket) packet.datalink;
			
			if (ether.frametype == CMOHeader.ETHERTYPE_CMO){

				
				CMOState cmo = new CMOState(packet.data);
				System.out.println(cmo);
				
			}
				
		}
			
	}

	public static void main(String[] args) throws Exception {
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		if(args.length<1){
			System.out.println("usage: java BeaconRecv <select a number from the following>");
			
			for (int i = 0; i < devices.length; i++) {
				System.out.println(i+" :"+devices[i].name + "(" + devices[i].description+")");
				System.out.println("    data link:"+devices[i].datalink_name + "("
						+ devices[i].datalink_description+")");
				System.out.print("    MAC address:");
				for (byte b : devices[i].mac_address)
					System.out.print(Integer.toHexString(b&0xff) + ":");
				System.out.println();
				for (NetworkInterfaceAddress a : devices[i].addresses)
					System.out.println("    address:"+a.address + " " + a.subnet + " "
							+ a.broadcast);
			}
		}else{
			JpcapCaptor jpcap = JpcapCaptor.openDevice(devices[Integer.parseInt(args[0])], 2000, false, 20);
			//JpcapCaptor jpcap = JpcapCaptor.openFile(args[0]);
			
		
			jpcap.loopPacket(-1, new BeaconRecv());
		}
	}	
}
