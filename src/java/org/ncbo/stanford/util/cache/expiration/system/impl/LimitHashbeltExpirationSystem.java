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
public class LimitHashbeltExpirationSystem<K, V> extends
		AbstractHashbeltExpirationSystem<K, V> {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(LimitHashbeltExpirationSystem.class);

	private int maxObjects;
	private int numObjects = 0;

	public LimitHashbeltExpirationSystem(int maxObjects) {
		super();
		this.maxObjects = maxObjects;
	}

	public LimitHashbeltExpirationSystem(int numberOfContainers,
			long rotationTime, int maxObjects) {
		super(numberOfContainers, rotationTime);
		this.maxObjects = maxObjects;
	}

	public LimitHashbeltExpirationSystem(ExpirationHandler<K, V> handler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory,
			int maxObjects) {
		super(handler, hashbeltContainerFactory);
		this.maxObjects = maxObjects;
	}

	public LimitHashbeltExpirationSystem(int numberOfContainers,
			long rotationTime, ExpirationHandler<K, V> expirationHandler,
			HashbeltContainerFactory<K, V> hashbeltContainerFactory,
			int maxObjects) {
		super(numberOfContainers, rotationTime, expirationHandler,
				hashbeltContainerFactory);
		this.maxObjects = maxObjects;
	}

	public void put(K key, V expirableObject) {
		HashbeltContainer<K, V> container = findContainer(key);

		if (log.isDebugEnabled()) {
			log.debug("Adding knowledgebase: " + key);
		}

		if (null != container) {
			// element already exists, just replacing old
			container.removeShallow(key);
		} else {
			// brand new element
			// need to check if numObjects exceeds maxObjects
			if (numObjects >= maxObjects) {
				V oldValue = removeLeastRecentlyUsed();

				if (log.isDebugEnabled()) {
					log.debug("NumObjects: " + numObjects + ", MaxObjects: "
							+ maxObjects
							+ ". Removing least recently used object: "
							+ oldValue);
				}

				if (log.isDebugEnabled()) {
					log.debug(getContainersAsString("Containers after LRU:"));
				}
			} else {
				numObjects++;

				if (log.isDebugEnabled()) {
					log.debug("Incremented numObjects: " + numObjects);
				}
			}
		}

		containers[0].put(key, expirableObject);
	}

	@Override
	public synchronized void clear() {
		super.clear();
		numObjects = 0;
	}

	@Override
	public void remove(K key) {
		HashbeltContainer<K, V> container = findContainer(key);

		if (null != container) {
			container.remove(key);
			numObjects--;
		}
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

	@Override
	protected HashbeltContainer<K, V> expireObjects() {
		HashbeltContainer<K, V> expiredContainer = super.expireObjects();
		numObjects -= expiredContainer.size();

		if (log.isInfoEnabled()) {
			log.info("numObjects after expire: " + numObjects);
			log.info(getContainersAsString("Containers after expire:"));
		}

		return expiredContainer;
	}

	private String getContainersAsString(String label) {
		StringBuffer sb = new StringBuffer();

		sb.append(label + " {");

		for (int i = 0; i < containers.length; i++) {
			sb.append(containers[i]);
		}

		sb.append("}\n");

		return sb.toString();
	}
}