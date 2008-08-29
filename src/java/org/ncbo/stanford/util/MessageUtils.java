package org.ncbo.stanford.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.messages.Messages;

/**
 * Class responsible for exposing and using the global resource bundle
 * 
 * @author Michael Dorf
 * 
 */
public class MessageUtils {

	private static final Log log = LogFactory.getLog(MessageUtils.class);

	private static Messages messages;

	/**
	 * Initializes the messages bundle
	 */
	public MessageUtils(String basename) {
		if (messages == null) {
			messages = new Messages(basename);
		}
	}

	/**
	 * Return a message from the message bundle using its key and specified
	 * parameters
	 * 
	 * @param msgKey
	 * @param params
	 *            Parameter replacement values
	 * @return
	 */
	public static String getMessage(String msgKey, Object params[]) {
		return messages.getMessage(msgKey, params);
	}

	/**
	 * Return a message from the message bundle using its key
	 * 
	 * @param msgKey
	 * @return
	 */
	public static String getMessage(String msgKey) {
		return messages.getMessage(msgKey);
	}
}
