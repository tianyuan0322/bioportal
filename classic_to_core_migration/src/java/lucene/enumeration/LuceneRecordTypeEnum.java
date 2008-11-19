package lucene.enumeration;

import org.ncbo.stanford.util.constants.ApplicationConstants;

public enum LuceneRecordTypeEnum {

	RECORD_TYPE_PREFERRED_NAME(
			ApplicationConstants.LUCENE_RECORD_TYPE_PREFERRED_NAME), RECORD_TYPE_SYNONYM(
			ApplicationConstants.LUCENE_RECORD_TYPE_SYNONYM), RECORD_TYPE_PROPERTY(
			ApplicationConstants.LUCENE_RECORD_TYPE_PROPERTY);

	private final String label;

	private LuceneRecordTypeEnum(String lbl) {
		label = lbl;
	}

	public static LuceneRecordTypeEnum getFromLabel(String label) {
		LuceneRecordTypeEnum recordType = null;

		if (label
				.equals(ApplicationConstants.LUCENE_RECORD_TYPE_PREFERRED_NAME)) {
			recordType = RECORD_TYPE_PREFERRED_NAME;
		} else if (label
				.equals(ApplicationConstants.LUCENE_RECORD_TYPE_SYNONYM)) {
			recordType = RECORD_TYPE_SYNONYM;
		} else if (label
				.equals(ApplicationConstants.LUCENE_RECORD_TYPE_PROPERTY)) {
			recordType = RECORD_TYPE_PROPERTY;
		}

		return recordType;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
}
