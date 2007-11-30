package org.ncbo.stanford.util.cache.expiration.handler;

import java.util.Iterator;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;

/**
 * Calls timeExpired for each object in the container. At the end,
 * the objects in the container have been expired and there's no record of
 * them in the AbstractHashbeltExpirationSystem.
 * 
 * @author Michael Dorf
 */
public abstract class AbstractSimpleExpirationHandler<K, V> implements
		ExpirationHandler<K, V> {
	public void handleExpiredContainer(HashbeltContainer<K, V> expiredContainer) {
		Iterator<V> i = expiredContainer.getValues();
		while (i.hasNext()) {
			V nextValue = i.next();
			timeExpired(nextValue);
		}
	}

	protected abstract void timeExpired(V object);
}
