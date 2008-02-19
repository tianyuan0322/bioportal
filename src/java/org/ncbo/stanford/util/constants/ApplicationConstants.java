package org.ncbo.stanford.util.constants;


public interface ApplicationConstants {

	/**
	 * Standard "NO VALUE" constant
	 */
	public static final int NO_VAL = -1;

	/**
	 * Default encoding message key
	 */
	public static final String DEFAULT_ENCODING_KEY = "default.encoding";
	
	/**
	 * Boolean-to-bit conversions
	 */
	public static final Byte TRUE = 1;
	public static final Byte FALSE = 0;
	

	/**
	 * Ontology formats
	 */
	public static final String FORMAT_OWL_DL = "OWL-DL";
	public static final String FORMAT_OWL_FULL = "OWL-FULL";
	public static final String FORMAT_PROTEGE = "PROTEGE";
	public static final String FORMAT_OBO = "OBO";
	public static final String FORMAT_UMLS_RRF = "UMLS-RRF";
	public static final String FORMAT_LEXGRID_XML = "LEXGRID-XML";
	
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