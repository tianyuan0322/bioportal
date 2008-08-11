package org.ncbo.stanford.util.cache.expiration.handler.impl;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;
import org.ncbo.stanford.util.cache.expiration.handler.ExpirationHandler;

/**
 * The simplest of all possible expiration handlers. It does nothing. But, note
 * what happens when we do nothing:
 * 
 * The AbstractHashbeltExpirationSystem loses a reference to the object. Garbage
 * collection has a chance to work.
 * 
 * The point is that, even when we do nothing in the expiration handler, system
 * state changes.
 * 
 * @author Michael Dorf
 */
public class NullExpirationHandler<K, V> implements ExpirationHandler<K, V> {
	public void handleExpiredContainer(HashbeltContainer<K, V> expiredContainer) {
	}
}
