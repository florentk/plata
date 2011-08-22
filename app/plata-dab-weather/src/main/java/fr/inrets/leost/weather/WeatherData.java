package fr.inrets.leost.weather;

/*"clouds":"few clouds"
"weatherCondition":"n/a"


"observation":"LSZH 191320Z 28009KT 9999 FEW050 28/19 Q1019 NOSIG",

"windDirection":280,
"ICAO":"LSZH",
"elevation":432,
"countryCode":"CH",
"lng":8.516666666666667,
"temperature":"28",
"dewPoint":"19",
"windSpeed":"09",
"humidity":58,
"stationName":"Zurich-Kloten",
"datetime":"2011-08-19 13:20:00",
"lat":47.46666666666667,
"hectoPascAltimeter":1019}}*/

public class WeatherData {
	private String metar;
	
	public WeatherData(String metar){
		this.metar = metar;
	}
	
	public String getMetar() {
		return metar;
	}
	
	public String toString(){
		return metar;
	}
}
