package fr.inrets.leost.weather;

public class Fake extends Weather {

	public Fake(String metar){
		WeatherData data = new WeatherData(metar);
		
		setCurrentCondition(data);
	}

	public static void main (String[] args) throws Exception{
		Weather w = new Fake("LFPG 191400Z 30005KT 250V320 9999 FEW046 BKN250 24/11 Q1020 NOSIG");
		
		w.addWeatherListener(new WeatherListener() {

			public void weatherChanged(WeatherData data) {
				System.out.println("Weather : " + data);
			}

		});
	}	


	public  void dispose(){}
} 
