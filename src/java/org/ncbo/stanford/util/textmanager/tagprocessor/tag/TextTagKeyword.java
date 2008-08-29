package org.ncbo.stanford.util.textmanager.tagprocessor.tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ncbo.stanford.util.textmanager.tagprocessor.TextTagProcessor;

/**
 * @author Michael Dorf
 * 
 * The class to process the <TAG_KEYWORD KEY> tag
 */
public class TextTagKeyword extends TextTag {

	/*
	 * (non-Javadoc)
	 * 
	 * @see TextTag#process(TextTagProcessor, java.lang.String)
	 */
	public String process(TextTagProcessor tp, String tagLine) {
		String result = "";

		try {
			Pattern pat = Pattern.compile("<TAG_\\w+\\s+(.*)>");
			Matcher mat = pat.matcher(tagLine);
			mat.matches();
			result = tp.getKeyword(mat.group(1).trim());
		} catch (Exception e) {
			result = "";
		}

		return result;
	}
}
