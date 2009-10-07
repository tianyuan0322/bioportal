package org.ncbo.stanford.util.cache.container.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;

/**
 * A basic implementation of the HashbeltContainer interface. It uses a HashMap
 * to store key/value pairs. This is optimized for lots of gets with a fair
 * number of removes. It there aren't going to be many removes, it's better to
 * use FastIteratingHashbeltContainer (which also has a vector of instances to
 * speed up iteration).
 * 
 * @author Michael Dorf
 */
public class StandardHashbeltContainer<K, V> implements HashbeltContainer<K, V> {
	private HashMap<K, V> keysToExpirableObjects = new HashMap<K, V>();

	public synchronized void clear() {
		keysToExpirableObjects.clear();
	}

	public synchronized V get(K key) {
		return keysToExpirableObjects.get(key);
	}

	public synchronized V removeLeastRecentlyUsed() {
		V returnValue = null;

		if (!keysToExpirableObjects.isEmpty()) {
			ArrayList<K> keyList = new ArrayList<K>(keysToExpirableObjects
					.keySet());
			returnValue = remove(keyList.get(0));
		}

		return returnValue;
	}

	public synchronized V remove(K key) {
		return keysToExpirableObjects.remove(key);
	}

	public synchronized V removeShallow(K key) {
		return remove(key);
	}

	public synchronized void put(K key, V value) {
		keysToExpirableObjects.put(key, value);
	}

	public synchronized Iterator<V> getValues() {
		ArrayList<V> values = new ArrayList<V>(keysToExpirableObjects.values());
		return values.iterator();
	}

	public synchronized Iterator<K> getKeys() {
		ArrayList<K> keys = new ArrayList<K>(keysToExpirableObjects.keySet());

		return keys.iterator();
	}

	public int size() {
		return keysToExpirableObjects.size();
	}

	public boolean isEmpty() {
		return keysToExpirableObjects.isEmpty();
	}

	@Override
	public String toString() {
		return keysToExpirableObjects.keySet().toString();
	}
}