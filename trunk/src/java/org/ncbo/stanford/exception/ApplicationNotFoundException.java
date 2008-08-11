package org.ncbo.stanford.exception;

import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.userdetails.UserDetailsService;


/**
 * Thrown if an {@link UserDetailsService} implementation cannot locate an application by its id.
 *
 * @author Michael Dorf
 */
public class ApplicationNotFoundException extends BadCredentialsException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    //~ Constructors ===================================================================================================

	/**
     * Constructs a <code>UsernameNotFoundException</code> with the specified
     * message.
     *
     * @param msg the detail message.
     */
    public ApplicationNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>UsernameNotFoundException</code>, making use of the <tt>extraInformation</tt>
     * property of the superclass.
     *
     * @param msg the detail message
     * @param extraInformation additional information such as the username.
     */
    public ApplicationNotFoundException(String msg, Object extraInformation) {
        super(msg, extraInformation);
    }

    /**
     * Constructs a <code>UsernameNotFoundException</code> with the specified
     * message and root cause.
     *
     * @param msg the detail message.
     * @param t root cause
     */
    public ApplicationNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
