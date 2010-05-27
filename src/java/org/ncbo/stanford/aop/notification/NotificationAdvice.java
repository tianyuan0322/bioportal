package org.ncbo.stanford.aop.notification;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.service.notification.NotificationService;

/**
 * @author g.prakash
 * 
 */

public class NotificationAdvice {
	
	private NotificationService notificationService;

	/**
	 * 
	 * @param notificationService
	 */
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	/**
	 * 
	 * @param call
	 * @param ontologyVersion
	 * @param ontologyId
	 * @return
	 * @throws Throwable
	 */
	public void adviceUpdateOntology(OntologyBean ontologyBean)
			throws Throwable {
		// This sendNotification() is implemented in NotificationService
		notificationService.sendNotification(
				NotificationTypeEnum.UPDATE_ONTOLOGY_NOTIFICATION, ontologyBean);

	}

}
