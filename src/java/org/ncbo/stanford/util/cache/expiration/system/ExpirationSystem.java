package org.ncbo.stanford.util.cache.expiration.system;

import java.util.Collection;
import java.util.Set;

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

	public Collection<V> getValues();

	public Set<K> getKeys();
}