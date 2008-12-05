package org.ncbo.stanford.util.cache.container.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;

/**
 * A slight variation on the standard hashbelt container. This maintains two
 * data structures:
 * 
 * a hashmap from keys to values and a linked list (of nodes)
 * 
 * The idea is that this lets us have fast updates and removals, but also fairly
 * fast iterations at the end.
 * 
 * @author Michael Dorf
 */
public class HashlistBasedHashbeltContainer<K, V> implements
		HashbeltContainer<K, V> {

	private HashMap<K, V> keysToExpirableObjects = new HashMap<K, V>();
	private LinkedList<V> expirableObjects = new LinkedList<V>();

	public synchronized void clear() {
		keysToExpirableObjects.clear();
	}

	public synchronized V get(K key) {
		return keysToExpirableObjects.get(key);
	}

	public synchronized V remove(K key) {
		V returnValue = keysToExpirableObjects.remove(key);

		if (null == returnValue) {
			return null;
		}

		expirableObjects.remove(returnValue);

		return returnValue;
	}

	public synchronized V removeShallow(K key) {
		return remove(key);
	}

	public synchronized void put(K key, V value) {
		keysToExpirableObjects.put(key, value);
		expirableObjects.add(value);
	}

	public synchronized Iterator<V> getValues() {
		return expirableObjects.iterator();
	}

	public synchronized Iterator<K> getKeys() {
		ArrayList<K> keys = new ArrayList<K>(keysToExpirableObjects.keySet());

		return keys.iterator();
	}

	public int size() {
		return keysToExpirableObjects.size();
	}
}