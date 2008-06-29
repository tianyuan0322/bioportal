package org.ncbo.stanford.util.constants;


public interface ApplicationConstants {

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
	 * File extensions
	 */
	public static final String OBO_EXTENSION = "obo";
	public static final String PROTEGE_EXTENSION = "pprj";
	public static final String OWL_EXTENSION = "owl";
	public static final String XML_EXTENSION = "xml";
	public static final String ZIP_EXTENSION = "zip";
	public static final String JAR_EXTENSION = "jar";
	public static final String TAR_EXTENSION = "tar";
	
	/**
	 * Ontology version status
	 */
	public static final String VERSION_STATUS_PREPRODUCTION = "pre_production";
	public static final String VERSION_STATUS_PRODUCTION = "production";
	
	/**
	 * Composite filename used for joining multiple ontologies into a single file
	 */
	public static final String COMPOSITE_FILENAME = "joined";
	
	/**
	 * Buffer size for compressed files
	 */
	public static final Integer BUFFER_SIZE = 2048;

	/**
	 * Ontology formats
	 */
	public static final String FORMAT_OWL_DL = "OWL-DL";
	public static final String FORMAT_OWL_FULL = "OWL-FULL";
	public static final String FORMAT_PROTEGE = "PROTEGE";
	public static final String FORMAT_OBO = "OBO";
	public static final String FORMAT_UMLS_RRF = "UMLS-RRF";
	public static final String FORMAT_LEXGRID_XML = "LEXGRID-XML";
	public static final String FORMAT_INVALID = "Invalid";
	
	/**
	 * Hierarchy types
	 */
	public static final String SUB_CLASS = "SubClass";
	public static final String SUPER_CLASS = "SuperClass";
	public static final String RDF_TYPE = "RdfType";	
	public static final String CHILD_COUNT = "ChildCount";
	
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
	public static final String[] ALL_TAGS = {KEYWORD, IMPORT, SETKEYWORD};
	
	// Content retrieval constants
	public static final int FROM_DB = 1200;
	public static final int FROM_CACHE = 1201;
	
	/**
	 * XML declaration
	 */
	public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	/**
	 * The name of the root tag of all xml error response messages
	 */
	public static final String ERROR_XML_TAG_NAME = "error";

	/**
	 * The name of the root tag of success xml response message
	 */
	public static final String SUCCESS_XML_TAG_NAME = "success";

	/**
	 * The name of the root tag of response xml response message
	 */
	public static final String RESPONSE_XML_TAG_NAME = "response";
}