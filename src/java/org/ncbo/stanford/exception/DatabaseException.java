package org.ncbo.stanford.exception;

/**
 * Thrown in case of database error.
 * 
 * @author cyoun
 */
public class DatabaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4976135600117962702L;
	public static final String DEFAULT_MESSAGE = "Database exception";

	/**
	 * 
	 */
	public DatabaseException() {
		super(DEFAULT_MESSAGE);
	}

	public DatabaseException(String msg) {
		super(msg);
	}

	public DatabaseException(String msg, Exception e) {
		super(msg, e);
	}

	public DatabaseException(Throwable t) {
		super(t);
	}

	public DatabaseException(String msg, Throwable t) {
		super(msg, t);
	}

}