package org.ncbo.stanford.exception;

public class OntologyNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8646279646126534355L;

	public static final String DEFAULT_MESSAGE = "Ontology not found";

	/**
	 * @param message
	 * @param cause
	 */
	public OntologyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public OntologyNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OntologyNotFoundException(Throwable cause) {
		super(cause);
	}
}
