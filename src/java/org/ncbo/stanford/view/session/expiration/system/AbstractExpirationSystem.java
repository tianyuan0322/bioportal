package org.ncbo.stanford.view.session.expiration.system;


/**
 * An abstract superclass for ExpirationSystems. Holds some
 * common code and useful methods.
 * 
 * @author Michael Dorf
 */
public abstract class AbstractExpirationSystem<K, V> implements
		ExpirationSystem<K, V> {

	protected abstract void expireObjects();

	private ExpirationThread expirationThread;

	public AbstractExpirationSystem() {
		expirationThread = new ExpirationThread(this);
		expirationThread.start();
	}

	public AbstractExpirationSystem(long timeToSleep) {
		expirationThread = new ExpirationThread(timeToSleep,
				this);
		expirationThread.start();
	}

	public synchronized void halt() {
		clear();
		expirationThread.halt();
	}
}
