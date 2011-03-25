package org.ncbo.stanford.service.session.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.util.cache.container.HashbeltContainerFactory;
import org.ncbo.stanford.util.cache.expiration.handler.ExpirationHandler;
import org.ncbo.stanford.util.cache.expiration.system.impl.UpdatingHashbeltExpirationSystem;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * Extends UpdatingHashbeltExpirationSystem so that active sessions get their
 * expiration delayed.
 * 
 * @author Michael Dorf
 */
public class SessionServiceImpl extends
		UpdatingHashbeltExpirationSystem<String, RESTfulSession> implements
		SessionService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(SessionServiceImpl.class);

	public SessionServiceImpl() {
		super(ApplicationConstants.USER_SESSION_NAME);
	}

	/**
	 * Constructor to allow specific expiration handler and container factory
	 * 
	 * @param handler
	 * @param hashbeltContainerFactory
	 */
	public SessionServiceImpl(
			ExpirationHandler<String, RESTfulSession> handler,
			HashbeltContainerFactory<String, RESTfulSession> hashbeltContainerFactory) {
		super(ApplicationConstants.USER_SESSION_NAME, handler,
				hashbeltContainerFactory);
	}

	/**
	 * Full constructor
	 * 
	 * @param numberOfContainers
	 * @param expirationHandler
	 * @param hashbeltContainerFactory
	 */
	public SessionServiceImpl(
			int numberOfContainers,
			ExpirationHandler<String, RESTfulSession> expirationHandler,
			HashbeltContainerFactory<String, RESTfulSession> hashbeltContainerFactory) {
		super(ApplicationConstants.USER_SESSION_NAME, numberOfContainers,
				expirationHandler, hashbeltContainerFactory);
	}

	/**
	 * Allows to enter number of containers and rotation time
	 * 
	 * @param numberOfContainers
	 */
	public SessionServiceImpl(int numberOfContainers) {
		super(ApplicationConstants.USER_SESSION_NAME, numberOfContainers);
	}

	/**
	 * Creates and returns a new session instance
	 * 
	 * @param key
	 *            used to create a session id
	 * @return new session
	 */
	public RESTfulSession createNewSession(String key) {
		RESTfulSession ses = new RESTfulSession();
		String sessionId = getSessionId(key);
		ses.setId(sessionId);
		ses.setValid(true);
		put(sessionId, ses);

		return ses;
	}

	/**
	 * Returns a session based on the original unencrypted key
	 * 
	 * @param key
	 */
	@Override
	public RESTfulSession get(String key) {
		String sessionId = getSessionId(key);

		return super.get(sessionId);
	}

	/**
	 * Invalidates a session with the given key
	 * 
	 * @param key
	 */
	public void invalidate(String key) {
		String sessionId = getSessionId(key);
		super.remove(sessionId);
	}

	/**
	 * Returns a session id using a given key
	 * 
	 * @param key
	 * @return
	 */
	private String getSessionId(String key) {
		return key;
	}
}