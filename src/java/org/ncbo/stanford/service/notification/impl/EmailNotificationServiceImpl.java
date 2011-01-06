package org.ncbo.stanford.service.notification.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserSubscriptionsDAO;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
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
			OntologyBean ontologyBean, HashMap<String, String> keywords) {
		NcboUser ncboUser = new NcboUser();
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

		// This List Contains the NcboUserSubscriptions Field according to the
		// OntolgyId And NotificationType and it also contains default Dummy
		// OntologyID(99)

		List<NcboUserSubscriptions> ncboUserSubscriptions = ncboUserSubscriptionsDAO
				.findByOntologyIdAndNotificationType(ontologyBean
						.getOntologyId().toString(), notificationType);
		for (NcboUserSubscriptions userSubscriptions : ncboUserSubscriptions) {
			ncboUser = ncboUserDAO.findById(userSubscriptions.getUserId());

			UserBean userBean = new UserBean();
			userBean.populateFromEntity(ncboUser);
			String from = MessageUtils.getMessage("notification.mail.from");

			keywords.put(ApplicationConstants.ONTOLOGY_VERSION_ID, ontologyBean
					.getId().toString());
			keywords.put(ApplicationConstants.USERNAME, userBean.getUsername());

			textManager.appendKeywords(keywords);
			String message = textManager
					.getTextContent(notificationType.name());
			String subject = textManager.getTextContent(notificationType.name()
					+ ApplicationConstants.SUBJECT_SUFFIX);

			notificationManagerMap.get(ApplicationConstants.EMAIL)
					.sendNotification(subject, message, from, messageId,
							inReplyTo, userBean);
		}
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
		// Instantiate the NcboUser
		NcboUser ncboUser = new NcboUser();
		// Instantiate the UserBean
		UserBean userBean = new UserBean();

		try {
			// Condition for param keywords is null or not
			if (keywords == null) {
				keywords = new HashMap<String, String>();
			}
			// Creating messageId
			String messageId = (keywords.get("messageId") != null) ? keywords
					.get("messageId") : null;
			// Creating inReplyTo
			String inReplyTo = (keywords.get("inReplyTo") != null) ? keywords
					.get("inReplyTo") : null;
			// Getting the NcboUser according to Id
			ncboUser = ncboUserDAO.findById(ontologyBean.getId());
			// Populating the UserBean
			userBean.populateFromEntity(ncboUser);
			// Getting the Address for Outgoing mail
			String from = MessageUtils.getMessage("notification.mail.from");
			keywords.put(ApplicationConstants.ONTOLOGY_VERSION_ID, ontologyBean
					.getOntologyId().toString());
			keywords.put(ApplicationConstants.USERNAME, userBean.getUsername());
			textManager.appendKeywords(keywords);
			// Creating the message
			String message = textManager
					.getTextContent(notificationType.name());
			// Creating the Subject
			String subject = textManager.getTextContent(notificationType.name()
					+ ApplicationConstants.SUBJECT_SUFFIX);
			notificationManagerMap.get(ApplicationConstants.EMAIL)
					.sendNotification(subject, message, from, messageId,
							inReplyTo, userBean);
		} catch (Exception exception) {
			exception.getMessage();
		}
	}
}
