package org.ncbo.stanford.exception;

public class MetadataObjectNotFoundException extends MetadataException {

	private static final long serialVersionUID = -5324701145194113115L;
	public static final String DEFAULT_MESSAGE = "Metadata object not found";

	/**
	 * 
	 */
	public MetadataObjectNotFoundException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MetadataObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MetadataObjectNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MetadataObjectNotFoundException(Throwable cause) {
		super(cause);
	}
}
