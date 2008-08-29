package org.ncbo.stanford.util.cache.expiration.handler;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;

/**
 * Interface to handle expired containers of a given type.
 * 
 * @author Michael Dorf
 * 
 * @param <K> -
 *            key
 * @param <V> -
 *            value
 */
public interface ExpirationHandler<K, V> {
	public void handleExpiredContainer(HashbeltContainer<K, V> expiredContainer);
}
