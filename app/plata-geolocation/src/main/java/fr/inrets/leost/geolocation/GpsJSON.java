package fr.inrets.leost.geolocation;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public abstract class GpsJSON extends Geolocation {
	
	/** reader buffer for read the data from gpsd*/
	protected BufferedReader gpsBR;
	/** init the logger */
	protected static Logger logger = Logger.getLogger(Gps.class);	
	
	private int waitTime;
	
	public GpsJSON(int waitTime) {
		super();
		this.waitTime = waitTime;
	}

	/**
	 * read a line from gpsd and decode the data
	 */
	public void run() {
		if (gpsBR == null) return;

		
		while(true) {
			// for each line in the buffer
			try {
				//reads a line (passive wait) and decodes
				GpsData data = GpsData.decodeGPSDataJson(gpsBR.readLine());

				logger.info("receive_gps_data: " + data);
				
				//System.out.println("Read a line : " + data);
				
				//if needed, update the current position
				if(data!= null  && (   !getLastPos().equals(data.getPosition())
						|| !getCurrentSpeed().equals(data.getSpeed())
						|| !getCurrentTrack().equals(data.getTrack())))
				{
					//setCurrentTime(data.getTime());
					setCurrentPos(data.getPosition());
					setCurrentSpeed(data.getSpeed());	
					setCurrentTrack(data.getTrack());						
				}

				if(waitTime!=0) sleep(waitTime);

			} catch(IOException ioe) {
				System.err.println("Connection error with gps service");
			} catch(InterruptedException ie) {
				System.err.println("Waiting time error : " + ie);
			}			
		}
		
	}
}
