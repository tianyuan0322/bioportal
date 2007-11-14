package org.ncbo.stanford.view.session.expiration.system.impl;

import org.ncbo.stanford.view.session.container.HashbeltContainer;
import org.ncbo.stanford.view.session.container.HashbeltContainerFactory;
import org.ncbo.stanford.view.session.expiration.handler.ExpirationHandler;
import org.ncbo.stanford.view.session.expiration.system.AbstractHashbeltExpirationSystem;

/**
 * A subclass of AbstractHashbeltExpirationSystem that moves requested
 * elements back into the first container when a get or add occurs.
 * Suitable for information that is expired when it is not used for a
 * while (cached data, session keys, etc.).
 * 
 * @author Michael Dorf
 */
public class UpdatingHashbeltExpirationSystem<K, V> extends
		AbstractHashbeltExpirationSystem<K, V> {
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