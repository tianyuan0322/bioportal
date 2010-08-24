package org.ncbo.stanford.exception;

public class NotificationNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8646279646126534355L;

	public static final String DEFAULT_MESSAGE = "Notification not found";

	/**
	 * 
	 */
	public NotificationNotFoundException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotificationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NotificationNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotificationNotFoundException(Throwable cause) {
		super(cause);
	}
}
