package org.ncbo.stanford.exception;

/**
 * This exception represents an unexpected value or value type in a metadata 
 * store transaction.
 * 
 * @author Tony Loeser
 */
public class MetadataValueException extends MetadataException {

	private static final long serialVersionUID = -1313799517586387437L;

	public MetadataValueException(String message) {
		super(message);
	}
	
	public MetadataValueException(String message, Throwable cause) {
		super(message, cause);
	}
}
