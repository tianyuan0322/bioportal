package org.ncbo.stanford.enumeration;

import org.ncbo.stanford.util.constants.ApplicationConstants;

public enum SearchRecordTypeEnum {

	RECORD_TYPE_PREFERRED_NAME(
			ApplicationConstants.SEARCH_RECORD_TYPE_PREFERRED_NAME), RECORD_TYPE_SYNONYM(
			ApplicationConstants.SEARCH_RECORD_TYPE_SYNONYM), RECORD_TYPE_PROPERTY(
			ApplicationConstants.SEARCH_RECORD_TYPE_PROPERTY);

	private final String label;

	private SearchRecordTypeEnum(String lbl) {
		label = lbl;
	}

	public static SearchRecordTypeEnum getFromLabel(String label) {
		SearchRecordTypeEnum recordType = null;

		if (label
				.equals(ApplicationConstants.SEARCH_RECORD_TYPE_PREFERRED_NAME)) {
			recordType = RECORD_TYPE_PREFERRED_NAME;
		} else if (label
				.equals(ApplicationConstants.SEARCH_RECORD_TYPE_SYNONYM)) {
			recordType = RECORD_TYPE_SYNONYM;
		} else if (label
				.equals(ApplicationConstants.SEARCH_RECORD_TYPE_PROPERTY)) {
			recordType = RECORD_TYPE_PROPERTY;
		}

		return recordType;
	}

	public String toString() {
		return label;
	}
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
}
