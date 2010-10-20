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

	public void run() {
		
	}

	public void dispose(){
		
	}
	
	
	//Unit testing
	public static void main (String[] args) throws Exception{
		Geolocation geo = new Fixe(new WGS84(1.0,2.0,3.0),4.0,5.0);
		
		geo.addPositionListener(new GeolocationListener() {

			public void positionChanged(WGS84 position, Double speed, Double track) {
				System.out.println(position + " Speed : " + speed + " Track : " + track);
			}

		});
		
		geo.start();
		
		geo.join();
	}



}
