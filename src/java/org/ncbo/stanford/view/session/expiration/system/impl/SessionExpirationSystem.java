package org.ncbo.stanford.view.session.expiration.system.impl;

import org.ncbo.stanford.view.session.Session;
import org.ncbo.stanford.view.session.container.HashbeltContainerFactory;
import org.ncbo.stanford.view.session.expiration.handler.ExpirationHandler;
import org.ncbo.stanford.view.session.util.SessionKey;

/**
 * Extends UpdatingHashbeltExpirationSystem so that active sessions get their
 * expiration delayed.
 * 
 * @author Michael Dorf
 */
public class SessionExpirationSystem extends
		UpdatingHashbeltExpirationSystem<SessionKey, Session> {

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
			ExpirationHandler<SessionKey, Session> handler,
			HashbeltContainerFactory<SessionKey, Session> hashbeltContainerFactory) {
		super(handler, hashbeltContainerFactory);
	}

	public SessionExpirationSystem(
			int numberOfContainers,
			long rotationTime,
			ExpirationHandler<SessionKey, Session> expirationHandler,
			HashbeltContainerFactory<SessionKey, Session> hashbeltContainerFactory) {
		super(numberOfContainers, rotationTime, expirationHandler,
				hashbeltContainerFactory);
	}

	public SessionExpirationSystem(int numberOfContainers, long rotationTime) {
		super(numberOfContainers, rotationTime);
	}
}