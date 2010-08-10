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

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(UpdatingHashbeltExpirationSystem.class);

	public UpdatingHashbeltExpirationSystem(String cacheName) {
		super(cacheName);
	}

	public UpdatingHashbeltExpirationSystem(String cacheName,
			int numberOfContainers) {
		super(cacheName, numberOfContainers);
	}

	public UpdatingHashbeltExpirationSystem(String cacheName,
			ExpirationHandler<K, V> handler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		super(cacheName, handler, hashbeltContainerFactory);
	}

	public UpdatingHashbeltExpirationSystem(String cacheName,
			int numberOfContainers, ExpirationHandler<K, V> expirationHandler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		super(cacheName, numberOfContainers, expirationHandler,
				hashbeltContainerFactory);
	}

	public void put(K key, V expirableObject) {
		HashbeltContainer<K, V> container = findContainer(key);

		if (null != container) {
			container.removeShallow(key);
		}

		containers[0].put(key, expirableObject);
	}

	public V get(K key) {
		HashbeltContainer<K, V> container = findContainer(key);

		if (null == container) {
			return null;
		}

		V returnValue = container.get(key);
		container.removeShallow(key);
		containers[0].put(key, returnValue);

		return returnValue;
	}
}