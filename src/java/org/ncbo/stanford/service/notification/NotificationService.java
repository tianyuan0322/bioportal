/**
 * 
 */
package org.ncbo.stanford.service.notification;

import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.bean.OntologyBean;

/**
 * @author g.prakash
 * 
 */
public interface NotificationService {

	/**
	 *  
	 * An interface designed to provide an abstraction layer to notification
	 * operations. The layer will consume this interface instead of directly
	 * calling a specific implementation.
	 *  
	 * @param notificationType
	 * @param ontology_id
	 */
	public void sendNotification(
			NotificationTypeEnum notificationType, OntologyBean ontologyBean);
}
