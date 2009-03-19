package org.ncbo.stanford.util.textmanager.service.impl;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.domain.custom.dao.CustomNcboAppTextDAO;
import org.ncbo.stanford.domain.generated.NcboAppText;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.textmanager.service.TextManager;
import org.ncbo.stanford.util.textmanager.tagprocessor.TextTagProcessor;

/**
 * @author Michael Dorf
 * 
 * Provides the main engine class for TextManager. Contains all public API.
 */
public class TextManagerImpl implements TextManager {

	// public static void main(String[] args) {
	// XmlBeanFactory beanFactory = new XmlBeanFactory(
	// new ClassPathResource("applicationContext.xml"));
	// TextManager tm = (TextManagerImpl) beanFactory.getBean("textManager");
	// tm.setKeyword("TEST_WORD", "Java is beautiful");
	// String content = tm.getContent("java_test");
	// System.out.println(content);
	// }

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(TextManagerImpl.class);
	private CustomNcboAppTextDAO ncboAppTextDAO = null;
	private TextTagProcessor tp = null; // instance of TagProcessor

	public TextManagerImpl() {
		tp = new TextTagProcessor(this);
	}

	/**
	 * Returns content for a given text identifier
	 * 
	 * @param a
	 *            text identifier
	 * @return the processed content
	 */
	public String getTextContent(String textIdent) {
		return getTextContent(textIdent, ApplicationConstants.FROM_DB);
	}

	/**
	 * Returns content for a given text identifier using the specified source:
	 * CoreConstants.FROM_DB [default] CoreConstants.FROM_CACHE
	 * 
	 * @param textIdent -
	 *            a text identifier
	 * @param sourceFlag
	 *            (FROM_DB/FROM_CACHE)
	 * @return the processed content
	 */
	public String getTextContent(String textIdent, int sourceFlag) {
		String content = ApplicationConstants.TEXT_NOT_FOUND;

		switch (sourceFlag) {
		case ApplicationConstants.FROM_DB:
		case ApplicationConstants.FROM_CACHE:
			// TODO: implement this :)
		default:
			NcboAppText at = ncboAppTextDAO.findById(textIdent);

			if (at != null) {
				content = at.getTextContent();
			} else {
				content = ApplicationConstants.TEXT_NOT_FOUND;
			}

			if (content == ApplicationConstants.TEXT_NOT_FOUND) {
				content = "";
			} else {
				tp.setTextIdent(textIdent);
				tp.setText(content);
				content = tp.getProcessedText();
			}
		}

		return content;
	}

	/**
	 * Return all information about a given template
	 * 
	 * @param textIdent -
	 *            text identifier
	 * @return a template object or null, if template does not exist
	 */
	public NcboAppText getTextContentTemplate(String textIdent) {
		return ncboAppTextDAO.findById(textIdent);
	}

	/**
	 * Adds a keyword into the tag processor's keyword collection, used to
	 * define dynamically generated values inside a text
	 * 
	 * @param the
	 *            key
	 * @param the
	 *            value
	 * @return the previous value of the keyword
	 */
	public String setKeyword(String key, String value) {
		return tp.setKeyword(key, value);
	}

	/**
	 * Append keywords to the existing keywords collection
	 * 
	 * @param keywords
	 */
	public void appendKeywords(HashMap<String, String> keywords) {
		tp.appendKeywords(keywords);
	}

	/**
	 * Overwrite the existing Keywords collection
	 * 
	 * @param keywords
	 */
	public void setKeywords(HashMap<String, String> keywords) {
		tp.setKeywords(keywords);
	}

	/**
	 * Returns the value of a keyword identified by the given key
	 * 
	 * @param the
	 *            key of the keyword
	 * @return the value of the keyword
	 */
	public String getKeyword(String key) {
		return tp.getKeyword(key);
	}

	/**
	 * @return the ncboAppTextDAO
	 */
	public CustomNcboAppTextDAO getNcboAppTextDAO() {
		return ncboAppTextDAO;
	}

	/**
	 * @param ncboAppTextDAO
	 *            the ncboAppTextDAO to set
	 */
	public void setNcboAppTextDAO(CustomNcboAppTextDAO ncboAppTextDAO) {
		this.ncboAppTextDAO = ncboAppTextDAO;
	}
}
