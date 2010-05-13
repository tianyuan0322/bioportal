/**
 * 
 */
package org.ncbo.stanford.service.notification;

/**
 * @author g.prakash
 * 
 */
public interface NotificationService {

	/**
	 * An interface designed to provide an abstraction layer to notification
	 * operations. The layer will consume this interface instead of directly
	 * calling a specific implementation.
	 */

	public void sendNotification();
}
