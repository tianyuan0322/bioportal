package org.ncbo.stanford.util.cache.container;

import java.util.Iterator;

/**
 * Holds some of the objects for a Hashbelt. Conceptually, this is "one bucket
 * on the conveyor belt."
 * 
 * Note: implementations of this interface need to be highly synchronized-- the
 * implementations of the hashbelt rely on this object to be threadsafe (at
 * least in the get/remove/put block. getIterator is only called once the
 * container is dead and is called from within a single thread).
 * 
 * @author Michael Dorf
 * 
 * @param <K>
 * @param <V>
 */
public interface HashbeltContainer<K, V> {
	public V get(K key);

	public V removeLeastRecentlyUsed();

	public V remove(K key);

	public V removeShallow(K key);

	public void put(K key, V value);

	public void clear();

	public Iterator<V> getValues();

	public Iterator<K> getKeys();

	public int size();
	
	public boolean isEmpty();

	public String toString();
}