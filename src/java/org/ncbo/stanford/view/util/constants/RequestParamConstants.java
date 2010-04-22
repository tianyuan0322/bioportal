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
	public static final String PARAM_OPENId = "openId";
	public static final String PARAM_ROOT_CONCEPT = "root";
	public static final String PARAM_ALL_CONCEPTS = "all";
	public static final String PARAM_PROPERTY_VALUE = "val";

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
	public static final String PARAM_INCLUDEINSTANCES = "includeinstances";

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
	public static final String PARAM_START_DATE_ACCESSED = "startdateaccessed";
	public static final String PARAM_END_DATE_ACCESSED = "enddateaccessed";
	public static final String PARAM_EVENT_TYPE = "eventtype";
	public static final String PARAM_METHOD = "method";
	public static final String PARAM_USER_ID = "userid";
	public static final String PARAM_ONTOLOGY_ID = "ontologyid";
	public static final String PARAM_ONTOLOGY_VERSION_ID = "ontologyversionid";
	public static final String PARAM_ONTOLOGY_NAME = "ontologyname";
	public static final String PARAM_CONCEPT_NAME = "conceptname";
	public static final String PARAM_SEARCH_PARAMETERS = "searchparameters";
	public static final String PARAM_NUM_SEARCH_RESULTS = "numsearchresults";
	public static final String PARAM_IP_ADDRESS = "ipaddress";

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
	
	/**
	 * Params for creating diffs
	 */
	public static final String PARAM_LATEST = "latest";
	public static final String PARAM_ALL = "all";
	public static final String PARAM_ONTOLOGY_VERSION_NEW = "ontologyversionnew";
	public static final String PARAM_ONTOLOGY_VERSION_OLD = "ontologyversionold";
	
	/*
	 * view extractor
	 */
	public static final String PARAM_DELAY = "delay";
	public static final String PARAM_FILTERRELATIONS = "filterrelations";
	public static final String PARAM_ONTOLOGYNAME = "ontologyname";
	public static final String PARAM_EXISTONTOLOGY = "existontology";
	public static final String PARAM_LOGCOUNT = "logcount";
	public static final String PARAM_SAVECOUNT = "savecount";

	/**
	 * Notes parameters
	 */
	public static final String PARAM_APPLIES_TO = "appliesto";
	public static final String PARAM_APPLIES_TO_TYPE = "appliestotype";
	public static final String PARAM_NOTE_ID = "noteid";
	public static final String PARAM_NOTE_TYPE = "type";
	public static final String PARAM_NOTE_SUBJECT = "subject";
	public static final String PARAM_NOTE_CONTENT = "content";
	public static final String PARAM_NOTE_AUTHOR = "author";
	public static final String PARAM_NOTE_THREADED = "threaded";
	public static final String PARAM_NOTE_ASSOCIATED = "associated";
	public static final String PARAM_NOTE_ARCHIVE = "archive";
	public static final String PARAM_NOTE_ARCHIVE_THREAD = "archivethread";
	public static final String PARAM_NOTE_UNARCHIVE = "unarchive";
	public static final String PARAM_NOTE_UNARCHIVE_THREAD = "unarchivethread";
	
	/**
	 * Notes: All proposals
	 */
	public static final String PARAM_REASON_FOR_CHANGE = "reasonforchange";
	public static final String PARAM_CONTACT_INFO = "contactinfo";
	
	/**
	 * Notes: New term proposal
	 */
	public static final String PARAM_NEW_TERM_DEFINITION = "newtermdefinition";
	public static final String PARAM_NEW_TERM_ID = "newtermid";
	public static final String PARAM_NEW_TERM_PARENT = "newtermparent";
	public static final String PARAM_NEW_TERM_PREFERRED_NAME = "newtermpreferredname";
	public static final String PARAM_NEW_TERM_SYNONYMS = "newtermsynonyms";
	
	/**
	 * Notes: New relationship proposal
	 */
	public static final String PARAM_NEW_REL_TYPE = "relationshiptype";
	public static final String PARAM_NEW_REL_TARGET = "relationshiptarget";
	public static final String PARAM_NEW_REL_OLD_TARGET = "oldrelationshiptarget";
	
	/**
	 * Notes: Property value change proposal
	 */
	public static final String PARAM_PROP_NEW_VALUE = "newpropertyvalue";
	public static final String PARAM_PROP_OLD_VALUE = "oldpropertyvalue";
	public static final String PARAM_PROP_ID = "propertyid";

}
