package fr.inrets.leost.cmo.beaconning;

import java.util.ArrayList;
import java.util.Collection;

import fr.inrets.leost.cmo.beaconning.packet.*;



public class BeaconRecvFake extends BeaconRecv{
	private final Collection<CMOState> cmos = new ArrayList<CMOState>();
	

	
	public void addFixedCMO(CMOState cmo){
		cmos.add(cmo);
	}
	


	public void run() {
		while(true){
		
			try{
			
				for ( CMOState cmo : cmos )
					//notify the listerners
					notifyListener(cmo);
				
				sleep(1000);
			
			}catch(InterruptedException e){
				
			}
		}
	}
	
	
	
	
	private static BeaconRecvListener createPrintListener(){
		return new BeaconRecvListener() {

			public void cmoStatChanged(CMOState stat) {
				System.out.println(stat);
			}

		};
	}	
	
	
	public static void main(String[] args) throws Exception {
		BeaconRecvFake bRecv = new BeaconRecvFake();
		
		bRecv.addFixedCMO(new CMOState(
				new CMOHeader((byte)100, 0, 5000, "GR-487-AZ",CMOHeader.CMO_TYPE_CAR ),
				3.1383562088f,
				50.6111869812f,
				0.0f,
				1.0f,
				56.0f));
		
		bRecv.addFixedCMO(new CMOState(
				new CMOHeader((byte)100, 0, 5000, "AZ-197-UY",CMOHeader.CMO_TYPE_CAR ),
				3.12586784363f,
				50.6021995544f,
				0.0f,
				1.0f,
				56.0f));
		
		
		

    	bRecv.addListener(createPrintListener());
    	bRecv.start();  
    	bRecv.join();
	}


}
