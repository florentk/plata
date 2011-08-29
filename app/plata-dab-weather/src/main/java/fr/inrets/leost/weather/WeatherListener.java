package fr.inrets.leost.weather;

import net.sf.jweather.metar.*;

/**
 * Listener for receive a event when the weather condition change
 * 
 * @author florent
 */
public interface WeatherListener {
	/**
	 * the weather condition has changed
	 * @param data data of current weather
	 */
	public void weatherChanged(Weather w);
}

