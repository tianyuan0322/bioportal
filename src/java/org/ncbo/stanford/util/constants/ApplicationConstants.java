package org.ncbo.stanford.util.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public interface ApplicationConstants {

	public static final String USER_SESSION_NAME = "UserSession";
	public static final String SECURITY_CONTEXT_KEY = "SECURITY_CONTEXT";
	
	/**
	 * User Roles
	 */
	public static final String USER_ROLE_DEVELOPER = "ROLE_DEVELOPER";
	public static final String USER_ROLE_LIBRARIAN = "ROLE_LIBRARIAN";
	public static final String USER_ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
	
	public static final String DEFAULT_USER_ROLE = USER_ROLE_LIBRARIAN;
	
	/**
	 * Standard "NO VALUE" constant
	 */
	public static final int NO_VAL = -1;

	/**
	 * Boolean-to-bit conversions
	 */
	public static final Byte TRUE = 1;
	public static final Byte FALSE = 0;

	/**
	 * UTF-8 Encoding
	 */
	public static final String UTF_8 = "UTF-8";

	public static final Integer TIMETOLIVE = 60;
	/**
	 * File extensions
	 */
	public static final String OBO_EXTENSION = "obo";
	public static final String PROTEGE_EXTENSION = "pprj";
	public static final String OWL_EXTENSION = "owl";
	public static final String XML_EXTENSION = "xml";
	public static final String ZIP_EXTENSION = "zip";
	public static final String GZIP_EXTENSION = "gz";
	public static final String JAR_EXTENSION = "jar";
	public static final String TAR_EXTENSION = "tar";

	/**
	 * Search Record Types
	 */
	public static final String SEARCH_RECORD_TYPE_PREFERRED_NAME = "apreferredname";
	public static final String SEARCH_RECORD_TYPE_CONCEPT_ID = "bconceptid";
	public static final String SEARCH_RECORD_TYPE_SYNONYM = "csynonym";
	public static final String SEARCH_RECORD_TYPE_PROPERTY = "dproperty";

	/**
	 * Concept Types
	 */
	public static final String CONCEPT_TYPE_CLASS = "class";
	public static final String CONCEPT_TYPE_INDIVIDUAL = "individual";
	public static final String CONCEPT_TYPE_PROPERTY = "property";

	/**
	 * Viewing Restrictions
	 */
	public static final String VIEWING_RESTRICTION_PRIVATE = "private";
	public static final String VIEWING_RESTRICTION_LICENSED = "licensed";

	/**
	 * Ontology version status
	 */
	public static final String VERSION_STATUS_PREPRODUCTION = "beta";
	public static final String VERSION_STATUS_ALPHA = "alpha";
	public static final String VERSION_STATUS_BETA = "beta";
	public static final String VERSION_STATUS_PRODUCTION = "production";
	public static final String VERSION_STATUS_RETIRED = "retired";

	/**
	 * Composite filename used for joining multiple ontologies into a single
	 * file
	 */
	public static final String COMPOSITE_FILENAME = "joined";

	/**
	 * Value used when the ontology version is not known
	 */
	public static final String UNKNOWN = "unknown";	
	/**
	 * Buffer size for compressed files
	 */
	public static final Integer BUFFER_SIZE = 2048;

	/**
	 * Ontology formats
	 */
	public static final String FORMAT_OWL = "OWL";
	public static final String FORMAT_OWL_DL = "OWL-DL";
	public static final String FORMAT_OWL_FULL = "OWL-FULL";
	public static final String FORMAT_OWL_LITE = "OWL-LITE";
	public static final String FORMAT_PROTEGE = "PROTEGE";
	public static final String FORMAT_OBO = "OBO";
	public static final String FORMAT_UMLS_RRF = "RRF";
	public static final String FORMAT_UMLS_RELA = "UMLS-RELA";
	public static final String FORMAT_META = "META";
	public static final String FORMAT_LEXGRID_XML = "LEXGRID-XML";
	public static final String FORMAT_INVALID = "Invalid";

	/**
	 * Hierarchy types
	 */
	public static final String SUB_CLASS = "SubClass";
	public static final String SUPER_CLASS = "SuperClass";
	public static final String SUB_PROPERTY = "SubProperty";
	public static final String SUPER_PROPERTY = "SuperProperty";
	public static final String RDF_TYPE = "RdfType";
	public static final String CHILD_COUNT = "ChildCount";
	public static final String INSTANCE_COUNT = "InstanceCount";
	public static final String SEMANTIC_TYPES = "SemanticTypes";
	public static final String LEVEL = "Level";
	public static final String PATH = "Path";
	public static final String EMAIL = "Email";
	public static final String CLASS_PROPERTIES = "ClassProperties";
	public static final String RANGE = "Range";
	public static final String DOMAIN = "Domain";

	/**
	 * Ontology format handlers
	 */
	public static final String FORMAT_HANDLER_PROTEGE = "protege";
	public static final String FORMAT_HANDLER_LEXGRID = "lexgrid";

	/**
	 * TextManager constants
	 */
	// TAG Constants
	public static final String KEYWORD = "KEYWORD";
	public static final String IMPORT = "IMPORT";
	public static final String SETKEYWORD = "SETKEYWORD";

	public static final String TEXT_TAG_SUFFIX = "Tag";
	public static final String TEXT_TAG_PREFIX = "Text";
	public static final String TM_TAG_JAVA_PACKAGE = "org.ncbo.stanford.util.textmanager.tagprocessor.tag";
	public static final String TEXT_NOT_FOUND = "###_TEXT_NOT_FOUND_###";
	public static final String[] ALL_TAGS = { KEYWORD, IMPORT, SETKEYWORD };

	// Content retrieval constants
	public static final int FROM_DB = 1200;
	public static final int FROM_CACHE = 1201;

	/**
	 * Notification Service constants
	 */
	// TAG Constants
	public static final String ONTOLOGY_VERSION_ID = "ONTOLOGY_VERSION_ID";
	public static final String ONTOLOGY_DISPLAY_LABEL = "ONTOLOGY_DISPLAY_LABEL";
	public static final String CONCEPT_ID = "CONCEPT_ID";
	public static final String USERNAME = "USERNAME";
	public static final String SUBJECT_SUFFIX = "_SUB";
	public static final String NOTE_SUBJECT = "NOTE_SUBJECT";
	public static final String NOTE_BODY = "NOTE_BODY";
	public static final String NOTE_PROPOSAL_INFO = "NOTE_PROPOSAL_INFO";
	public static final String NOTE_USERNAME = "NOTE_USERNAME";
	public static final String NOTE_URL = "NOTE_URL";
	public static final String NCBO_USER_SUBSCRIPTIONS = "99 is a good candidate";
	public static final String ONTOLOGY_ID = "99";

	/**
	 * XML declaration
	 */
	public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	/**
	 * The name of the root tag of all xml error response messages
	 */
	public static final String ERROR_XML_TAG_NAME = "error";

	/**
	 * The name of the data element in response xml
	 */
	public static final String DATA_XML_TAG_NAME = "data";

	/**
	 * The name of the root tag of all xml error status response messages
	 */
	public static final String ERROR_STATUS_XML_TAG_NAME = "errorStatus";

	/**
	 * The name of the root tag of success xml response message
	 */
	public static final String SUCCESS_XML_TAG_NAME = "success";

	/**
	 * The name of the root tag of response xml response message
	 */
	public static final String RESPONSE_XML_TAG_NAME = "response";

	public static final String DIR = "######DIR######";

	public static final String NOTIFICATION_ID = "99";

	public static final String BASE_CONCEPT_NAMESPACE = "http://purl.bioontology.org/ontology/";

	/**
	 * RDF and RDF store namespace constants.
	 */
	public static final String DEFAULT_RDF_PREFIX = "http://purl.bioontology.org/bioportal#";
	public static final URI DEFAULT_RDF_TYPE = new URIImpl("http://purl.bioontology.org/bioportal#SPARQL_Bean");
	public static final String VIRTUOSO = "virtuoso";
	public static final String MYSQL = "mysql";
	public static final String MAPPING_PREFIX = "http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#";
	public static final String MAPPING_ID_PREFIX = "http://purl.bioontology.org/mapping/";
	public static final String PROVISIONAL_TERM_PREFIX = "http://purl.bioontology.org/ontology/provisional#";
	public static final String PROVISIONAL_TERM_ID_PREFIX = "http://purl.bioontology.org/ontology/provisional/";

	// This is equivalent to a named graph in an RDF store.
	public static final String MAPPING_CONTEXT = "http://protege.stanford.edu/ontologies/mappings/mappings.rdfs";
	public static final URI MAPPING_CONTEXT_URI = new URIImpl(MAPPING_CONTEXT);
	public static final String PROVISIONAL_TERM_CONTEXT = "http://purl.bioontology.org/ontology/provisional";
	public static final URI PROVISIONAL_TERM_CONTEXT_URI = new URIImpl(PROVISIONAL_TERM_CONTEXT);
	
	// Common URIs
	public static final URI MAPPING_ONE_TO_ONE_URI = new URIImpl(MAPPING_PREFIX + "One_To_One_Mapping");
	public static final URI PROVISIONAL_TERM_URI = new URIImpl(PROVISIONAL_TERM_PREFIX + "Provisional_Term");
	public static final URI RDF_TYPE_URI = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	
	public static final String TIMEZONE_ID = "Z";
	
	/**
	 * Mappings constants
	 */
	public static final Integer DEFAULT_MAPPINGS_PAGE_SIZE = 5000;
	public static final Integer DEFAULT_MAPPINGS_PAGE_NUM = 1;
	public static final Integer MAPPINGS_MAX_PAGE_SIZE = 50000;
    public static final Boolean GENERATE_UNION_SPARQL = true;


    /**
    * Mapping Predicates
    **/
    public static final String NS_MAPPING_PREFIX_URI = "http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#";
    public static final URI XSD_INTEGER = new URIImpl("http://www.w3.org/2001/XMLSchema#integer");
    public static final String NS_MAPPING_PREFIX = "map";
    public static final URI SOURCE_ONTOLOGY_ID = new URIImpl(NS_MAPPING_PREFIX +":source_ontology_id");
    public static final URI TARGET_ONTOLOGY_ID = new URIImpl(NS_MAPPING_PREFIX +":target_ontology_id");
    public static final URI SOURCE_TERM = new URIImpl(NS_MAPPING_PREFIX +":source");
    public static final URI TARGET_TERM = new URIImpl(NS_MAPPING_PREFIX +":target");
	
	/**
	 * File Uploading Constants
	 */
	public static final String CSVFile = ".csv";
	public static final String RDFFile = ".rdf";
	public static final String FileType = ".owl";
	public static final String RDFStoreType="rdfStoreType";
}
