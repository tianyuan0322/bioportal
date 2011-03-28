package org.ncbo.stanford.enumeration;

import org.ncbo.stanford.util.constants.ApplicationConstants;

public enum ConceptTypeEnum {

	CONCEPT_TYPE_CLASS(ApplicationConstants.CONCEPT_TYPE_CLASS), 
	CONCEPT_TYPE_INDIVIDUAL(ApplicationConstants.CONCEPT_TYPE_INDIVIDUAL), 
	CONCEPT_TYPE_PROPERTY(ApplicationConstants.CONCEPT_TYPE_PROPERTY);

	private final String label;

	private ConceptTypeEnum(String lbl) {
		label = lbl;
	}

	public static ConceptTypeEnum getFromLabel(String label) {
		ConceptTypeEnum conceptType = null;

		if (label != null) {
			if (label.equals(ApplicationConstants.CONCEPT_TYPE_CLASS)) {
				conceptType = CONCEPT_TYPE_CLASS;
			} else if (label
					.equals(ApplicationConstants.CONCEPT_TYPE_INDIVIDUAL)) {
				conceptType = CONCEPT_TYPE_INDIVIDUAL;
			} else if (label.equals(ApplicationConstants.CONCEPT_TYPE_PROPERTY)) {
				conceptType = CONCEPT_TYPE_PROPERTY;
			}
		}

		return conceptType;
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
