package org.ncbo.stanford.util.cache.expiration.system;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;

/**
 * An abstract superclass for ExpirationSystems. Holds some common code and
 * useful methods.
 * 
 * @author Michael Dorf
 */
public abstract class AbstractExpirationSystem<K, V> implements
		ExpirationSystem<K, V> {

	protected abstract HashbeltContainer<K, V> expireObjects();

	private ExpirationThread expirationThread;

	public AbstractExpirationSystem() {
		expirationThread = new ExpirationThread(this);
		expirationThread.start();
	}

	public AbstractExpirationSystem(long timeToSleep) {
		expirationThread = new ExpirationThread(timeToSleep, this);
		expirationThread.start();
	}

	public synchronized void halt() {
		clear();
		expirationThread.halt();
	}
}