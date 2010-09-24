/**
 * 
 * Listener for receive the state change
 * 
 * @author Florent Kaisser
 *
 */

public interface CMOStateListener {
	
	/**
	 * A CMO stat has changed
	 * @param stat new state of a CMO
	 */
	void cmoStatChanged(CMOState stat);
}

