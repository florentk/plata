package fr.inrets.leost.cmo.dashboard;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import fr.inrets.leost.cmo.management.CMOManagement;
import fr.inrets.leost.cmo.management.CMOTableEntry;
import fr.inrets.leost.cmo.utils.Physics;
import fr.inrets.leost.geolocation.Geolocation;
import fr.inrets.leost.geolocation.WGS84;

public class CrossingCMO implements Indicator {

	public class CrossingCMOEntry {
		final Double t; //predicted time before reach the cross point
		final Double t_cmo; //predicted time before the cmo reach the cross point
		final Double d; // distance to the CMO
		final Double azimuth; //track for reach CMO
		final DecimalFormat format = new DecimalFormat("#.##");
		public CrossingCMOEntry(Double t, Double t_cmo, Double d, Double azimuth) {
			this.t = t;
			this.t_cmo = t_cmo;
			this.d = d;
			this.azimuth = azimuth;
		}
		@Override
		public String toString() {
			return  "t=" + format.format(t) + 
				" s, t_cmo=" + format.format(t_cmo) + 
				" s, d=" + format.format(d) + " m" +
				" m, a=" + format.format(azimuth) + " °";			
		}
	}
	
	/**none hazard*/
	public static final int DECISION_NONE = 0;
	/**warning*/
	public static final int DECISION_WARNING = 1;	
	/**hazard !*/
	public static final int DECISION_HAZARD = 2;	
	
	private static final double DECISION_PERSIST = 4.0;
	
	private int decision=DECISION_NONE;	
	private Date lastDecissionTime = null;
	
	
	
	/**  time before predicted collision for a warning  */
	private static final double LIMIT_WARNING = 10;
	
	/**  time before predicted collision for a hazard  */
	private static final double LIMIT_HAZARD = 2;	
	
	final private CMOManagement cmo;
	final private Geolocation geo;
	
	final private Hashtable<CMOTableEntry,CrossingCMOEntry> cmoTable = new Hashtable<CMOTableEntry,CrossingCMOEntry>();
	
	public void updateCrossingCMO(){		
		final WGS84 pos = geo.getLastPos();
		final WGS84 prevPos = geo.getPrevPos();		
		
		if (prevPos!=null) {
			short newDecision=DECISION_NONE;
			
			//for each value in CMO table
			for ( CMOTableEntry e : cmo.getTable() ){
				final double  x = e.crossPosX(
						pos.longitude(), pos.latitude(), 
						prevPos.longitude(), prevPos.latitude());
				
				if (x != 0.0) {
					final WGS84 currentPos = geo.getLastPos();
					final double t = geo.getPreditedTimeFromLongitude(x);
					final double t_cmo = e.getPreditedTimeFromLongitude(x);
					final double d = e.distance(currentPos.longitude(),currentPos.latitude());
					final double a = e.azimuth(currentPos.longitude(),currentPos.latitude());
					cmoTable.put(e,new CrossingCMOEntry(t, t_cmo, d, geo.getCurrentTrack() - a));
					
					if(t >= 0.0 && t_cmo >= 0.0 && Math.abs(t-t_cmo) < LIMIT_HAZARD) {
						if (t < LIMIT_WARNING && newDecision < DECISION_WARNING) 
							newDecision = DECISION_WARNING;
						
						if (t < LIMIT_HAZARD && newDecision < DECISION_HAZARD) 
							newDecision = DECISION_HAZARD;
					}
				}
			}
			
			if(lastDecissionTime==null){
				lastDecissionTime= new Date();
				decision = newDecision;
			}else{
				final double elapsedTime = ((double)((new Date()).getTime() - lastDecissionTime.getTime()))/1000.0;
				
				if(newDecision > decision || elapsedTime >= DECISION_PERSIST){
					if(newDecision > decision) 
						lastDecissionTime= new Date();
					decision = newDecision;
				}
			}
		}
		
		//System.out.println(toString());
	}
	
	public CrossingCMO(Geolocation geo, CMOManagement cmo) {
		super();
		this.cmo = cmo;
		this.geo = geo;
	}
	
	@Override
	public void update() {
		updateCrossingCMO();
	}
	
	

	public int getDecision() {
		return decision;
	}

	public Map<CMOTableEntry, CrossingCMOEntry> getCrossingTimeTable() {
		return cmoTable;
	}

	@Override
	public String name() {
		return "CrossingCMO";
	}

	@Override
	public String toString() {
		StringBuffer s=new StringBuffer();
		
		for(Entry<CMOTableEntry,CrossingCMOEntry> e:cmoTable.entrySet())
			s.append(e.getKey().getCmoID() + " " + e.getValue());
		
		return s.toString();
	}
	
	

}
