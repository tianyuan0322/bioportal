package org.ncbo.stanford.view.session.expiration.system.impl;

import org.ncbo.stanford.view.session.RESTSession;
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
		UpdatingHashbeltExpirationSystem<SessionKey, RESTSession> {

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
			ExpirationHandler<SessionKey, RESTSession> handler,
			HashbeltContainerFactory<SessionKey, RESTSession> hashbeltContainerFactory) {
		super(handler, hashbeltContainerFactory);
	}

	public SessionExpirationSystem(
			int numberOfContainers,
			long rotationTime,
			ExpirationHandler<SessionKey, RESTSession> expirationHandler,
			HashbeltContainerFactory<SessionKey, RESTSession> hashbeltContainerFactory) {
		super(numberOfContainers, rotationTime, expirationHandler,
				hashbeltContainerFactory);
	}

	public SessionExpirationSystem(int numberOfContainers, long rotationTime) {
		super(numberOfContainers, rotationTime);
	}
	
	public RESTSession getNewSession() {
		return new RESTSession();
	}
}