package org.ncbo.stanford.view.session.expiration.system.impl;

import org.ncbo.stanford.view.session.RESTfulSession;
import org.ncbo.stanford.view.session.SessionKey;
import org.ncbo.stanford.view.session.container.HashbeltContainerFactory;
import org.ncbo.stanford.view.session.expiration.handler.ExpirationHandler;

/**
 * Extends UpdatingHashbeltExpirationSystem so that active sessions get their
 * expiration delayed.
 * 
 * @author Michael Dorf
 */
public class SessionExpirationSystem extends
		UpdatingHashbeltExpirationSystem<String, RESTfulSession> {

	// public static SessionExpirationSystem ses = null;
	//
	// public static SessionExpirationSystem getInstance(
	// int numberOfContainers,
	// long rotationTime,
	// ExpirationHandler<SessionKey, Session> expirationHandler,
	// HashbeltContainerFactory<SessionKey, Session> hashbeltContainerFactory) {
	//
	// if (ses == null) {
	// ses = new SessionExpirationSystem(numberOfContainers, rotationTime,
	// expirationHandler, hashbeltContainerFactory);
	// }
	//
	// return ses;
	// }

	public SessionExpirationSystem() {
		super();
	}

	public SessionExpirationSystem(
			ExpirationHandler<String, RESTfulSession> handler,
			HashbeltContainerFactory<String, RESTfulSession> hashbeltContainerFactory) {
		super(handler, hashbeltContainerFactory);
	}

	public SessionExpirationSystem(
			int numberOfContainers,
			long rotationTime,
			ExpirationHandler<String, RESTfulSession> expirationHandler,
			HashbeltContainerFactory<String, RESTfulSession> hashbeltContainerFactory) {
		super(numberOfContainers, rotationTime, expirationHandler,
				hashbeltContainerFactory);
	}

	public SessionExpirationSystem(int numberOfContainers, long rotationTime) {
		super(numberOfContainers, rotationTime);
	}
	
	public RESTfulSession createNewSession() {
		RESTfulSession ses = new RESTfulSession();
		String key = new SessionKey().getKey();
		ses.setId(key);
		ses.setValid(true);
		put(key, ses);
		
		return ses;
	}
}