package org.ncbo.stanford.util.cache.expiration.system.impl;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;
import org.ncbo.stanford.util.cache.container.HashbeltContainerFactory;
import org.ncbo.stanford.util.cache.expiration.handler.ExpirationHandler;
import org.ncbo.stanford.util.cache.expiration.system.AbstractHashbeltExpirationSystem;

/**
 * The standard implementation of get and add does not reinsert objects at the
 * front of the conveyor belt.
 * 
 * @author Michael Dorf
 */
public class StandardHashbeltExpirationSystem<K, V> extends
		AbstractHashbeltExpirationSystem<K, V> {
	public StandardHashbeltExpirationSystem(String cacheName) {
		super(cacheName);
	}

	public StandardHashbeltExpirationSystem(String cacheName,
			int numberOfContainers) {
		super(cacheName, numberOfContainers);
	}

	public StandardHashbeltExpirationSystem(String cacheName,
			ExpirationHandler<K, V> expirationHandler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		super(cacheName, expirationHandler, hashbeltContainerFactory);
	}

	public StandardHashbeltExpirationSystem(String cacheName,
			int numberOfContainers, ExpirationHandler<K, V> expirationHandler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		super(cacheName, numberOfContainers, expirationHandler,
				hashbeltContainerFactory);
	}

	public void put(K key, V expirableObject) {
		HashbeltContainer<K, V> container = findContainer(key);

		if (null == container) {
			containers[0].put(key, expirableObject);
		}
	}

	public V get(K key) {
		return findObjectForKey(key);
	}
}