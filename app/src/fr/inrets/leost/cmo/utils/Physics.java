package fr.inrets.leost.cmo.utils;

import fr.inrets.leost.geolocation.WGS84;

public class Physics {
	
	public static final double MAX_ANGLE_SAME_DIRECTION = 90; 	
	
	public static final double COEF_FRICTION_NORMAL = 0.8;
	public static final double COEF_FRICTION_AVG = 0.7;	
	public static final double COEF_FRICTION_PAVEMENT = 0.6;
	
	public static final double TEMPS_REACTION = 2.0;	
	
	public static final double G = 9.81;
	
	/**
	 * Compute the angle between two track in degree
	 * @param track1 first track in degree
	 * @param track2 second track in degree
	 * @return the angular difference between track1 et track2
	 */
	static public double differenceTrack(double track1, double track2){
		double diffAbs =  Math.abs(track1 - track2);
		
		return Math.min(360 - diffAbs, diffAbs);
	}	
	
	/**
	 * return true if the mobile object move in same direction
	 * @param track1 track of first mobile object 
	 * @param track2 track of second mobile object 
	 * @return true if the mobile object move in same direction
	 */
	static public boolean inSameDirection(double track1, double track2){
		return Double.compare( differenceTrack( track1 , track2 ),MAX_ANGLE_SAME_DIRECTION) <0;
	}

	/**
	 * convert a track in radian angular unit
	 * @param track the track in degree 
	 * @return the track in radian
	 */
	static public double trackToRadians(double track){
		//TODO 360 - track ???
		return Math.toRadians(360.0-track);
	}

	/**
	 * return true if the mobile object move to a point
	 * @param dx x delta between the mobile object and the point
	 * @param dy y delta between the mobile object and the point
	 * @param track track of mobile object
	 * @return true if the mobile object move to a point
	 */
	static public boolean inFront(double dx, double dy, double track){
		double a=  trackToRadians(track);
		return dx*Math.cos(a) + dy*Math.sin(a) > 0.0;
	}
	
	
	/**
	 * compute the Cartesian distance between p1 and p2
	 * @param lg1 longitude of p1
	 * @param lg1 latitude of p1
	 * @param lg2 longitude of p2
	 * @param lg2 latitude of p2
	 * @return the distance in meter
	 */
	static public double cartesianDistance(double lg1, double lt1, double lg2, double lt2){
		double dx = lg1 - lg2;
		double dy = lt1 - lt2;
		return cartesianDistance(dx,dy);	
	}		
	
	/**
	 * compute the Cartesian distance
	 * @param dx longitude difference
	 * @param dy latitude difference
	 * @return the distance in meter
	 */
	static public double cartesianDistance(double dx, double dy){
		return (double) Math.sqrt(  dx*dx + dy*dy  ) * ((Math.PI * WGS84.a / 180.0)) ;			
	}		

	/**
	 * extrapolate the longitude with the current speed
	 * @param time difference between actual time and t0
	 * @param lon longitude at t0
	 * @param speed current speed
	 * @param track current track
	 * @return the extrapolate current longitude
	 */
	/*static public double extrapolateLongitude(double dt, double lon, double speed, double track){
		return lon + speed * Math.sin(trackToRadians(track)) * dt ;
	}*/
	
	/**
	 * extrapolate the longitude with the current speed
	 * @param time difference between actual time and t0
	 * @param lat latitude at t0
	 * @param speed current speed
	 * @param track current track
	 * @return the extrapolate current longitude
	 */
	/*static public double extrapolateLatitude(double dt, double lat, double speed, double track){
		return lat + speed * Math.cos(trackToRadians(track)) * dt ;
	}*/
	
	
	
	/**
	 * Compute the Braking distance
	 * 
	 * Source : http://fr.wikipedia.org/wiki/Distance_d'arr%C3%AAt
	 * @param v speed (m/s)
	 * @param coef friction coef 
	 * @param dec declivity (m/m)
	 * @return distance braking (m)
	 */
	static public double BrakingDistance(double v, double coef, double dec){
		return (v*v) / (2.0*G*(coef + dec));
	}
	
	/**
	 * Compute the Braking distance
	 * 
	 * Source : http://fr.wikipedia.org/wiki/Distance_d'arr%C3%AAt
	 * @param v speed (m/s)
	 * @param coef friction coef 
	 * @return distance braking (m)
	 */
	static public double BrakingDistance(double v, double coef){
		return BrakingDistance(v, coef, 0.0);
	}	
	
	
	/**
	 * traveled distance while the reaction
	 * @param v speed
	 * @return
	 */
	static public double ReactionDistance(double v){
		return TEMPS_REACTION * v;
	}
	
	/**
	 * distance between the perception and the vehicle stopped
	 * @param v speed (m/s)
	 * @param coef friction coef 
	 */
	static public double StoppingDistance(double v, double coef){
		return BrakingDistance(v,coef) + ReactionDistance(v);
	}
	
}
