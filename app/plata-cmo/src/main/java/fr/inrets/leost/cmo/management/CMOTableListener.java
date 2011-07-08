package fr.inrets.leost.cmo.management;

/**
 * for notification when the table is modified
 * @author florent kaisser
 * @dep CMOTableEntry
 */
public interface CMOTableListener {
	/**
	 * event when the table change
	 * @param cmoId cmo updated in the table
	 * @param table the complete table
	 */
	void tableChanged(CMOTableEntry table);
	
	/**
	 * event when a entry is remove
	 * @param cmoId cmo updated in the table
	 * @param table the complete table
	 */	
	void tableCMORemoved(CMOTableEntry table);

	/**
	 * event when a entry is added
	 * @param cmoId cmo updated in the table
	 * @param table the complete table
	 */	
	void tableCMOAdded(CMOTableEntry table);
}
