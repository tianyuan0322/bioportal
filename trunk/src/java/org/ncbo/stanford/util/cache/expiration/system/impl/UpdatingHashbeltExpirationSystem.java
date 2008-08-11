package org.ncbo.stanford.util.cache.expiration.system.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.cache.container.HashbeltContainer;
import org.ncbo.stanford.util.cache.container.HashbeltContainerFactory;
import org.ncbo.stanford.util.cache.expiration.handler.ExpirationHandler;
import org.ncbo.stanford.util.cache.expiration.system.AbstractHashbeltExpirationSystem;

/**
 * A subclass of AbstractHashbeltExpirationSystem that moves requested elements
 * back into the first container when a get or add occurs. Suitable for
 * information that is expired when it is not used for a while (cached data,
 * session keys, etc.).
 * 
 * @author Michael Dorf
 */
public class UpdatingHashbeltExpirationSystem<K, V> extends
		AbstractHashbeltExpirationSystem<K, V> {

	private static final Log log = LogFactory
			.getLog(UpdatingHashbeltExpirationSystem.class);

	public UpdatingHashbeltExpirationSystem() {
		super();
	}

	public UpdatingHashbeltExpirationSystem(int numberOfContainers,
			long rotationTime) {
		super(numberOfContainers, rotationTime);
	}

	public UpdatingHashbeltExpirationSystem(ExpirationHandler<K, V> handler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		super(handler, hashbeltContainerFactory);
	}

	public UpdatingHashbeltExpirationSystem(int numberOfContainers,
			long rotationTime, ExpirationHandler<K, V> expirationHandler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		super(numberOfContainers, rotationTime, expirationHandler,
				hashbeltContainerFactory);
	}

	public void put(K key, V expirableObject) {
		HashbeltContainer<K, V> container = findContainer(key);

		if (null != container) {
			container.remove(key);
		}

		containers[0].put(key, expirableObject);
	}

	public V get(K key) {
		HashbeltContainer<K, V> container = findContainer(key);

		if (null == container) {
			return null;
		}

		V returnValue = container.get(key);
		container.remove(key);
		containers[0].put(key, returnValue);

		return returnValue;
	}
}