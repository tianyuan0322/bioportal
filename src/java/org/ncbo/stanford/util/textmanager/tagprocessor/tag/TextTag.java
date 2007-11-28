package org.ncbo.stanford.util.textmanager.tagprocessor.tag;

import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.textmanager.tagprocessor.TextTagProcessor;

/**
 * @author Michael Dorf
 *
 * An abstract class to define the generic
 * TextManager Tag behavior
 */
public abstract class TextTag {
	
	public TextTag() {
	}

	/**
	 * Process the current tag, return the processed value
	 * @param tp - an instance of TagProcessor
	 * @param tagLine - the string representing the tag
	 * 			Ex: <TAG_KEYWORD HELLO>
	 * @return the processed value of the tag
	 */
	public abstract String process(TextTagProcessor tp, String tagLine);
	
	
	/**
	 * Checks if a given tag is valid
	 * @param the tag name
	 * @return true/false
	*/	
	public static boolean tagExists(String tagName) {
		for (int i = 0; i < ApplicationConstants.ALL_TAGS.length; i++) {
			if (tagName.equalsIgnoreCase(ApplicationConstants.ALL_TAGS[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * returns the class name for a given tag
	 * @param the tag name
	 * @return the class name or null if no class exists
	*/	
	public static String getTagClassName(String tagName) {
		if (tagExists(tagName)) {
			return ApplicationConstants.TEXT_TAG_PREFIX + 
					ApplicationConstants.TEXT_TAG_SUFFIX + 
					tagName.substring(0, 1).toUpperCase() +
					tagName.substring(1).toLowerCase();
		}
		
		return null;
	}	
}
