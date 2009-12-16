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
	public static final String PARAM_OPENId="openId";
	public static final String PARAM_ROOT_CONCEPT = "root";
	public static final String PARAM_ALL_CONCEPTS = "all";

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
	 * Concepts Restlet parameters (param for dealing with http://something.com
	 * Ids
	 */
	public static final String PARAM_CONCEPT_ID = "conceptid";
	public static final String PARAM_MAXNUMCHILDREN = "maxnumchildren";
	public static final String PARAM_NORELATIONS = "norelations";

	/**
	 * Path Restlet parameters
	 */
	public static final String PARAM_SOURCE = "source";
	public static final String PARAM_TARGET = "target";
	public static final String PARAM_LIGHT = "light";

	/**
	 * Search query parameters
	 */
	public static final String PARAM_ONTOLOGY_IDS = "ontologyids";
	public static final String PARAM_ONTOLOGY_VERSION_IDS = "ontologyversionids";
	public static final String PARAM_INCLUDEPROPERTIES = "includeproperties";
	public static final String PARAM_ISEXACTMATCH = "isexactmatch";
	public static final String PARAM_PAGESIZE = "pagesize";
	public static final String PARAM_PAGENUM = "pagenum";
	public static final String PARAM_MAXNUMHITS = "maxnumhits";
	public static final String PARAM_QUERY = "query";
	public static final String PARAM_INCLUDEVIEWS = "includeviews";
	public static final String PARAM_SUBTREEROOTCONCEPTID = "subtreerootconceptid";

	/**
	 * Search index parameters
	 */
	public static final String PARAM_DOBACKUP = "dobackup";
	public static final String PARAM_DOOPTIMIZE = "dooptimize";
	public static final String PARAM_RELOADCACHE = "reloadcache";

	/**
	 * Diff download parameters
	 */
	public static final String PARAM_FORMAT = "format";

	/**
	 * Hierarchy parameters
	 */
	public static final String PARAM_LEVEL = "level";
	public static final String PARAM_OFFSET = "offset";
	public static final String PARAM_LIMIT = "limit";

	/**
	 * Parameter for overriding default ontology load handler (parser)
	 */
	public static final String PARAM_ONTOLOGYPARSER = "ontologyparser";

	/**
	 * Logging parameters
	 */
	public static final String PARAM_LOGONLY = "logonly";
	public static final String PARAM_REQUEST_URL = "requesturl";
	public static final String PARAM_RESOURCE_PARAMETERS = "resourceparameters";
	public static final String PARAM_START_DATE_ACCESSED = "startdateaccessed";
	public static final String PARAM_END_DATE_ACCESSED = "enddateaccessed";

	/**
	 * Ontology deletion/deprecation parameters
	 */
	public static final String PARAM_REMOVE_METADATA = "removemetadata";
	public static final String PARAM_REMOVE_ONTOLOGY_FILES = "removeontologyfiles";
	public static final String PARAM_EXCLUDE_DEPRECATED = "excludedeprecated";

	/**
	 * Params for different types of ontology lists
	 */
	public static final String PARAM_ONTOLOGIES = "ontologies";
	public static final String PARAM_VIEWS = "views";
	public static final String PARAM_ACTIVE = "active";
	public static final String PARAM_PULLED = "pulled";
}
