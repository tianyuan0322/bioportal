package org.ncbo.stanford.enumeration;

import org.ncbo.stanford.util.constants.ApplicationConstants;

public enum ViewingRestrictionEnum {
	VIEWING_RESTRICTION_PRIVATE(
			ApplicationConstants.VIEWING_RESTRICTION_PRIVATE), VIEWING_RESTRICTION_LICENSED(
			ApplicationConstants.VIEWING_RESTRICTION_LICENSED), VIEWING_RESTRICTION_PUBLIC(
			ApplicationConstants.VIEWING_RESTRICTION_PUBLIC);

	private final String label;

	private ViewingRestrictionEnum(String lbl) {
		label = lbl;
	}

	public static ViewingRestrictionEnum getFromLabel(String label) {
		ViewingRestrictionEnum restriction = null;

		if (label != null) {
			if (label.equals(ApplicationConstants.VIEWING_RESTRICTION_PRIVATE)) {
				restriction = VIEWING_RESTRICTION_PRIVATE;
			} else if (label
					.equals(ApplicationConstants.VIEWING_RESTRICTION_LICENSED)) {
				restriction = VIEWING_RESTRICTION_LICENSED;
			}
		}

		return restriction;
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
