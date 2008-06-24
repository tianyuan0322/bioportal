package org.ncbo.stanford.exception;

public class InvalidOntologyFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8646279646126534355L;
	
	public static final String DEFAULT_MESSAGE = "Invalid ontology format";

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidOntologyFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidOntologyFormatException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidOntologyFormatException(Throwable cause) {
		super(cause);
	}
}
