package org.ncbo.stanford.exception;

public class OntologyDeprecatedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8646279646126534355L;

	public static final String DEFAULT_MESSAGE = "Ontology has been deprecated";

	/**
	 * 
	 */
	public OntologyDeprecatedException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OntologyDeprecatedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public OntologyDeprecatedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OntologyDeprecatedException(Throwable cause) {
		super(cause);
	}
}
