package org.ncbo.stanford.exception;

public class InvalidMethodCallException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_MESSAGE = "This method should not be called from within this class";
	
	/**
	 * 
	 */
	public InvalidMethodCallException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidMethodCallException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidMethodCallException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidMethodCallException(Throwable cause) {
		super(cause);
	}
}
