package org.ncbo.stanford.util.textmanager.tagprocessor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.textmanager.service.TextManager;
import org.ncbo.stanford.util.textmanager.tagprocessor.tag.TextTag;

/**
 * @author Michael Dorf
 * 
 * The main tag processing engine. Traverses the entire template, executing
 * (processing) each tag it encounters.
 */
public class TextTagProcessor {

	private HashMap<String, String> keywords = new HashMap<String, String>(); // stores
	// the
	// list
	// of
	// keywords
	private String textIdent = ""; // the text identifier
	private String text = ""; // the current text to be processed
	private TextManager tm = null; // the instance of TextManager to

	// which this instance of TagProcessor belongs

	/**
	 * @param tm -
	 *            an instance of TextManager
	 */
	public TextTagProcessor(TextManager tm) {
		this(tm, "", "");
	}

	/**
	 * @param tm -
	 *            an instance of TextManager
	 * @param text -
	 *            the current text to process
	 * @param textIdent -
	 *            the current text identifier
	 */
	public TextTagProcessor(TextManager tm, String text, String textIdent) {
		this.tm = tm;
		setTextIdent(textIdent);
		setText(text);
	}

	/**
	 * Sets a given keyword
	 * 
	 * @param key -
	 *            the keyword key
	 * @param value -
	 *            the keyword value
	 * @return - the previous value of the keyword
	 */
	public String setKeyword(String key, String value) {
		return (String) keywords.put(key, value);
	}

	/**
	 * Append keywords to the existing collection
	 * 
	 * @param keywords
	 */
	public void appendKeywords(HashMap<String, String> keywords) {
		String key = null;

		for (Iterator<String> iter = keywords.keySet().iterator(); iter
				.hasNext();) {
			key = iter.next();
			this.keywords.put(key, keywords.get(key));
		}
	}

	/**
	 * Overwrite the existing Keywords collection
	 * 
	 * @param keywords
	 */
	public void setKeywords(HashMap<String, String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * Returns the value of a keyword
	 * 
	 * @param key -
	 *            the keyword key
	 * @return - the keyword value
	 */
	public String getKeyword(String key) {
		String value = (String) keywords.get(key);

		if (value == null) {
			value = "";
		}

		return value;
	}

	/**
	 * The setter of the text
	 * 
	 * @param text -
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * The setter of the text identifier
	 * 
	 * @param textIdent -
	 *            the identifer to set
	 */
	public void setTextIdent(String textIdent) {
		this.textIdent = textIdent;
	}

	/**
	 * The getter for the text identifier
	 * 
	 * @return the current text identifier
	 */
	public String getTextIdent() {
		return textIdent;
	}

	/**
	 * The getter for the text
	 * 
	 * @return the current text
	 */
	public String getText() {
		return text;
	}

	/**
	 * The getter for the TextManager instance
	 * 
	 * @return the current instance of TextManager (TM)
	 */
	public TextManager getTM() {
		return tm;
	}

	/**
	 * Traverses the current text, processing all the tags within it
	 * 
	 * @return the processed text
	 */
	public String getProcessedText() {
		String result = "";
		int start = 0;
		int end = 0;
		String tagStr = "";
		String tempStr = "";
		start = text.indexOf("<TAG_");

		while (start >= 0) {
			tempStr = text.substring(end, start);
			result += tempStr;
			end = text.indexOf(">", start) + 1;
			tagStr = text.substring(start, end);
			result += processTag(tagStr);
			start = text.indexOf("<TAG_", end);
		}

		tempStr = text.substring(end, text.length());
		result += tempStr;

		return result;
	}

	/**
	 * Writes out the processed text to the standard output
	 */
	public void writeProcessedText() {
		System.out.print(getProcessedText());
	}

	// Private
	// ////////////////////////////////////////////////////////

	/**
	 * Processes an individual tag by dispatching the "process" task to the
	 * appropriate tag class
	 * 
	 * @param tagLine -
	 *            the tag string
	 * @return the value of the processed tag
	 */
	private String processTag(String tagLine) {
		String result = "";

		try {
			Pattern pat = Pattern.compile("<TAG_(\\w+)\\s+(.*)>");
			Matcher mat = pat.matcher(tagLine);
			mat.matches();
			TextTag curTag = null;
			String tagClassName = TextTag.getTagClassName(mat.group(1));

			if (tagClassName != null) {
				// get the appropriate tag class
				curTag = (TextTag) (Class
						.forName(ApplicationConstants.TM_TAG_JAVA_PACKAGE + "."
								+ tagClassName).newInstance());
				result += curTag.process(this, tagLine);
			}
		} catch (Exception e) {
			result = "";
		}

		return result;
	}
}
