import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;


public final class PcapsTool {
	public static NetworkInterface toNetworkInterface(String strDevice){
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		
		for(int i=0;i<devices.length;i++){
			if (devices[i].name.compareToIgnoreCase(strDevice) == 0)
				return devices[i];
		}
		
		return null;
	}
	
	

	public static void printDevice(){
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		
		if(devices.length==0)
			System.out.println("No network interface. Do you have super user privilege ?");

		for(int i=0;i<devices.length;i++)
			System.out.println(i+":"+devices[i].name+"("+devices[i].description+")");	
	}
}
