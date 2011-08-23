package fr.inrets.leost.weather.dab;

import java.net.InetAddress;

import fr.inrets.leost.weather.*;

import DABInterface.*;

public class WeatherDAB implements DABControllerHelper.DABControllerClientFactory {
	public static final String METAR_SERVICE = "metar";
	public static final int WAIT = 4000;

	private Weather weather = new Fake("N/A");
	private RemoteDABControllerServer ctrl;

	private int byteToInt(byte[] b){
		return (int)b[0] * 256 * 256 * 256 + (int)b[1] * 256 * 256 + (int)b[2] * 256 + (int)b[3];
	}
	
	public WeatherDAB(String uri_ctrl, InetAddress udp_address, int udp_port) {
		try {
			ctrl = DABControllerHelper.newServer( uri_ctrl, null, this );
			ctrl._startAndWaitUp( WAIT );
			ctrl.DataServiceConfig( byteToInt(udp_address.getAddress()), udp_port );
			ctrl.DataServiceSet( METAR_SERVICE );
			weather = new WeatherUDP(udp_address, udp_port);
		}catch (Exception e){
			System.err.println("Unable connect to Etch controleur : " + e);
		}
	}
	
	public Weather getWeather(){
		return weather;
	}
	
	public void close(){
		weather.dispose();
		
		try {
			ctrl.DataServiceSet( "" );
			ctrl._stopAndWaitDown( WAIT );
		}catch (Exception e){
			System.err.println("Unable to stop Etch controleur : " + e);
		}

	}
	
	public DABControllerClient newDABControllerClient( RemoteDABControllerServer server )
		throws Exception
	{
		return new ImplDABControllerClient( server );
	}	
	
}


