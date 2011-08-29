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

	public  void dispose(){}
} 
