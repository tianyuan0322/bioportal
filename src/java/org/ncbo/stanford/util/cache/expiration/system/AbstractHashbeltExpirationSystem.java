package org.ncbo.stanford.util.cache.expiration.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.cache.container.HashbeltContainer;
import org.ncbo.stanford.util.cache.container.HashbeltContainerFactory;
import org.ncbo.stanford.util.cache.container.impl.StandardHashbeltContainerFactory;
import org.ncbo.stanford.util.cache.expiration.handler.ExpirationHandler;
import org.ncbo.stanford.util.cache.expiration.handler.impl.NullExpirationHandler;

/**
 * Abstract superclass that handles most of the conveyor belt details. The
 * subclasses (Standard and Updating) implement add and get functionality to
 * specify whether "reinserting" is the right behavior.
 * 
 * @author Michael Dorf
 */
@SuppressWarnings("unchecked")
public abstract class AbstractHashbeltExpirationSystem<K, V> implements
		ExpirationSystem<K, V> {
	private static final Log log = LogFactory
			.getLog(AbstractHashbeltExpirationSystem.class);

	protected static final int DEFAULT_NUMBER_OF_CONTAINERS = 5;
	protected static final long ONE_MINUTE = 60 * 1000;

	protected int numberOfContainers;
	protected HashbeltContainer<K, V>[] containers;
	protected ExpirationHandler<K, V> expirationHandler;
	protected HashbeltContainerFactory<K, V> hashbeltContainerFactory;
	protected String cacheName;

	public abstract void put(K key, V expirableObject);

	public abstract V get(K key);

	public AbstractHashbeltExpirationSystem(String cacheName) {
		this(cacheName, DEFAULT_NUMBER_OF_CONTAINERS);
	}

	public AbstractHashbeltExpirationSystem(String cacheName,
			int numberOfContainers) {
		this(cacheName, numberOfContainers, new NullExpirationHandler<K, V>(),
				new StandardHashbeltContainerFactory<K, V>());
	}

	public AbstractHashbeltExpirationSystem(String cacheName,
			ExpirationHandler<K, V> expirationHandler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		this(cacheName, DEFAULT_NUMBER_OF_CONTAINERS, expirationHandler,
				hashbeltContainerFactory);
	}

	public AbstractHashbeltExpirationSystem(String cacheName,
			int numContainers, ExpirationHandler<K, V> expHandler,
			HashbeltContainerFactory<K, V> hashbeltContFactory) {
		this.cacheName = cacheName;
		numberOfContainers = numContainers;
		expirationHandler = expHandler;
		hashbeltContainerFactory = hashbeltContFactory;
		containers = new HashbeltContainer[numberOfContainers];

		for (int i = 0; i < numberOfContainers; i++) {
			containers[i] = hashbeltContainerFactory.getNewContainer();
		}
	}

	public synchronized void clear() {
		for (int index = 0; index < numberOfContainers; index++) {
			containers[index].clear();
		}
	}

	public Set<K> getKeys() {
		Set<K> keys = new HashSet<K>(0);

		for (int index = 0; index < numberOfContainers; index++) {
			Iterator<K> keysIter = containers[index].getKeys();

			while (keysIter.hasNext()) {
				K key = keysIter.next();

				if (key != null) {
					keys.add(key);
				}
			}
		}

		return keys;
	}

	public Collection<V> getValues() {
		Collection<V> values = new ArrayList<V>(0);

		for (int index = 0; index < numberOfContainers; index++) {
			Iterator<V> valuesIter = containers[index].getValues();

			while (valuesIter.hasNext()) {
				V value = valuesIter.next();

				if (value != null) {
					values.add(value);
				}
			}
		}

		return values;
	}

	public void remove(K key) {
		HashbeltContainer<K, V> container = findContainer(key);

		if (null != container) {
			container.remove(key);
		}
	}

	public HashbeltContainer<K, V> expireObjects() {
		if (log.isInfoEnabled()) {
			log.info("Running " + cacheName + " expiration job...");
		}

		HashbeltContainer<K, V> newContainer = hashbeltContainerFactory
				.getNewContainer();
		HashbeltContainer<K, V> expiredContainer = rotateContainers(newContainer);
		expirationHandler.handleExpiredContainer(expiredContainer);

		return expiredContainer;
	}

	protected synchronized HashbeltContainer<K, V> findContainer(K key) {
		for (int index = 0; index < numberOfContainers; index++) {
			if (null != containers[index].get(key)) {
				return containers[index];
			}
		}

		return null;
	}

	protected V removeLeastRecentlyUsed() {
		V returnValue = null;

		for (int index = numberOfContainers - 1; index >= 0; index--) {
			if (!containers[index].isEmpty()) {
				returnValue = containers[index].removeLeastRecentlyUsed();
				break;
			}
		}

		return returnValue;
	}

	protected synchronized V findObjectForKey(K key) {
		V returnValue;

		for (int index = 0; index < numberOfContainers; index++) {
			returnValue = containers[index].get(key);

			if (null != returnValue) {
				return returnValue;
			}
		}

		return null;
	}

	private synchronized HashbeltContainer<K, V> rotateContainers(
			HashbeltContainer<K, V> newContainer) {
		/*
		 * This doesn't do a single, in-place, array copy because we can't
		 * guarantee how the JDK implements the array copy (e.g. whether it'll
		 * do the in-place copy correctly). It's much safer to use a second
		 * array.
		 */
		HashbeltContainer<K, V> returnValue = containers[numberOfContainers - 1];
		HashbeltContainer<K, V>[] newContainers = new HashbeltContainer[numberOfContainers];
		System.arraycopy(containers, 0, newContainers, 1,
				numberOfContainers - 1);
		containers = newContainers;
		containers[0] = newContainer;

		return returnValue;
	}
}