package org.ncbo.stanford.exception;

public class InvalidDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5777897520002877516L;

	public static final String DEFAULT_MESSAGE = "Invalid data";

	/**
	 * 
	 */
	public InvalidDataException() {
		super(DEFAULT_MESSAGE);
	}

	public InvalidDataException(String msg) {
		super(msg);
	}

	public InvalidDataException(String msg, Exception e) {
		super(msg, e);
	}

	public InvalidDataException(Throwable t) {
		super(t);
	}

	public InvalidDataException(String msg, Throwable t) {
		super(msg, t);
	}
}
