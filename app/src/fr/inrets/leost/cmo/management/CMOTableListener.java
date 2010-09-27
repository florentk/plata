package fr.inrets.leost.cmo.management;

public interface CMOTableListener {
	/**
	 * event when the table change
	 * @param cmoId cmo updated in the table
	 * @param table the complete table
	 */
	void tableChanged(String cmoId, CMOTable table);
}
