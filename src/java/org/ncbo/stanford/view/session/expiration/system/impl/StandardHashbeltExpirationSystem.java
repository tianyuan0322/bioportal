package org.ncbo.stanford.view.session.expiration.system.impl;

import org.ncbo.stanford.view.session.container.HashbeltContainer;
import org.ncbo.stanford.view.session.container.HashbeltContainerFactory;
import org.ncbo.stanford.view.session.expiration.handler.ExpirationHandler;
import org.ncbo.stanford.view.session.expiration.system.AbstractHashbeltExpirationSystem;

/**
 * The standard implementation of get and add does not reinsert objects at the
 * front of the conveyor belt.
 * 
 * @author Michael Dorf
 */
public class StandardHashbeltExpirationSystem<K, V> extends
		AbstractHashbeltExpirationSystem<K, V> {
	public StandardHashbeltExpirationSystem() {
		super();
	}

	public StandardHashbeltExpirationSystem(int numberOfContainers,
			long rotationTime) {
		super(numberOfContainers, rotationTime);
	}

	public StandardHashbeltExpirationSystem(
			ExpirationHandler<K, V> expirationHandler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		super(expirationHandler, hashbeltContainerFactory);
	}

	public StandardHashbeltExpirationSystem(int numberOfContainers,
			long rotationTime, ExpirationHandler<K, V> expirationHandler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		super(numberOfContainers, rotationTime, expirationHandler,
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