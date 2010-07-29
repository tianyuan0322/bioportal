package org.ncbo.stanford.service.subscriptions.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ncbo.stanford.bean.SubscriptionsBean;

import org.ncbo.stanford.service.subscriptions.SubscriptionsService;

import org.ncbo.stanford.domain.custom.dao.CustomNcboLNotificationTypeDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboAppTextDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserSubscriptionsDAO;

import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;

import org.ncbo.stanford.domain.generated.NcboLNotificationType;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.mail.impl.MailServiceImpl;
import org.ncbo.stanford.util.textmanager.service.TextManager;

public class SubscriptionsServiceImpl implements SubscriptionsService {

	private CustomNcboUserSubscriptionsDAO ncboUserSubscriptionsDAO;

	private CustomNcboLNotificationTypeDAO ncboLNotificationTypeDAO;

	private MailServiceImpl mailService;
	private NcboUserDAO ncboUserDAO;
	private TextManager textManager;
	private CustomNcboAppTextDAO textDAO;

	public void setTextManager(TextManager textManager) {
		this.textManager = textManager;
	}

	public void setMailService(MailServiceImpl mailService) {
		this.mailService = mailService;
	}

	public void setNcboUserSubscriptionsDAO(
			CustomNcboUserSubscriptionsDAO ncboUserSubscriptionsDAO) {
		this.ncboUserSubscriptionsDAO = ncboUserSubscriptionsDAO;
	}

	public void setNcboUserDAO(NcboUserDAO ncboUserDAO) {
		this.ncboUserDAO = ncboUserDAO;
	}

	public void setNcboLNotificationTypeDAO(
			CustomNcboLNotificationTypeDAO ncboLNotificationTypeDAO) {
		this.ncboLNotificationTypeDAO = ncboLNotificationTypeDAO;
	}

	/**
	 * This method creates the Subscriptions According to SubscriptionsBean
	 * 
	 * @param subscriptionsBean
	 * 
	 */

	public void createSubscriptions(SubscriptionsBean subscriptionsBean) {

		NcboUserSubscriptions ncboUserSubscriptions = new NcboUserSubscriptions();

		NcboLNotificationType notificationType = new NcboLNotificationType();
		// Creating the List of NcboLNotificationType
		List<NcboLNotificationType> listOfNotificationType = new ArrayList<NcboLNotificationType>();
		// Populating the NcboUserSubscriptions from SubscriptonsBean
		subscriptionsBean.populateToEntity(ncboUserSubscriptions);
		// Populating the list of listOfNotificationType from the database
		// accrding to the subscriptionsBean
		listOfNotificationType = ncboLNotificationTypeDAO
				.findByType(subscriptionsBean.getNotificationType().toString());
		for (NcboLNotificationType ncboLNotificationType : listOfNotificationType) {
			ncboUserSubscriptions
					.setNcboLNotificationType(ncboLNotificationType);

		}

		ncboUserSubscriptionsDAO.save(ncboUserSubscriptions);
		
		NcboUser ncboUser = ncboUserDAO.findById(subscriptionsBean.getUserId());
		// Creating the Map For messageId and for reply
		HashMap<String, String> keywords = new HashMap<String, String>();

		String messageId = (keywords.get("messageId") != null) ? keywords
				.get("messageId") : null;

		String inReplyTo = (keywords.get("inReplyTo") != null) ? keywords
				.get("inReplyTo") : null;

		String from = MessageUtils.getMessage("notification.mail.from");
		// getting the email
		String email = ApplicationConstants.EMAIL;
		keywords.put(ApplicationConstants.ONTOLOGY_VERSION_ID,
				subscriptionsBean.getOntologyId().toString());
		keywords.put(ApplicationConstants.USERNAME, ncboUser.getUsername());

		textManager.appendKeywords(keywords);

		String message = textManager.getTextContent(subscriptionsBean
				.getNotificationType().toString());
		String subject = textManager.getTextContent(subscriptionsBean
				.getNotificationType().toString()
				+ ApplicationConstants.SUBJECT_SUFFIX);

		mailService.sendMail(from, email, subject, messageId, inReplyTo,
				message);

	}

	/**
	 * This methods creates the List Of All Subscriptions According to userId
	 * 
	 * @param userId
	 *@return subscriptionsBeanList
	 */
	public List<SubscriptionsBean> listOfSubscriptions(Integer userId) {

		// List of NcboUserSubscriptions its contains the All subscriptions
		// According to userId
		List<NcboUserSubscriptions> ncboUserSubscriptionsList = ncboUserSubscriptionsDAO
				.findByUserId(userId);

		List<SubscriptionsBean> subscriptionsBeanList = new ArrayList<SubscriptionsBean>();

		// populate the query result to subscriptionsBean
		for (NcboUserSubscriptions ncboUserSubscriptions : ncboUserSubscriptionsList) {
			SubscriptionsBean subscriptionsBean = new SubscriptionsBean();
			subscriptionsBean.populateFromEntity(ncboUserSubscriptions);
			subscriptionsBeanList.add(subscriptionsBean);
		}
		return subscriptionsBeanList;
	}

