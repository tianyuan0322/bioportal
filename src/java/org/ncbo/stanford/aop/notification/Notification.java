package org.ncbo.stanford.aop.notification;

/**
 * @author g.prakash
 * 
 */
public class Notification {

	/**
	 * 
	 * @param ontologyVersion
	 * @param ontologyId
	 * @return message
	 */
	public String around(String ontologyVersion, String ontologyId) {
		String message = ontologyVersion + ontologyId;
		return message;
	}

}
