package org.ncbo.stanford.exception;

public class InvalidCompressedFileHandlerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5942895827870145299L;
	public static final String DEFAULT_MESSAGE = "Invalid ontology compressed file handler";

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidCompressedFileHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidCompressedFileHandlerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidCompressedFileHandlerException(Throwable cause) {
		super(cause);
	}
}
