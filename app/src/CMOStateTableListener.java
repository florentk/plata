
public interface CMOStateTableListener {
	/**
	 * event when the table change
	 * @param cmoId cmo updated in the table
	 * @param table the complete table
	 */
	void tableChanged(String cmoId, CMOStateTable table);
}
