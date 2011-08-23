package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.weather.*;
import net.sf.jweather.metar.*;

public class WeatherIndicator implements Indicator {
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
	
	public String toString(){
		return station;
	}

}
