package org.ncbo.stanford.exception;

public class InstanceNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8646279646126534355L;

	public static final String DEFAULT_MESSAGE = "Instance(s) not found";

	/**
	 * 
	 */
	public InstanceNotFoundException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InstanceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InstanceNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InstanceNotFoundException(Throwable cause) {
		super(cause);
	}
}
