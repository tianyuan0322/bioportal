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
			String ontology_version_id) {

		List<NcboUserSubscriptions> ncboUserSubscriptions = ncboUserSubscriptionsDAO
				.findByOntologyVersionId(ontology_version_id);

		for (NcboUserSubscriptions ncboUserSubscription : ncboUserSubscriptions) {
			NcboUser ncboUser = ncboUserDAO.findById(ncboUserSubscription
					.getUserId());

			HashMap<String, String> keywords;

			UserBean userBean = new UserBean();
			userBean.populateFromEntity(ncboUser);
			String from = MessageUtils.getMessage("notification.mail.from");
			keywords = new HashMap<String, String>();

			keywords.put(ApplicationConstants.ONTOLOGY_VERGION_ID,
					ontology_version_id);
			keywords.put(ApplicationConstants.USERNAME, ncboUser.getUsername());

			textManager.appendKeywords(keywords);
			String message = textManager
					.getTextContent(notificationType.name());
			String subject = textManager.getTextContent(notificationType.name()
					+ ApplicationConstants.SUBJECT_PREFIX);

			notificationManagerMap.get(ApplicationConstants.EMAIL)
					.sendNotification(subject, message, from, userBean);

		}

	}

}
