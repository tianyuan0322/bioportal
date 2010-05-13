package org.ncbo.stanford.manager.notification;

/**
 * An interface designed to provide an abstraction layer to notification
 * operations. The layer will consume this interface instead of directly calling
 * a specific implementation.
 * 
 * @author g.prakash
 */
public interface NotificationManager {
	
	/**
	 * 
	 * @return
	 */
	public String sendNotification();

}
