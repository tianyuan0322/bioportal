package org.ncbo.stanford.service.notification.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptionsDAO;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.manager.notification.NotificationManager;
import org.ncbo.stanford.service.notification.NotificationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.textmanager.service.TextManager;
import org.ncbo.stanford.bean.OntologyBean;

/**
 * Implementation of NotificationService.
 * 
 * @author g.prakash
 */

public class NotificationServiceImpl implements NotificationService {

	private Map<String, NotificationManager> notificationManagerMap = new HashMap<String, NotificationManager>();

	private NcboUserSubscriptionsDAO ncboUserSubscriptionsDAO;
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
			NcboUserSubscriptionsDAO ncboUserSubscriptionsDAO) {
		this.ncboUserSubscriptionsDAO = ncboUserSubscriptionsDAO;
	}

	public Map<String, NotificationManager> getNotificationManagerMap() {
		return notificationManagerMap;
	}

	@Override
	public void sendNotification(NotificationTypeEnum notificationType,
			OntologyBean ontologyBean, HashMap<String, String> keywords) {
		if (keywords == null) {
			keywords = new HashMap<String, String>();
		}

		List<NcboUserSubscriptions> ncboUserSubscriptions = ncboUserSubscriptionsDAO
				.findByOntologyId(ontologyBean.getOntologyId().toString());

		for (NcboUserSubscriptions ncboUserSubscription : ncboUserSubscriptions) {
			NcboUser ncboUser = ncboUserDAO.findById(ncboUserSubscription
					.getUserId());

			UserBean userBean = new UserBean();
			userBean.populateFromEntity(ncboUser);
			String from = MessageUtils.getMessage("notification.mail.from");

			keywords.put(ApplicationConstants.ONTOLOGY_VERSION_ID,
					ontologyBean.getId().toString());
			keywords.put(ApplicationConstants.USERNAME, userBean.getUsername());

			textManager.appendKeywords(keywords);
			String message = textManager
					.getTextContent(notificationType.name());
			String subject = textManager.getTextContent(notificationType.name()
					+ ApplicationConstants.SUBJECT_SUFFIX);

			notificationManagerMap.get(ApplicationConstants.EMAIL)
					.sendNotification(subject, message, from, userBean);

		}

	}

}
