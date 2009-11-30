package org.ncbo.stanford.exception;

/**
 * A non-specific exception that is used when some method signature
 * prevents us from throwing a checked exception.  
 * 
 * For example, use this exception if something goes wrong in an
 * implementation of {@link java.util.Iterator#next()}.  The point is
 * that it can still be caught without grabbing all RuntimeExceptions.
 * 
 * @author Tony Loeser
 */
public class BPRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// ==========================================================================
	// Constructors

	/**
	 * Constructs a <code>UsernameNotFoundException</code> with the specified
	 * message.
	 * 
	 * @param msg
	 *            the detail message.
	 */
	public BPRuntimeException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a <code>UsernameNotFoundException</code> with the specified
	 * message and root cause.
	 * 
	 * @param msg
	 *            the detail message.
	 * @param t
	 *            root cause
	 */
	public BPRuntimeException(String msg, Throwable t) {
		super(msg, t);
	}
}
