package org.ncbo.stanford.exception;

public class ProvisionalTermExistsException extends Exception {

	private static final long serialVersionUID = 2343355034054432402L;

	public static final String DEFAULT_MESSAGE = "Provisional term with given id already exists";
	
	public ProvisionalTermExistsException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProvisionalTermExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ProvisionalTermExistsException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ProvisionalTermExistsException(Throwable cause) {
		super(cause);
	}

}
