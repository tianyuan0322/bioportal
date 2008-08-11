package org.ncbo.stanford.util.textmanager.tagprocessor.tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ncbo.stanford.util.textmanager.tagprocessor.TextTagProcessor;

/**
 * @author Michael Dorf
 *
 * The class to process the <TAG_SETKEYWORD KEY "VALUE"> tag
 */
public class TextTagSetkeyword extends TextTag {

	/* (non-Javadoc)
	 * @see TextTag#process(TextTagProcessor, java.lang.String)
	 */
	public String process(TextTagProcessor tp, String tagLine) {
		try {
			Pattern pat = Pattern.compile("<TAG_\\w+\\s+(\\w+)\\s+[\"\']*([\\w\\d\\s\\.\\-]+)[\"\']*>");			
			Matcher mat = pat.matcher(tagLine);
			mat.matches();
			String key = mat.group(1).trim();
			String val = mat.group(2).trim();
			tp.setKeyword(key, val);
		} catch (Exception e) {
		}

		return "";
	}
}
