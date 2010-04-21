package org.ncbo.stanford.exception;

public class NoteNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8646279646126534355L;

	public static final String DEFAULT_MESSAGE = "Note(s) not found";

	/**
	 * 
	 */
	public NoteNotFoundException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoteNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NoteNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NoteNotFoundException(Throwable cause) {
		super(cause);
	}
}
