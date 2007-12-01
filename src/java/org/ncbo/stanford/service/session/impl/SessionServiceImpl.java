package org.ncbo.stanford.service.session.impl;

import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionKey;
import org.ncbo.stanford.service.session.SessionService;
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

	public SessionServiceImpl() {
		super();
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
		super(handler, hashbeltContainerFactory);
	}

	/**
	 * Full constructor
	 * 
	 * @param numberOfContainers
	 * @param rotationTime
	 * @param expirationHandler
	 * @param hashbeltContainerFactory
	 */
	public SessionServiceImpl(
			int numberOfContainers,
			long rotationTime,
			ExpirationHandler<String, RESTfulSession> expirationHandler,
			HashbeltContainerFactory<String, RESTfulSession> hashbeltContainerFactory) {
		super(numberOfContainers, rotationTime, expirationHandler,
				hashbeltContainerFactory);
	}

	/**
	 * Allows to enter number of containers and rotation time
	 * 
	 * @param numberOfContainers
	 * @param rotationTime
	 */
	public SessionServiceImpl(int numberOfContainers, long rotationTime) {
		super(numberOfContainers, rotationTime);
	}

	/**
	 * Creates and returns a new session instance
	 * 
	 * @return new session
	 */
	public RESTfulSession createNewSession() {
		RESTfulSession ses = new RESTfulSession();
		String key = new SessionKey().getKey();
		ses.setId(key);
		ses.setValid(true);
		put(key, ses);

		return ses;
	}
}