package lucene.enumeration;

public enum LuceneRecordTypeEnum {

	RECORD_TYPE_SYNONYM("bSynonym"), RECORD_TYPE_PREFERRED_NAME("aPreferredName"), RECORD_TYPE_PROPERTY(
			"cProperty");

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
