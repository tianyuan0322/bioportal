package org.ncbo.stanford.view.util.constants;

/**
 * Request parameter constants
 * 
 * @author Michael Dorf
 */
public interface RequestParamConstants {

	/**
	 * ------------------------------------------------------------
	 * Shared Constants across the application goes here
	 */	
	public static final String PARAM_ERROR = "error";

	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_APPLICATIONID = "applicationid";
	public static final String PARAM_SESSIONID = "sessionid";
	
	public static final String PARAM_ROOT_CONCEPT = "root";
	
	public static final String PARAM_ALL_ONTOLOGIES="0";

	/**
	 * A mandatory parameter required to be included in all encrypted
	 * URL query strings
	 */
	public static final String PARAM_ENCRYPTED_MANDATORY = "owmvurhsllhheeddtgkxx";
	
	/**
	 * The constant for value "true" appended to query string
	 */
	public static final String PARAM_VALUE_TRUE = "true";
	
	
	// THE FOLLOWING IS OBSOLETE -
	// MOVED TO message.properties.
	// THE ENTIRE FILE WILL BE MIGRATED TO THE SAME FILE.
	
	/*
	//The constants to identify HTTP call
	public static final String PARAM_METHOD = "method";	
	public static final String HTTP_PUT = "PUT";	
	public static final String HTTP_DELETE = "DELETE";
	
	
	public static final String ENTITY_USER = "user";
	public static final String ENTITY_ONTOLOGY = "ontology";
	
	
	// User Constants
	public static final String FORM_USERNAME = "username";
	public static final String FORM_PASSWORD = "password";
	public static final String FORM_FIRSTNAME = "firstname";
	public static final String FORM_LASTNAME = "lastname";
	public static final String FORM_EMAIL = "email";
	public static final String FORM_PHONE = "phone";
	public static final String FORM_DATECREATED = "dateCreated";
		
	// Ontology Constants

	*/
}
