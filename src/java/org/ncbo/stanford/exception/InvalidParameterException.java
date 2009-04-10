package org.ncbo.stanford.exception;

public class InvalidParameterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8646279646126534355L;

	public static final String DEFAULT_MESSAGE = "Invalid parameter";

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidParameterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidParameterException(Throwable cause) {
		super(cause);
	}
}
