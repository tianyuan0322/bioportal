package org.ncbo.stanford.enumeration;

import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * An Enum to store user roles (corresponds to ncbo_l_role table values)
 * 
 * @author Michael Dorf
 * 
 */
public enum RoleEnum {

	ROLE_DEVELOPER(ApplicationConstants.USER_ROLE_DEVELOPER),
	ROLE_LIBRARIAN(ApplicationConstants.USER_ROLE_LIBRARIAN),
	ROLE_ADMINISTRATOR(ApplicationConstants.USER_ROLE_ADMINISTRATOR);

	private final String label;

	private RoleEnum(String lbl) {
		label = lbl;
	}
	
	public static RoleEnum getFromLabel(String label) {
		RoleEnum role = null;

		if (label != null) {
			if (label.equals(ApplicationConstants.USER_ROLE_DEVELOPER)) {
				role = ROLE_DEVELOPER;
			} else if (label.equals(ApplicationConstants.USER_ROLE_LIBRARIAN)) {
				role = ROLE_LIBRARIAN;
			} else if (label
					.equals(ApplicationConstants.USER_ROLE_ADMINISTRATOR)) {
				role = ROLE_ADMINISTRATOR;
			}
		}

		return role;
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