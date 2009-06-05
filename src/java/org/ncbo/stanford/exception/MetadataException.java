package org.ncbo.stanford.exception;

/**
 * This Exception should be thrown whenever an operation involving 
 * metadata fails. (For example save ontology metadata, updata view metadata, etc.)
 *  
 * @author csnyulas
 */
public class MetadataException extends Exception {

	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_MESSAGE = "Metadata operation failed";

	/**
	 * 
	 */
	public MetadataException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MetadataException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MetadataException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MetadataException(Throwable cause) {
		super(cause);
	}
}
