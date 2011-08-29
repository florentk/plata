package fr.inrets.leost.weather;

import java.util.*;

import net.sf.jweather.metar.*;

public abstract class Weather extends Thread {
	public static final double KMH = 1.609344d;
	
	/** collection of listener for receive a event on weather condition change*/
	private final Collection<WeatherListener> weatherListeners = new ArrayList<WeatherListener>();
	private Metar currentCondition = null;
	private boolean sun, moon, cloud, overcloud, thunderstorms, rain, rainfall, snow, snowfall, fog;

    private void reset() {
    	sun = false;
    	moon = false;
    	cloud = false;
    	overcloud = false;
    	thunderstorms = false;
    	rain = false;
    	rainfall = false;
    	snow = false;
    	snowfall = false;
    	fog = false;
    }
    
    private void setAstre(boolean astre) {
    	sun = astre;
    }

    private void setSky(SkyCondition sc){
 		if (sc.isClear()) {
                  setAstre(true);
		} else if (sc.isFewClouds() || sc.isScatteredClouds() || sc.isBrokenClouds()) {
                  setAstre(true);		
                  cloud = true;              
		} else if (sc.isOvercast()) {
                  cloud = true;
                  overcloud = true;
		} else if (sc.isNoSignificantClouds()) {
                  setAstre(true);
        }
    }
    
    
    private void setWeather(WeatherCondition wc){
        if (wc.isThunderstorms()) {
			thunderstorms = true;
        }

		if (wc.isDrizzle()) {
			rain = true;
			if (wc.isHeavy()) rainfall = true;
		} else if (wc.isShowers() || wc.isRain() || wc.isHail() || wc.isSmallHail()) {
			rain = true;
			if (wc.isHeavy()) rainfall = true;
		} else if (wc.isSnow() || wc.isSnowGrains() || wc.isIceCrystals() || wc.isIcePellets()) {
			snow = true;
			if (wc.isHeavy()) snowfall = true;
		} else if (wc.isMist() || wc.isFog()) {
			fog = true;
		} 
    } 
    
    private int mphToKmh (double s){
		return new Double(s*KMH).intValue();
	}
	
	/**
	 * must be call when the current weather change
	 */
	private void weatherChanged(){
      	if(currentCondition != null) {
      		reset();
      	
			if (currentCondition.getWeatherConditions() != null) {
				Iterator i = currentCondition.getWeatherConditions().iterator();
				while (i.hasNext()) 
					setWeather((WeatherCondition)i.next());
			}
			if (currentCondition.getSkyConditions() != null) {
				Iterator i = currentCondition.getSkyConditions().iterator();
				while (i.hasNext()) 
					setSky((SkyCondition)i.next());
			}
		}
	
		//call the method positionChanged of each registered listener
		for (WeatherListener l : weatherListeners)
			l.weatherChanged(this);
	}	
	

	/**
	 * set the current condition
	 * @param currentPos the current position in WGS84 format
	 */
	protected void setCurrentCondition(Metar currentCondition) {
		this.currentCondition = currentCondition;
		weatherChanged();
	}
	
	public boolean validData() {return currentCondition != null;}
	public double getTemperature() {return currentCondition.getTemperatureMostPreciseInCelsius();}
	public int getWindSpeed() {return mphToKmh(currentCondition.getWindSpeedInMPH());}
	public int getWindGusts() {
		if(currentCondition.getWindGustsInMPH() != null)
			return mphToKmh(currentCondition.getWindGustsInMPH());
		else
			return 0;
	}
	public boolean isSun (){
		return sun;
	}
	public boolean isMoon (){
		return moon;
	}
	public boolean isCloud (){
		return cloud;
	}
	public boolean isOvercloud (){
		return overcloud;
	}
	public boolean isThunderstorms (){
		return thunderstorms;
	}
	public boolean isRain (){
		return rain;
	}
	public boolean isRainfall (){
		return rainfall;
	}
	public boolean isSnow (){
		return snow;
	}
	public boolean isSnowfall (){
		return snowfall;
	}
	public boolean isFog (){
		return fog;
	}
	
	public String getStationID(){
		return currentCondition.getStationID();
	}
	
	
	
	/**
	 * get the current condition 
	 * @return the current weather
	 */
	protected Metar getCurrentCondition() {
		return currentCondition;
	}
	
	
	/**
	 * register a new listener
	 * @param l
	 */
	public void addWeatherListener(WeatherListener l){
		weatherListeners.add(l);
		if (currentCondition!=null) l.weatherChanged(this);
	}
	
	/**
	 * remove a registered listener
	 * @param l
	 */
	public void removeWeatherListener(WeatherListener l){
		weatherListeners.remove(l);
	}
	
	/**
	 * call for clean the devise
	 */
	public abstract void dispose();		
}
