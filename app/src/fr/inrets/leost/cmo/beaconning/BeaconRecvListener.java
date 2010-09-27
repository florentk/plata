package fr.inrets.leost.cmo.beaconning;

import fr.inrets.leost.cmo.packet.*;

/**
 * 
 * Listener for receive the state change
 * 
 * @author Florent Kaisser
 *
 */

public interface BeaconRecvListener {
	
	/**
	 * A CMO stat has changed
	 * @param stat new state of a CMO
	 */
	void cmoStatChanged(CMOState stat);
}

