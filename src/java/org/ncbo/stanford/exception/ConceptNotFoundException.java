package org.ncbo.stanford.exception;

public class ConceptNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8646279646126534355L;

	public static final String DEFAULT_MESSAGE = "Concept not found";

	/**
	 * 
	 */
	public ConceptNotFoundException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConceptNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ConceptNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConceptNotFoundException(Throwable cause) {
		super(cause);
	}
}
