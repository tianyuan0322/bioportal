package org.ncbo.stanford.exceptions;

public class MappingMissingException extends Exception {
	
	private static final long serialVersionUID = -4053680637021403004L;

	public static final String DEFAULT_MESSAGE = "Mapping with given id does not exist";

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
