package fr.inrets.leost.geolocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Trace extends Geolocation {

	
	private final List<GpsData> positions; 
	private final int waitTime;
	
	public Trace(String coordinates, int waitTime) {
		this.waitTime=waitTime;
		positions = parse(coordinates);
	}
	
	static public double computeTrack(double longi1, double lati1, double longi2, double lati2){
		double dx = longi2-longi1;
		double dy = lati2-lati1;
		double a = 0;
		
		if (dx >= 0 && dy < 0) {
			a = 180 - Math.toDegrees(Math.atan(dx/-dy)) ;
		} else if (dx < 0 && dy > 0) {
			a = 360 - Math.toDegrees(Math.atan(-dx/dy));
		} else if (dx <= 0 && dy < 0) {
			a = 180 + Math.toDegrees(Math.atan(dx/dy));
		} else if (dy != 0) {
			//dx >= 0 && dy > 0 
			a = Math.toDegrees(Math.atan(dx/dy));
		} else {
			//dy==0
			if (dx>0) a = 90; else a = 270;
		}
		
		return a;
	}
	
	private WGS84 parsePos(String pos){
		pos.trim();
		
		String[] tokens = pos.split(",");
		
		if (tokens.length == 3)
			return new WGS84(
					Double.parseDouble(tokens[0]),
					Double.parseDouble(tokens[1]),
					Double.parseDouble(tokens[2]));
		else
			return null;
	}
	
	private List<GpsData> parse(String coordinates) {
		ArrayList<GpsData> l = new ArrayList<GpsData>();
		double t=0;
		WGS84 oldPos = null;
		
		coordinates.trim();
		String[] tokens = coordinates.split(" ");
		
		for (String spos:tokens) {
			WGS84 pos = parsePos(spos);
			double track = 0;
			
			if(oldPos!=null)
				track = computeTrack(oldPos.longitude(), oldPos.latitude(), 
						pos.longitude(), pos.latitude());

			l.add(new GpsData(t,pos,0.0,track));
			t+=waitTime;
			oldPos = pos;
		}
		
		return l;
	}
	
	@Override
	public void run() {
		super.run();
		while(true){
			for(GpsData data:positions){
				try {
					
					setCurrentPos(data.getPosition());
					setCurrentTrack(data.getTrack());
							
					if(waitTime!=0) sleep(waitTime);
		
				} catch(InterruptedException ie) {
					System.err.println("Waiting time error : " + ie);
				}	
			}
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	
	
	static public Trace traceFromFile(String path, int waitTime) throws FileNotFoundException, IOException {
		return traceFromInput(new FileReader(new File(path)),waitTime);
	}
	
	static public Trace traceFromInput(InputStreamReader i, int waitTime) throws IOException{
		BufferedReader br = new BufferedReader(i);
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(' ');
	            line = br.readLine();
	        }
	        return new Trace(sb.toString(),waitTime);
	    
		} finally {
	        br.close();
	    }
	}

	public static void main (String[] args) throws Exception{
		if(args.length == 0)
			return;
		
		Trace t=null;
		
		if(args.length == 2)
			t = traceFromFile(args[0],Integer.parseInt(args[1]));
		if(args.length == 1)
			t = traceFromInput(new InputStreamReader(System.in),Integer.parseInt(args[0]));
			
		t.addPositionListener(new GeolocationListener() {

			public void positionChanged(WGS84 position, Double speed, Double track) {
				System.out.println(position + " Speed : " + speed + " Track : " + track);
			}

		});
		
		t.start();
		t.join();
	}
	
	
}
