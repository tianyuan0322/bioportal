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
}