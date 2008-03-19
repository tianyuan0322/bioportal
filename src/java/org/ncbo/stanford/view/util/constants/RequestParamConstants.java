package org.ncbo.stanford.view.util.constants;

/**
 * Request parameter constants
 * 
 * @author Michael Dorf
 */
public interface RequestParamConstants {

	public static final String PARAM_ERROR = "error";

	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_APPLICATIONID = "applicationid";
	public static final String PARAM_SESSIONID = "sessionid";
	
	public static final String PARAM_ROOT_CONCEPT = "root";

	/**
	 * A mandatory parmeter required to be included in all encrypted
	 * URL query strings
	 */
	public static final String PARAM_ENCRYPTED_MANDATORY = "owmvurhsllhheeddtgkxx";
	
	/**
	 * The constant for value "true" appended to query string
	 */
	public static final String PARAM_VALUE_TRUE = "true";
}
