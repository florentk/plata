
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
	 * @param speed speed in meter per second
	 * @param track orientation in degree (0 to 360)
	 */
	void positionChanged(WGS84 position, Double speed, Double track);
}
