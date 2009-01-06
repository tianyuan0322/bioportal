package org.ncbo.stanford.util.cache.expiration.handler;

import java.util.Iterator;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;
import org.ncbo.stanford.util.cache.expiration.system.AbstractHashbeltExpirationSystem;

/**
 * Calls timeExpired for the object and then reinserts it in the front of the
 * expiration system.
 * 
 * Useful for alerts and announcements. E.g. if you send someone an update every
 * 15 minutes. Use this one and an object that sends the message inside its
 * "expire" method.
 * 
 * @author Michael Dorf
 * 
 * @param <K>
 * @param <V>
 */
public abstract class AbstractReinsertingExpirationHandler<K, V> implements
		ExpirationHandler<K, V> {
	private AbstractHashbeltExpirationSystem<K, V> owner;

	public AbstractReinsertingExpirationHandler(
			AbstractHashbeltExpirationSystem<K, V> ownr) {
		owner = ownr;
	}

	public void handleExpiredContainer(HashbeltContainer<K, V> expiredContainer) {
		Iterator<K> i = expiredContainer.getKeys();
		
		while (i.hasNext()) {
			K key = i.next();
			V nextValue = expiredContainer.get(key);
			timeExpired(nextValue);

			if (null != owner) {
				owner.put(key, nextValue);
			}
		}
	}

	protected abstract void timeExpired(V object);
}
