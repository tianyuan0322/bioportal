package org.ncbo.stanford.util.textmanager.service;

import java.util.HashMap;

import org.ncbo.stanford.domain.generated.NcboAppText;

/**
 * @author Michael Dorf
 * 
 * Interface for TextManager service
 */
public interface TextManager {

	public String getTextContent(String textIdent);

	public String getTextContent(String textIdent, int sourceFlag);

	public NcboAppText getTextContentTemplate(String textIdent);

	public String setKeyword(String key, String value);

	public void appendKeywords(HashMap<String, String> keywords);

	public void setKeywords(HashMap<String, String> keywords);

	public String getKeyword(String key);
}
