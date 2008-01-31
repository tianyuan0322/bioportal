package org.ncbo.stanford.enumeration;

import org.ncbo.stanford.util.constants.ApplicationConstants;

public enum OntologyFormatEnum {

	OWL_DL(ApplicationConstants.FORMAT_OWL_DL,
			ApplicationConstants.FORMAT_HANDLER_PROTEGE), OWL_FULL(
			ApplicationConstants.FORMAT_OWL_FULL,
			ApplicationConstants.FORMAT_HANDLER_PROTEGE), PROTEGE(
			ApplicationConstants.FORMAT_PROTEGE,
			ApplicationConstants.FORMAT_HANDLER_PROTEGE), OBO(
			ApplicationConstants.FORMAT_OBO,
			ApplicationConstants.FORMAT_HANDLER_LEXGRID);

	private final String format;
	private final String formatHandler;

	private OntologyFormatEnum(String fmt, String fmtHandler) {
		format = fmt;
		formatHandler = fmtHandler;
	}

	public static final OntologyFormatEnum getOntologyFormatEnum(String fmt) {
		return fmt.equalsIgnoreCase(ApplicationConstants.FORMAT_OWL_DL) ? OWL_DL : fmt
				.equalsIgnoreCase(ApplicationConstants.FORMAT_OWL_FULL) ? OWL_FULL : fmt
				.equalsIgnoreCase(ApplicationConstants.FORMAT_PROTEGE) ? PROTEGE : fmt
				.equalsIgnoreCase(ApplicationConstants.FORMAT_OBO) ? OBO : null;
	}

	public boolean equals(String fmt) {
		return format == fmt;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @return the formatHandler
	 */
	public String getFormatHandler() {
		return formatHandler;
	}
}
