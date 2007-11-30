package org.ncbo.stanford.service.session;



/**
 * Service interface for handling RESTful sessions
 * 
 * @author Michael Dorf
 */
public interface SessionService {
	
	/**
	 * Creates and returns a new session instance
	 * 
	 * @return new session
	 */
	public RESTfulSession createNewSession();

	/**
	 * Return the session using its session key
	 * 
	 * @param session key
	 * @return
	 */
	public RESTfulSession get(String sessionKey);
}