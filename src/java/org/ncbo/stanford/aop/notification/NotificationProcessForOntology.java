/**
 * 
 */
package org.ncbo.stanford.aop.notification;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;

/**
 * @author g.prakash
 * 
 */
public class NotificationProcessForOntology {

	/**
	 * This method provides the support for EmailNotification when the
	 * processRecord method is called
	 * 
	 * @param loadQueue
	 * @param formatHandler
	 * @param ontologyBean
	 */
	public void processForOntologySubmitted(NcboOntologyLoadQueue loadQueue,
			String formatHandler, OntologyBean ontologyBean) {
		try {
			// This Method is only for AOP, when this method is called by
			// processRecord() method in
			// OntologyLoadSchedularServiceImpl.java, then defined pointcut is executed.
			
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
