package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.weather.*;

public class WeatherIndicator implements Indicator {
	private Weather weather;

	private String metar="";
	
	public WeatherIndicator(Weather weather) {
		this.weather = weather;
	}

	@Override
	public void update() {
		WeatherData data = weather.getCurrentCondition();
		if (data != null)
			metar = data.getMetar();
	}
	
	public String getMetar(){
		return metar;
	}
	
	
	@Override
	public String name(){
		return "Weather";
	}
	
	public String toString(){
		return metar;
	}

}
