package org.ncbo.stanford.exception;

public class MappingExistsException extends Exception {

	private static final long serialVersionUID = -6540841714824269141L;
	
	public static final String DEFAULT_MESSAGE = "Mapping with given id already exists";
	
	public MappingExistsException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MappingExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MappingExistsException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MappingExistsException(Throwable cause) {
		super(cause);
	}

}
