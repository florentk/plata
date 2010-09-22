
/**
 * Listener for receive a event when the geographical position change 
 * 
 * @author florent
 *
 */
public interface GpsListener {
	/**
	 * the geographical position has changed
	 * @param position geographical position in WGS84 format
	 */
	void positionChanged(WGS84 position);
}
