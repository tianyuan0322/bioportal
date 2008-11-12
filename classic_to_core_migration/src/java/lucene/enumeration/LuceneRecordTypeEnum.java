package lucene.enumeration;

public enum LuceneRecordTypeEnum {

	RECORD_TYPE_PREFERRED_NAME("apreferredname"), RECORD_TYPE_SYNONYM(
			"bsynonym"), RECORD_TYPE_PROPERTY("cproperty");

	private final String label;

	private LuceneRecordTypeEnum(String lbl) {
		label = lbl;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
}
