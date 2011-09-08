package org.ncbo.stanford.manager.notification.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.manager.notification.NotificationManager;
import org.ncbo.stanford.util.mail.impl.MailServiceImpl;

/**
 * A implementation of the NotificationManager for providing mail notification
 * on around() method .
 *
 * @author g.prakash
 *
 */
public class NotificationManagerEmailImpl implements NotificationManager {
	private static final Log log = LogFactory.getLog(MailServiceImpl.class);

	private MailServiceImpl mailService;

	/**
	 *
	 * @param mailService
	 */

	public void setMailService(MailServiceImpl mailService) {
		this.mailService = mailService;
	}

	@Override
	public void sendNotification(String subject, String message, String from,
			String messageId, String inReplyTo, String email) {
		log.info("Sending Notification to: " + email);
		mailService.sendMail(from, email, subject, messageId,
				inReplyTo, message);

	}

}
