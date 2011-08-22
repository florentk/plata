package fr.inrets.leost.weather.dab;

import java.net.*;
import java.io.IOException;
import java.util.Map;

import org.json.simple.parser.*;

import fr.inrets.leost.weather.*;

public class WeatherDAB extends Weather {
	public static final int PACKET_MAX_SIZE = 1024;
	private DatagramSocket socket;
	private boolean actif = true;
	
	public WeatherDAB(InetAddress address, int port)   {
		try {
			socket = new DatagramSocket(port, address);
		} catch(IOException ioe) {
			System.err.println("Unable create socket : " + ioe);
		}
	}

	public String readUDPData()  throws IOException {
		byte[] buffer = new byte[PACKET_MAX_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		socket.receive(packet);
		
		return new String(packet.getData(), 0, packet.getLength(), "US-ASCII");
	}
	
	private WeatherData decodeFromJSON(String str) throws ParseException{
		//convert JSON string in Java Map
		Map dict = (Map)(new JSONParser()).parse(str);
		Map wMap = (Map)dict.get("weatherObservation");
		
		//get the METAR
		return new WeatherData((String)wMap.get("observation"));
	}
	
	/**
	 * read weather data from UDP socket
	 */
	public void run() {
		while(actif) try {
			setCurrentCondition(decodeFromJSON(readUDPData()));
		} catch(IOException ioe) {
			System.err.println("Unable recevie UDP data" + ioe);
		} catch(ParseException pe){
			System.err.println("JSon parsing error: " + pe.getPosition());
			System.err.println(pe);
		}			
	}
	
	public void dispose() {
		actif = false;
		socket.close();
	}
	
/*	public static void main (String[] args) throws IOException{
		WeatherDAB w = new WeatherDAB(InetAddress.getLocalHost(),6666);
		
		w.addWeatherListener(new WeatherListener() {
			public void weatherChanged(WeatherData data) {
				System.out.println("Weather : " + data);
			}

		});
		
		w.run();
	}	*/

}


