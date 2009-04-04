package org.ncbo.stanford.exception;

public class OntologyVersionNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8646279646126534355L;

	public static final String DEFAULT_MESSAGE = "Ontology version not found";

	/**
	 * @param message
	 * @param cause
	 */
	public OntologyVersionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public OntologyVersionNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OntologyVersionNotFoundException(Throwable cause) {
		super(cause);
	}
}
