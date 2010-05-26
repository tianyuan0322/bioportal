package org.ncbo.stanford.manager.notification.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.generated.NcboAppText;
import org.ncbo.stanford.domain.generated.NcboAppTextDAO;
import org.ncbo.stanford.manager.notification.NotificationManager;
import org.ncbo.stanford.util.MessageUtils;
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
			UserBean userBean) {
		log.info("Sending Notification Mail to User :" + userBean.getUsername()
				+ " with email id :" + userBean.getEmail());
		mailService.sendMail(from, userBean.getEmail(), subject, message);

	}

}
