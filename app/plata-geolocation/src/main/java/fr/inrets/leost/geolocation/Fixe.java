package fr.inrets.leost.geolocation;



import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;


/**
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 */
public class Fixe  extends Geolocation  {

	

	public Fixe(WGS84 pos, double speed, double track)  {

		
		//init the variable
		setCurrentPos(pos);
		setCurrentSpeed(speed);
		setCurrentTrack(track);		

	}
	
	public Fixe(String arg) {
		double latitude=0.0, longitude=0.0, speed=0.0, track=0.0;
	     
		arg.trim();
		
		String delims = " ";
		String[] tokens = arg.split(delims);
		
		String[] pos = new String[2];
		System.arraycopy(tokens, 0, pos, 0, 2);
		
		StringParser p = new StringParser();
		//for each coordinate
		for ( String coord:pos ){


			
			p.parseWGS84(coord);
			
			//init corresponding value
			if(p.getType() == StringParser.LATITUDE)
				latitude = p.getValue();
			
			if(p.getType() == StringParser.LONGITUDE)
				longitude = p.getValue();			
		}
		
		if (tokens.length > 2) {
			p.parseSpeed(tokens[2]);
			speed = p.getValue();
		}
		
		if (tokens.length > 3) {
			p.parseTrack(tokens[3]);
			track = p.getValue();		
		}	
		
		setCurrentPos(new WGS84(longitude,latitude,0.0));
		setCurrentSpeed(speed);
		setCurrentTrack(track);	
	}

	public void run() {
		
	}

	public void dispose(){
		
	}
	
	
	//Unit testing
	public static void main (String[] args) throws Exception{
		//Geolocation geo = new Fixe(new WGS84(1.0,2.0,3.0),4.0,5.0);
		Geolocation geo = new Fixe("50°37'02.44\"N 3°07'31.10\"E 16km/h 18°");
		
		System.out.println(geo.getLastPos() + " " + geo.getCurrentSpeed() + " " + geo.getCurrentTrack());
		
		/*geo.addPositionListener(new GeolocationListener() {

			public void positionChanged(WGS84 position, Double speed, Double track) {
				System.out.println(position + " Speed : " + speed + " Track : " + track);
			}

		});
		
		geo.start();
		
		geo.join();*/
	}



}
