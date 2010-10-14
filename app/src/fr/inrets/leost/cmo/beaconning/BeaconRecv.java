package fr.inrets.leost.cmo.beaconning;

import java.util.ArrayList;
import java.util.Collection;

import fr.inrets.leost.cmo.beaconning.packet.CMOState;


/**
 * generalisation of a beacon receiver
 * @author florent kaisser <florent.kaisser@free.fr>
 * @has 0..* - -  BeaconRecvListener
 */
public class BeaconRecv extends Thread{

	private final Collection<BeaconRecvListener> listerners = new ArrayList<BeaconRecvListener>();
	
	public void addListener(BeaconRecvListener l){
		listerners.add(l);
	}
	
	public void removeListener(BeaconRecvListener l){
		listerners.remove(l);
	}
	
	protected void notifyListener(CMOState stat){
	
		for ( BeaconRecvListener l : listerners )
			l.cmoStatChanged(stat);	
	
	}
}