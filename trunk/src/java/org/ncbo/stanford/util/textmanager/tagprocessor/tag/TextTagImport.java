package org.ncbo.stanford.util.textmanager.tagprocessor.tag;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ncbo.stanford.util.textmanager.tagprocessor.TextTagProcessor;

/**
 * @author Michael Dorf
 *
 * The class to process the <TAG_IMPORT textIdent> tag
 */
public class TextTagImport extends TextTag {

	private static Vector<String> useIdents = null;  // a vector used to keep track of
												     // the recursive template calls

	/* (non-Javadoc)
	 * @see TextTag#process(TagProcessor, java.lang.String)
	 */	
	public String process(TextTagProcessor tp, String tagLine) {
		String result = "";
		
		try {
			Pattern pat = Pattern.compile("<TAG_\\w+\\s+(.*)>");
			Matcher mat = pat.matcher(tagLine);
			mat.matches();
			String val = mat.group(1).trim();

			// static object to keep all text identifiers
			// that have been processed so far
			// used to prevent recursive calls to each other
			// within two or more templates
			if (useIdents == null) {
				useIdents = new Vector<String>();	
			}
		
			// add this identifier to the global
			// identifiers object to avoid recursive
			// calls to it from lower templates
			useIdents.add(tp.getTextIdent());

			// avoid recursive calls to the same identifiers
			// within other templates
			if (useIdents.contains(val)) {
				result = "";
			} else {
				String curTextIdent = tp.getTextIdent();
				String curText = tp.getText();
				result = tp.getTM().getTextContent(val);
				tp.setTextIdent(curTextIdent);
				tp.setText(curText);
			}

			// resetting global identifiers object
			useIdents.removeAllElements();
		} catch (Exception e) {
			e.printStackTrace();
			result = "";
		}		

		return result;
	}
}
