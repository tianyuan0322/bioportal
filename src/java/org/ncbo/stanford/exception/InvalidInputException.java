package org.ncbo.stanford.exception;

/**
 * Input into an API is invalid, for example a URL parameter does not parse.
 */
public class InvalidInputException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5777897520002877516L;

	public static final String DEFAULT_MESSAGE = "Invalid input";

	/**
	 * 
	 */
	public InvalidInputException() {
		super(DEFAULT_MESSAGE);
	}

	public InvalidInputException(String msg) {
		super(msg);
	}

	public InvalidInputException(String msg, Exception e) {
		super(msg, e);
	}

	public InvalidInputException(Throwable t) {
		super(t);
	}

	public InvalidInputException(String msg, Throwable t) {
		super(msg, t);
	}
}
