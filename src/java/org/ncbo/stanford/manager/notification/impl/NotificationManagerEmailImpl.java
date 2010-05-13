package org.ncbo.stanford.manager.notification.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private NcboAppTextDAO ncboAppTextDAO;
	private NcboAppText ncboAppText;
	private MailServiceImpl mailService;

	/**
	 * 
	 * @param ncboAppTextDAO
	 */
	public void setNcboAppTextDAO(NcboAppTextDAO ncboAppTextDAO) {
		this.ncboAppTextDAO = ncboAppTextDAO;
	}

	/**
	 * 
	 * @param mailService
	 */

	public void setMailService(MailServiceImpl mailService) {
		this.mailService = mailService;
	}

	/**
	 * OverRide the sendNitification Form the NotificationManager Class
	 */
	public String sendNotification() {

		String sendingMessage = getMailMessage();
		// System.out.println(sendingMessage);
		log.debug(sendingMessage);
		// Here email,from to is used for sending a mail from source to
		// destination here source is from and destination is used for email.
		// Here email is hardcoded for testing purpose.
		String email = "g.prakash@optrasystems.com";
		String from = "g.prakash@optrasystems.com";
		String to = email;
		String sub = "Welcome";

		mailService.sendMail(from, to, sub, sendingMessage);
		return sendingMessage;

	}

	/**
	 * 
	 * @return String
	 */
	private String getMailMessage() {
		// Execute the Query findById(String id)from the NcboAppTextDAO and
		// collect in NcboAppText.Here NcboAppText is uses for Testing purpose.

		ncboAppText = ncboAppTextDAO.findById("Diagnosis");
		// Collect the Message Form message.properties in messageText For
		// Testing Purpose
		String messageText = MessageUtils.getMessage("msg.email.notification");
		// Replace the {class name} and {ontology name} by identifier content
		// from the messageText
		String messageReplace = messageText.replace("{class name}", ncboAppText
				.getIdentifier());
		String messageFinal = messageReplace.replace("{ontology name}",
				ncboAppText.getTextContent());

		return messageFinal;

	}

}
