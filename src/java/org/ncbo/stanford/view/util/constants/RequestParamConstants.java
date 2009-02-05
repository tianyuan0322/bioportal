package org.ncbo.stanford.view.util.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Request parameter constants
 * 
 * @author Michael Dorf
 */
public interface RequestParamConstants {

	/**
	 * ------------------------------------------------------------ Shared
	 * Constants across the application goes here
	 */
	public static final String PARAM_ERROR = "error";

	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_APPLICATIONID = "applicationid";
	public static final String PARAM_SESSIONID = "sessionid";

	public static final String PARAM_ROOT_CONCEPT = "root";

	/**
	 * A mandatory parameter required to be included in all encrypted URL query
	 * strings
	 */
	public static final String PARAM_ENCRYPTED_MANDATORY = "owmvurhsllhheeddtgkxx";

	/**
	 * The constant for values true/false appended to query string
	 */
	public static final Set<String> PARAM_VALUE_TRUE = new HashSet<String>(
			Arrays.asList("1", "true"));
	public static final Set<String> PARAM_VALUE_FALSE = new HashSet<String>(
			Arrays.asList("0", "false"));

	/**
	 * Concepts Restlet parameters (param for dealing with http://something.com Ids
	 */

	public static final String PARAM_CONCEPT_ID = "conceptid";
	
	
	/**
	 * Search query parameters
	 */
	public static final String PARAM_ONTOLOGY_IDS = "ontologyids";
	public static final String PARAM_ONTOLOGY_VERSION_IDS = "ontologyversionids";
	public static final String PARAM_INCLUDEPROPERTIES = "includeproperties";
	public static final String PARAM_ISEXACTMATCH = "isexactmatch";
	public static final String PARAM_PAGESIZE = "pagesize";
	public static final String PARAM_PAGENUM = "pagenum";

	/**
	 * Search index parameters
	 */
	public static final String PARAM_DOBACKUP = "dobackup";
	public static final String PARAM_DOOPTIMIZE = "dooptimize";
}
