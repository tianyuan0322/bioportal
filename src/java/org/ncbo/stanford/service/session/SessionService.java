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
	 * @param key
	 *            used to create a session id
	 * @return new session
	 */
	public RESTfulSession createNewSession(String key);

	/**
	 * Return the session using its session key
	 * 
	 * @param session
	 *            key
	 * @return
	 */
	public RESTfulSession get(String sessionKey);

	/**
	 * Invalidates a session with the given key
	 * 
	 * @param key
	 */
	public void invalidate(String key);
}