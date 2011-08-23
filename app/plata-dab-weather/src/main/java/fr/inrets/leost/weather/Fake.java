package fr.inrets.leost.weather;

import net.sf.jweather.metar.*;

public class Fake extends Weather {

	public Fake(String metar){
		try {
			Metar data = MetarParser.parseReport(metar);
			setCurrentCondition(data);
		}catch (MetarParseException e){
		}
	}

	public static void main (String[] args) throws Exception{
		Weather w = new Fake("LFPG 191400Z 30005KT 250V320 9999 FEW046 BKN250 24/11 Q1020 NOSIG");
		
		w.addWeatherListener(new WeatherListener() {

			public void weatherChanged(Metar data) {
				System.out.println("Weather : " + data.getStationID());
			}

		});
	}	


	public  void dispose(){}
} 
