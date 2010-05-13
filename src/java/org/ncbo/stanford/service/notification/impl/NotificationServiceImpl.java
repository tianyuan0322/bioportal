package org.ncbo.stanford.service.notification.impl;

import java.util.HashMap;
import java.util.Map;

import org.ncbo.stanford.manager.notification.NotificationManager;
import org.ncbo.stanford.service.notification.NotificationService;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * Implementation of NotificationService.
 * 
 * @author g.prakash
 */

public class NotificationServiceImpl implements NotificationService {

	protected Map<String, NotificationManager> notificationManagerMap = new HashMap<String, NotificationManager>();

	/**
	 * 
	 * @param notificationManagerMap
	 */
	public void setNotificationManagerMap(
			Map<String, NotificationManager> notificationManagerMap) {
		this.notificationManagerMap = notificationManagerMap;
	}

	public void sendNotification() {
		// Return the map of all notification pairs for a given constant
		notificationManagerMap.get(ApplicationConstants.EMAIL).sendNotification();

	}

}
