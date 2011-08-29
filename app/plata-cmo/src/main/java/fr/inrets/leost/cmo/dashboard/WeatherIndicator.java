package fr.inrets.leost.cmo.dashboard;

import fr.inrets.leost.weather.*;

public class WeatherIndicator implements Indicator {
	private Weather weather;
	
	public WeatherIndicator(Weather weather) {
		this.weather = weather;
	}

	@Override
	public void update() {
	}
	
	@Override
	public String name(){
		return "Weather";
	}
	

	
	private String makeString(){
		StringBuffer str = new StringBuffer();
		
		if (weather.validData()){ 
		
			if(weather.getWindSpeed() != 0)
				str.append(weather.getWindSpeed() + " km/h ");
		
			if(weather.getWindGusts() != 0)
				str.append("(" + weather.getWindGusts()  + ") ");		
		
			str.append(weather.getTemperature() + "°C ");

			if( weather.isSun() ) str.append("Sun with ");
			if( weather.isMoon() ) str.append("Moon with ");		
		
			if(weather.isOvercloud()) str.append(" Overcast");
		 		else if(weather.isCloud()) str.append(" cloud");
		 		
		 	if(weather.isThunderstorms()) str.append(" thunderstorms ");
		 	if(weather.isFog()) str.append(" fog ");	 	
		 	
		 	if(weather.isRain()) str.append(" rain ");
		 	
		 	if(weather.isSnow()) str.append(" snow ");
		 	
		 	if(weather.isRainfall() || weather.isSnowfall()) str.append(" fall ");	 			
		}
		return str.toString();
	}
	
	public String toString(){
		if (weather.validData())
			return makeString();
		else
			return "N/A";
	}

}
