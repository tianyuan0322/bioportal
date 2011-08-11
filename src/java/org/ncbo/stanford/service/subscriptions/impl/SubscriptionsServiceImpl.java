package org.ncbo.stanford.service.subscriptions.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.SubscriptionsBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboAppTextDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboLNotificationTypeDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserSubscriptionsDAO;
import org.ncbo.stanford.domain.generated.NcboLNotificationType;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;
import org.ncbo.stanford.exception.NotificationNotFoundException;
import org.ncbo.stanford.service.subscriptions.SubscriptionsService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.mail.impl.MailServiceImpl;
import org.ncbo.stanford.util.textmanager.service.TextManager;

/**
 * @author g.prakash
 *
 */

public class SubscriptionsServiceImpl implements SubscriptionsService {
	private static final Log log = LogFactory
			.getLog(SubscriptionsServiceImpl.class);

	private CustomNcboUserSubscriptionsDAO ncboUserSubscriptionsDAO;

	private CustomNcboLNotificationTypeDAO ncboLNotificationTypeDAO;

	private MailServiceImpl mailService;
	private NcboUserDAO ncboUserDAO;
	private TextManager textManager;
	private CustomNcboAppTextDAO textDAO;

	/**
	 * This method creates the Subscriptions According to SubscriptionsBean
	 *
	 * @param subscriptionsBean
	 *
	 */

	public void createSubscriptions(SubscriptionsBean subscriptionsBean) {
		List<NcboUserSubscriptions> existingSubs = new ArrayList<NcboUserSubscriptions>();
		List<String> ontologyIds = subscriptionsBean.getOntologyIds();
		List<NcboLNotificationType> listOfNotificationType = new ArrayList<NcboLNotificationType>();

		// Populating the NcboUserSubscriptions from SubscriptonsBean
		try {
			listOfNotificationType = ncboLNotificationTypeDAO
					.findByType(subscriptionsBean.getNotificationType()
							.toString());

			for (NcboLNotificationType notificationType : listOfNotificationType) {
				for (String ontologyId : ontologyIds) {
					existingSubs = ncboUserSubscriptionsDAO
							.findByUserIdAndOntologyId(ontologyId,
									subscriptionsBean.getUserId(),
									notificationType);

					if (existingSubs.isEmpty()) {
						NcboUserSubscriptions ncboUserSubscriptions = new NcboUserSubscriptions();
						subscriptionsBean
								.populateToEntity(ncboUserSubscriptions);
						ncboUserSubscriptions
								.setNcboLNotificationType(notificationType);
						ncboUserSubscriptions.setOntologyId(ontologyId);

						// We don't want to add multiple of the same
						// ontology/type for the user, so we only save if
						// existingSubs is empty
						ncboUserSubscriptionsDAO.save(ncboUserSubscriptions);
					}
				}
			}
		} catch (Exception ex) {
			log.error("Creating subscription failed", ex);
			ex.printStackTrace();
		}
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
		List<NcboUserSubscriptions> existingSubs = new ArrayList<NcboUserSubscriptions>();
		List<NcboLNotificationType> ncboLNotificationtype = new ArrayList<NcboLNotificationType>();
		List<String> ontologyIds = subscriptionsBean.getOntologyIds();

		try {
			ncboLNotificationtype = ncboLNotificationTypeDAO
					.findByType(subscriptionsBean.getNotificationType()
							.toString());

			for (NcboLNotificationType notificationType : ncboLNotificationtype) {
				for (String ontologyId : ontologyIds) {
					existingSubs = ncboUserSubscriptionsDAO
							.findByUserIdAndOntologyId(ontologyId,
									subscriptionsBean.getUserId(),
									notificationType);

					if (existingSubs.isEmpty()) {
						NcboUserSubscriptions ncboUserSubscriptions = new NcboUserSubscriptions();
						subscriptionsBean
								.populateToEntity(ncboUserSubscriptions);
						ncboUserSubscriptions
								.setNcboLNotificationType(notificationType);
						ncboUserSubscriptions.setOntologyId(ontologyId);

						// We don't want to add multiple of the same
						// ontology/type for the user, so we only save if
						// existingSubs is empty
						ncboUserSubscriptionsDAO.save(ncboUserSubscriptions);
					}
				}
			}
		} catch (Exception ex) {
			log.error("Problem updating subscription", ex);
			ex.printStackTrace();
		}
	}

	/**
	 * @param subscriptionsBean
	 *
	 */
	public void removeSubscriptions(SubscriptionsBean subscriptionsBean) {
		List<NcboUserSubscriptions> listOfSubscriptions = new ArrayList<NcboUserSubscriptions>();
		List<String> ontologyIds = subscriptionsBean.getOntologyIds();

		for (String ontologyId : ontologyIds) {
			listOfSubscriptions = ncboUserSubscriptionsDAO
					.findByOntologyIdAndNotificationType(ontologyId,
							subscriptionsBean.getNotificationType());

			for (NcboUserSubscriptions subscriptions : listOfSubscriptions) {
				ncboUserSubscriptionsDAO.delete(subscriptions);
			}
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

}
