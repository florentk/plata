package fr.inrets.leost.cmo.dashboard;

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
	
	final private Hashtable<CMOTableEntry,Double> cmoTable = new Hashtable<CMOTableEntry,Double>();
	
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
					final double t = geo.getPreditedTimeFromLongitude(x);
					cmoTable.put(e,t);
					
					if(t >= 0.0) {
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
		
		System.out.println(toString());
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

	public Map<CMOTableEntry, Double> getCrossingTimeTable() {
		return cmoTable;
	}

	@Override
	public String name() {
		return "CrossingCMO";
	}

	@Override
	public String toString() {
		StringBuffer s=new StringBuffer();
		
		for(Entry<CMOTableEntry,Double> e:cmoTable.entrySet())
			s.append(e.getKey().getCmoID() + " " + e.getValue() + " s;");
		
		return s.toString();
	}
	
	

}
