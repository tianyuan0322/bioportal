package lucene.enumeration;

public enum LuceneRecordTypeEnum {

	RECORD_TYPE_SYNONYM("synonym"), RECORD_TYPE_PREFERRED_NAME("preferred_name");

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
