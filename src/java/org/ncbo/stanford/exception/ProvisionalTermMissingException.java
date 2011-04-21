package org.ncbo.stanford.exception;

public class ProvisionalTermMissingException extends Exception {

	private static final long serialVersionUID = 7756482476278797760L;

	public static final String DEFAULT_MESSAGE = "Provisional term with given id doesn't exist";
	
	public ProvisionalTermMissingException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProvisionalTermMissingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ProvisionalTermMissingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ProvisionalTermMissingException(Throwable cause) {
		super(cause);
	}

}
