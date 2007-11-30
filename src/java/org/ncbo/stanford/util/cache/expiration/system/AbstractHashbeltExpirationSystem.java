package org.ncbo.stanford.util.cache.expiration.system;

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
public abstract class AbstractHashbeltExpirationSystem<K, V> extends
		AbstractExpirationSystem<K, V> {
	protected static final int DEFAULT_NUMBER_OF_CONTAINERS = 5;
	protected static final long ONE_MINUTE = 60 * 1000;
	protected static final long DEFAULT_ROTATION_TIME = ONE_MINUTE;

	protected int numberOfContainers;
	protected HashbeltContainer<K, V>[] containers;
	protected ExpirationHandler<K, V> expirationHandler;
	protected HashbeltContainerFactory<K, V> hashbeltContainerFactory;

	public abstract void put(K key, V expirableObject);

	public abstract V get(K key);

	public AbstractHashbeltExpirationSystem() {
		this(DEFAULT_NUMBER_OF_CONTAINERS, DEFAULT_ROTATION_TIME);
	}

	public AbstractHashbeltExpirationSystem(int numberOfContainers,
			long rotationTime) {
		this(numberOfContainers, rotationTime,
				new NullExpirationHandler<K, V>(),
				new StandardHashbeltContainerFactory<K, V>());
	}

	public AbstractHashbeltExpirationSystem(
			ExpirationHandler<K, V> expirationHandler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory) {
		this(DEFAULT_NUMBER_OF_CONTAINERS, DEFAULT_ROTATION_TIME,
				expirationHandler, hashbeltContainerFactory);
	}

	public AbstractHashbeltExpirationSystem(int numContainers,
			long rotationTime, ExpirationHandler<K, V> expHandler,
			HashbeltContainerFactory<K, V> hashbeltContFactory) {
		super(rotationTime);
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

	public void remove(K key) {
		HashbeltContainer<K, V> container = findContainer(key);

		if (null != container) {
			container.remove(key);
		}
	}

	protected void expireObjects() {
		HashbeltContainer<K, V> newContainer = hashbeltContainerFactory
				.getNewContainer();
		HashbeltContainer<K, V> expiredContainer = rotateContainers(newContainer);
		expirationHandler.handleExpiredContainer(expiredContainer);
	}

	protected synchronized HashbeltContainer<K, V> findContainer(K key) {
		for (int index = 0; index < numberOfContainers; index++) {
			if (null != containers[index].get(key)) {
				return containers[index];
			}
		}

		return null;
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