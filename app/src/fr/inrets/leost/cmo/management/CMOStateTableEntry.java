package fr.inrets.leost.cmo.management;

import java.util.Date;

import fr.inrets.leost.cmo.packet.CMOState;

public class CMOStateTableEntry {
	private CMOState state;
	private Date dateEntry;
	
	
	
	public CMOStateTableEntry(CMOState state) {
		super();
		this.state = state;
		this.dateEntry = new Date();
	}



	boolean isExpired(){
		Date now = new Date();
		return ( now.getTime() > (getDateEntry().getTime() + state.getLifetime()) );
	}



	/**
	 * @return the state
	 */
	public CMOState getState() {
		return state;
	}



	/**
	 * @return date which entry is added
	 */
	public Date getDateEntry() {
		return dateEntry;
	}



	/**
	 * @param state the state to set
	 */
	public void setState(CMOState state) {
		this.state = state;
	}
	
	
	public String toString(){
		return state.toString() + "Entry date : " + dateEntry;
	}
}
