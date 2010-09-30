package fr.inrets.leost.cmo.utils;

public class Physics {
	
	public static final double MAX_ANGLE_SAME_DIRECTION = 90; 	
	
	public static final double COEF_FRICTION_NORMAL = 0.8;
	public static final double COEF_FRICTION_AVG = 0.7;	
	public static final double COEF_FRICTION_PAVEMENT = 0.6;
	
	public static final double TEMPS_REACTION = 2.0;	
	
	public static final double G = 9.81;
	
	static public double differenceTrack(double track1, double track2){
		double diffAbs =  Math.abs(track1 - track2);
		
		return Math.min(360 - diffAbs, diffAbs);
	}	
	
	static public boolean inSameDirection(double track1, double track2){
		return Double.compare( differenceTrack( track1 , track2 ),MAX_ANGLE_SAME_DIRECTION) <0;
	}


	
	static public boolean inFront(double dx, double dy, double track){
		double a=  Math.toRadians(track);
		return dx*Math.cos(a) + dy*Math.sin(a) > 0.0;
	}
	
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
	 * distance between the perception and the vehicule stoped
	 * @param v speed (m/s)
	 * @param coef friction coef 
	 */
	static public double StoppingDistance(double v, double coef){
		return BrakingDistance(v,coef) + ReactionDistance(v);
	}
	
}