	/**
	 * This Method is called inside SubscriptionsRestlet Before Updating or
	 * Deleting Subscriptions
	 * 
	 * @param userId
	 * @return subscriptionsBean
	 * 
	 */
	public SubscriptionsBean findSubscriptionsForUserId(Integer userId) {
		SubscriptionsBean subscriptionsBean = null;
		// get subscriptionsDAO instance using userId
		List<NcboUserSubscriptions> userSubscriptions = new ArrayList<NcboUserSubscriptions>();
		userSubscriptions = ncboUserSubscriptionsDAO.findByUserId(userId);
		for (NcboUserSubscriptions subscriptions : userSubscriptions) {

			// populate userBean from ncbuUser with matched userId
			subscriptionsBean = new SubscriptionsBean();
			subscriptionsBean.populateFromEntity(subscriptions);

		}
		return subscriptionsBean;
	}

	/**
	 * @param subscriptionsBean
	 *            This method update the Subscriptions from the
	 *            SubscriptionsBean
	 */
	public void updateSubscriptions(SubscriptionsBean subscriptionsBean) {

		NcboUserSubscriptions ncboUserSubscriptions = new NcboUserSubscriptions();

		NcboLNotificationType notificationType = new NcboLNotificationType();
		// Creating the list of NcboLNotificationType
		List<NcboLNotificationType> ncboLNotificationtype = new ArrayList<NcboLNotificationType>();
		// Creating the list of NcboUserSubscriptions
		List<NcboUserSubscriptions> userSubscriptions = new ArrayList<NcboUserSubscriptions>();
		// Populating the List Of NcboUserSubscriptions from the database
		// according to the UserId of Subscriptions Bean
		userSubscriptions = ncboUserSubscriptionsDAO
				.findByUserId(subscriptionsBean.getUserId());

		for (NcboUserSubscriptions subscriptions : userSubscriptions) {
			// Populating the NcboUserSubscriptions from the SubscriptionsBean
			subscriptionsBean.populateToEntity(ncboUserSubscriptions);

			ncboLNotificationtype = ncboLNotificationTypeDAO
					.findByType(subscriptionsBean.getNotificationType()
							.toString());
			ncboUserSubscriptions
					.setNcboLNotificationType(ncboLNotificationtype.get(0));
			ncboUserSubscriptionsDAO.save(ncboUserSubscriptions);
		}

		NcboUser ncboUser = ncboUserDAO.findById(subscriptionsBean.getUserId());
		// Creating the Map For messageId and for reply
		HashMap<String, String> keywords = new HashMap<String, String>();

		String messageId = (keywords.get("messageId") != null) ? keywords
				.get("messageId") : null;

		String inReplyTo = (keywords.get("inReplyTo") != null) ? keywords
				.get("inReplyTo") : null;

		String from = MessageUtils.getMessage("notification.mail.from");
		// getting the email
		String email = ApplicationConstants.EMAIL;
		keywords.put(ApplicationConstants.ONTOLOGY_VERSION_ID,
				subscriptionsBean.getOntologyId().toString());
		keywords.put(ApplicationConstants.USERNAME, ncboUser.getUsername());

		textManager.appendKeywords(keywords);

		String message = textManager.getTextContent(subscriptionsBean
				.getNotificationType().toString());
		String subject = textManager.getTextContent(subscriptionsBean
				.getNotificationType().toString()
				+ ApplicationConstants.SUBJECT_SUFFIX);

		mailService.sendMail(from, email, subject, messageId, inReplyTo,
				message);

	}

	/**
	 * @param subscriptionsBean
	 * 
	 */
	public void removeSubscriptions(SubscriptionsBean subscriptionsBean) {

		NcboUserSubscriptions userSubscriptions = new NcboUserSubscriptions();
		List<NcboUserSubscriptions> listOfSubscriptions = new ArrayList<NcboUserSubscriptions>();
		NcboLNotificationType ncboLNotificationType = new NcboLNotificationType();
		listOfSubscriptions = ncboUserSubscriptionsDAO
				.findByOntologyIdAndNotificationType(subscriptionsBean
						.getOntologyId(), subscriptionsBean
						.getNotificationType());

		for (NcboUserSubscriptions subscriptions : listOfSubscriptions) {

			ncboUserSubscriptionsDAO.delete(subscriptions);
		}

	}

	public List<SubscriptionsBean> listOfSubscriptionsForOntologyId(
			String ontologyId) {
		// List of NcboUserSubscriptions its contains the All subscriptions
		// According to userId
		List<NcboUserSubscriptions> ncboUserSubscriptionsList = ncboUserSubscriptionsDAO
				.findByOntologyId(ontologyId);
	
		List<SubscriptionsBean> subscriptionsBeanList = new ArrayList<SubscriptionsBean>();

		// populate the query result to subscriptionsBean
		for (NcboUserSubscriptions ncboUserSubscriptions : ncboUserSubscriptionsList) {
			SubscriptionsBean subscriptionsBean = new SubscriptionsBean();
			subscriptionsBean.populateFromEntity(ncboUserSubscriptions);
			subscriptionsBeanList.add(subscriptionsBean);
		}

		return subscriptionsBeanList;
	}

	
}
