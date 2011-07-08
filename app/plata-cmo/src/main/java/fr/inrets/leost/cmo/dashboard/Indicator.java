package fr.inrets.leost.cmo.dashboard;

/**
 * Interface for all indicator
 * @author florent kaisser
 *
 */
public interface Indicator {
	/**
	 * the data (my position and neighborhood position) is update
	 */
	void update();
	
	/**
	 * 
	 * @return the show name of indicator
	 */
	String name();
}
