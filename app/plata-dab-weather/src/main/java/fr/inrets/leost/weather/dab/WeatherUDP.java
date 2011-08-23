package fr.inrets.leost.weather.dab;

import java.net.*;
import java.io.IOException;
import java.util.Map;
	
import net.sf.jweather.metar.*;
import fr.inrets.leost.weather.*;

public class WeatherUDP extends Weather {
	public static final int PACKET_MAX_SIZE = 1024;
	private DatagramSocket socket;
	private boolean actif = true;
	
	public WeatherUDP(InetAddress address, int port)   {
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
	
	/**
	 * read weather data from UDP socket
	 */
	public void run() {
		while(actif) try {
			setCurrentCondition(MetarParser.parseReport(readUDPData()));
		} catch(IOException ioe) {
			System.err.println("Unable recevie UDP data" + ioe);
		} catch(MetarParseException e){
			System.err.println("METAR parsing error: " + e);
		}			
	}
	
	public void dispose() {
		actif = false;
		socket.close();
	}
	
	public static void main (String[] args) throws IOException{
		WeatherUDP w = new WeatherUDP(InetAddress.getLocalHost(),6666);
		
		w.addWeatherListener(new WeatherListener() {
			public void weatherChanged(Metar data) {
				System.out.println("Weather : " + data.getStationID());
			}

		});
		
		w.run();
	}

}


