package org.ncbo.stanford.exception;

public class MappingMissingException extends Exception {

	private static final long serialVersionUID = 4906175603267658340L;

	public static final String DEFAULT_MESSAGE = "Mapping with given id doesn't exist";
	
	public MappingMissingException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MappingMissingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MappingMissingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MappingMissingException(Throwable cause) {
		super(cause);
	}

}
