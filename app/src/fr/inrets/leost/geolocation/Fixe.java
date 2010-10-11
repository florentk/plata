package fr.inrets.leost.geolocation;



import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;



/**
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 */
public class Fixe  extends Geolocation  {

	

	public Fixe(WGS84 pos, double speed, double track)  {

		
		//init the variable
		setUpdateInterval(DEFAULT_UPDATA_INTERVAL);
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

			public void positionChanged(Double time,WGS84 position, Double speed, Double track) {
				System.out.println(time + " : " + position + " Speed : " + speed + " Track : " + track);
			}

		});
		
		geo.start();
		
		geo.join();
	}



}
