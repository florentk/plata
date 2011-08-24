package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.weather.*;
import java.util.Iterator;
import net.sf.jweather.metar.*;

public class WeatherIndicator implements Indicator {
	public static final double KMH = 1.609344d;

	private Weather weather;

	private String station="";
	private Metar data;
	
	public WeatherIndicator(Weather weather) {
		this.weather = weather;
	}

	@Override
	public void update() {
		data = weather.getCurrentCondition();
		if (data != null)
			station = data.getStationID();
	}
	
	public Metar getMetar(){
		return data;
	}
	
	
	@Override
	public String name(){
		return "Weather";
	}
	
	private int mphToKmh (double s){
		return new Double(s*KMH).intValue();
	}
	
	private String makeString(){
		StringBuffer str = new StringBuffer();
		
		str.append(mphToKmh(data.getWindSpeedInMPH()) + " km/h ");
		
		if(data.getWindGustsInMPH() != null)
			str.append("(" + mphToKmh(data.getWindGustsInMPH())  + ") ");		
		
		str.append(data.getTemperatureMostPreciseInCelsius() + "°C ");
		
		if (data.getWeatherConditions() != null) {
			Iterator i = data.getWeatherConditions().iterator();
			while (i.hasNext()) {
				WeatherCondition weatherCondition = (WeatherCondition)i.next();
				str.append(weatherCondition.getNaturalLanguageString() + " ");
			}
		}
		if (data.getSkyConditions() != null) {
			Iterator i = data.getSkyConditions().iterator();
			while (i.hasNext()) {
				SkyCondition skyCondition = (SkyCondition)i.next();
				str.append(skyCondition.getNaturalLanguageString() + " ");
			}
		}
		
		return str.toString();
	}
	
	public String toString(){
		if (data == null)
			return "N/A";
		else
			return makeString();
	}

}
