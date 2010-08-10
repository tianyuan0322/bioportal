package org.ncbo.stanford.service.session.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.util.cache.GloballyUniqueKey;
import org.ncbo.stanford.util.cache.container.HashbeltContainerFactory;
import org.ncbo.stanford.util.cache.expiration.handler.ExpirationHandler;
import org.ncbo.stanford.util.cache.expiration.system.impl.UpdatingHashbeltExpirationSystem;

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
	private static final String CACHE_NAME = "UserSession";

	public SessionServiceImpl() {
		super(CACHE_NAME);
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
		super(CACHE_NAME, handler, hashbeltContainerFactory);
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
		super(CACHE_NAME, numberOfContainers, expirationHandler,
				hashbeltContainerFactory);
	}

	/**
	 * Allows to enter number of containers and rotation time
	 * 
	 * @param numberOfContainers
	 */
	public SessionServiceImpl(int numberOfContainers) {
		super(CACHE_NAME, numberOfContainers);
	}

	/**
	 * Creates and returns a new session instance
	 * 
	 * @return new session
	 */
	public RESTfulSession createNewSession() {
		RESTfulSession ses = new RESTfulSession();
		String key = new GloballyUniqueKey().getKey();
		ses.setId(key);
		ses.setValid(true);
		put(key, ses);

		return ses;
	}
}