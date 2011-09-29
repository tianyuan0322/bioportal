package org.ncbo.stanford.service.notification.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserSubscriptionsDAO;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.notification.NotificationManager;
import org.ncbo.stanford.service.notification.EmailNotificationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.textmanager.service.TextManager;

/**
 * Implementation of EmailNotificationService.
 *
 * @author g.prakash
 */

public class EmailNotificationServiceImpl implements EmailNotificationService {

	private Map<String, NotificationManager> notificationManagerMap = new HashMap<String, NotificationManager>();
	private CustomNcboUserSubscriptionsDAO ncboUserSubscriptionsDAO;
	private NcboUserDAO ncboUserDAO;
	private TextManager textManager;

	/**
	 *
	 * @param notificationManagerMap
	 */
	public void setNotificationManagerMap(
			Map<String, NotificationManager> notificationManagerMap) {
		this.notificationManagerMap = notificationManagerMap;
	}

	public void setTextManager(TextManager textManager) {
		this.textManager = textManager;
	}

	public void setNcboUserDAO(NcboUserDAO ncboUserDAO) {
		this.ncboUserDAO = ncboUserDAO;
	}

	public void setNcboUserSubscriptionsDAO(
			CustomNcboUserSubscriptionsDAO ncboUserSubscriptionsDAO) {
		this.ncboUserSubscriptionsDAO = ncboUserSubscriptionsDAO;
	}

	public Map<String, NotificationManager> getNotificationManagerMap() {
		return notificationManagerMap;
	}

	@Override
	public void sendNotification(NotificationTypeEnum notificationType,
			OntologyBean ontologyBean, HashMap<String, String> keywords,
			Set<String> recipients) {
		if (keywords == null) {
			keywords = new HashMap<String, String>();
		}

		// Totally broken way of getting additional params into this method
		// TODO: figure out how we would like to handle situations where
		// different notification methods will need different data
		String messageId = (keywords.get("messageId") != null) ? keywords
				.get("messageId") : null;
		String inReplyTo = (keywords.get("inReplyTo") != null) ? keywords
				.get("inReplyTo") : null;

		keywords.put(ApplicationConstants.ONTOLOGY_VERSION_ID, ontologyBean
				.getId().toString());

		textManager.appendKeywords(keywords);
		String from = MessageUtils.getMessage("notification.mail.from");
		String subject = textManager.getTextContent(notificationType.name()
				+ ApplicationConstants.SUBJECT_SUFFIX);
		String message = textManager.getTextContent(notificationType.name());

		addRecipients(ontologyBean, notificationType, recipients);

		// Send email to all recipients
		sendEmail(subject, message, from, messageId, inReplyTo, recipients);
	}

	/**
	 * This Method handle's the call for sending the EmailNotification to
	 * OntoogySubmitter
	 *
	 * @param notificationType
	 * @param ontologyBean
	 * @param keywords
	 */
	public void sendNotificationForOntology(
			NotificationTypeEnum notificationType, OntologyBean ontologyBean,
			HashMap<String, String> keywords) {
		if (keywords == null) {
			keywords = new HashMap<String, String>();
		}

		// Ontology parse failed
		boolean parseFailed = (ontologyBean.getStatusId() == StatusEnum.STATUS_ERROR
				.getStatus());

		// Getting the Address for Outgoing mail
		String from = MessageUtils.getMessage("notification.mail.from");

		keywords.put("CONTACT_EMAIL", from);

		// Add necessary information to keywords for use in generating email
		// body/subject
		keywords.put(ApplicationConstants.ONTOLOGY_DISPLAY_LABEL, ontologyBean
				.getDisplayLabel());

		// Should we say that parse failed or succeeded?
		String parseStatus = (parseFailed) ? "failed" : "was successful";
		keywords.put("PARSE_STATUS", parseStatus);
		keywords.put("PARSE_STATUS_UP", WordUtils.capitalize(parseStatus));

		// Display link to ontology
		String ontologyLink = MessageUtils.getMessage("ui.url")
				+ "/ontologies/" + ontologyBean.getOntologyId();
		keywords.put("ONTOLOGY_LINK", ontologyLink);

		// Only shows when parsing failed
		String parseMessage = (parseFailed) ? "The BioPortal team is investigating the issue."
				: "The parsed version of your ontology will display here within the next two hours:<br>"
						+ ontologyLink;
		keywords.put("PARSE_MESSAGE", parseMessage);

		textManager.appendKeywords(keywords);

		String message = textManager.getTextContent(notificationType.name());

		String subject = textManager.getTextContent(notificationType.name()
				+ ApplicationConstants.SUBJECT_SUFFIX);

		Set<String> recipients = new HashSet<String>();
		NcboUser submitter = ncboUserDAO.findById(ontologyBean.getUserId());
		recipients.add(submitter.getEmail());
		addRecipients(ontologyBean, NotificationTypeEnum.ALL_NOTIFICATION,
				recipients);

		if ((ontologyBean.isRemote() && parseFailed)
				|| !ontologyBean.isRemote()) {
			sendEmail(subject, message, from, null, null, recipients);
		}
	}

	/**
	 * This List Contains the NcboUserSubscriptions Field according to the
	 * OntolgyId And NotificationType and it also contains default Dummy
	 * OntologyID(99)
	 *
	 * @param ontologyBean
	 * @param notificationType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void addRecipients(OntologyBean ontologyBean,
			NotificationTypeEnum notificationType, Set<String> recipients) {
		if (recipients == null)
			return;

		NcboUser ncboUser = new NcboUser();

		List<NcboUserSubscriptions> ncboUserSubscriptions = ncboUserSubscriptionsDAO
				.findByOntologyIdAndNotificationType(ontologyBean
						.getOntologyId().toString(), notificationType);

		for (NcboUserSubscriptions userSubscriptions : ncboUserSubscriptions) {
			ncboUser = ncboUserDAO.findById(userSubscriptions.getUserId());

			UserBean userBean = new UserBean();
			userBean.populateFromEntity(ncboUser);

			recipients.add(userBean.getEmail());
		}
	}

	/**
	 * Sends an email to a list of recipients.
	 *
	 * @param subject
	 * @param message
	 * @param from
	 * @param messageId
	 * @param replyTo
	 * @param recipients
	 */
	private void sendEmail(String subject, String message, String from,
			String messageId, String replyTo, Set<String> recipients) {
		if (recipients != null && recipients.size() > 0) {
			for (String recipient : recipients) {
				notificationManagerMap.get(ApplicationConstants.EMAIL)
						.sendNotification(subject, message, from, null, null,
								recipient);
			}
		}
	}

}
