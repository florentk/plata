package fr.inrets.leost.weather;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Weather {
	/** collection of listener for receive a event on weather condition change*/
	private final Collection<WeatherListener> weatherListeners = new ArrayList<WeatherListener>();
	private WeatherData currentCondition = null;
	
	/**
	 * must be call when the current weather change
	 */
	private void weatherChanged(){
		//call the method positionChanged of each registered listener
		for (WeatherListener l : weatherListeners)
			l.weatherChanged(currentCondition);
	}	
	

	/**
	 * set the current condition
	 * @param currentPos the current position in WGS84 format
	 */
	protected void setCurrentCondition(WeatherData currentCondition) {
		this.currentCondition = currentCondition;
		weatherChanged();
	}
	
	/**
	 * get the current condition 
	 * @return the current weather
	 */
	public WeatherData getCurrentCondition() {
		return currentCondition;
	}
	
	
	/**
	 * register a new listener
	 * @param l
	 */
	public void addWeatherListener(WeatherListener l){
		weatherListeners.add(l);
		l.weatherChanged(currentCondition);
	}
	
	/**
	 * remove a registered listener
	 * @param l
	 */
	public void removeWeatherListener(WeatherListener l){
		weatherListeners.remove(l);
	}	
}
