package org.ncbo.stanford.bean;

import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;

import org.ncbo.stanford.enumeration.NotificationTypeEnum;

/**
 * 
 * @author g.prakash
 * 
 */
public class SubscriptionsBean {

	private Integer userId;
	private String ontologyId;
	private NotificationTypeEnum notificationType;

	/**
	 * 
	 * @return
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * 
	 * @param userId
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * 
	 * @return
	 */
	public String getOntologyId() {
		return ontologyId;
	}

	/**
	 * 
	 * @param ontologyId
	 */
	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}

	/**
	 * 
	 * @return
	 */
	public NotificationTypeEnum getNotificationType() {
		return notificationType;
	}

	/**
	 * 
	 * @param notificationType
	 */

	public void setNotificationType(NotificationTypeEnum notificationType) {
		this.notificationType = notificationType;
	}

	/**
	 * 
	 * @param ncboUserSubscriptions
	 */
	public void populateToEntity(NcboUserSubscriptions ncboUserSubscriptions) {
		if (ncboUserSubscriptions != null) {

			ncboUserSubscriptions.setUserId(this.getUserId());
			ncboUserSubscriptions.setOntologyId(this.ontologyId);

		}
	}

	/**
	 * 
	 * @param ncboUserSubscriptions
	 */

	public void populateFromEntity(NcboUserSubscriptions ncboUserSubscriptions) {
		if (ncboUserSubscriptions != null) {

			this.setUserId((ncboUserSubscriptions.getUserId()));
			this.setOntologyId((ncboUserSubscriptions.getOntologyId()));
			this.setNotificationType(NotificationTypeEnum
					.valueOf(ncboUserSubscriptions.getNcboLNotificationType()
							.getType()));

		}
	}

}
