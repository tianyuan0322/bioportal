package org.ncbo.stanford.aop.notification;

import org.aspectj.lang.ProceedingJoinPoint;
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
	public void setNotificationService(
			NotificationService notificationService) {
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
	public Object notify(ProceedingJoinPoint call, String ontologyVersion,
			String ontologyId) throws Throwable {
		// This sendNotification() is implemented in NotificationService
		notificationService.sendNotification();

		try {
			return call.proceed();
		} finally {

		}

	}

}
