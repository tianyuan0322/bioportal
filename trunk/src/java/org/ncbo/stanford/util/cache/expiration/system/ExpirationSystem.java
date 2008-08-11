package org.ncbo.stanford.util.cache.expiration.system;

/**
 * The basic interface for Expiration Managers
 * 
 * @author Michael Dorf
 * 
 */
public interface ExpirationSystem<K, V> {
	public void put(K key, V expirableObject);

	public void remove(K key);

	public V get(K key);

	public void clear();

	public void halt();
}