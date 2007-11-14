package org.ncbo.stanford.view.session.container.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.ncbo.stanford.view.session.container.HashbeltContainer;

/**
 * An implementation of the HashbeltContainer interface that makes three
 * assumptions about how it will be used.
 * 
 * (1) Not many removes will be called (in proportion to gets) and iteration
 * will be used fairly often. Remove is much more expensive here than in
 * StandardHashbeltContainer (because of the ArrayList).
 * 
 * (2) Keys will not be reused (using a key twice with different values
 * implicitly does an expensive remove).
 * 
 * (3) No removes or adds will be called while an iterator is being used (this
 * class does not attempt to prevent concurrent modification exceptions).
 * 
 * @author Michael Dorf
 */
public class FastIteratingHashbeltContainer<K, V> implements
		HashbeltContainer<K, V> {
	private HashMap<K, V> keysToExpirableObjects = new HashMap<K, V>();
	private ArrayList<V> values = new ArrayList<V>();
	private ArrayList<K> keys = new ArrayList<K>();

	public void clear() {
		keysToExpirableObjects.clear();
		values.clear();
	}

	public synchronized V get(K key) {
		return keysToExpirableObjects.get(key);
	}

	public synchronized V remove(K key) {
		V returnValue = keysToExpirableObjects.remove(key);

		if (null != returnValue) {
			values.remove(returnValue);
			keys.remove(key);
		}

		return returnValue;
	}

	public synchronized void put(K key, V value) {
		V currentValue = keysToExpirableObjects.get(key);

		if (null != currentValue) {
			if (currentValue.equals(value)) {
				return;
			}
			values.remove(currentValue);
		}

		keysToExpirableObjects.put(key, value);
		values.add(value);
		keys.add(key);
	}

	public Iterator<V> getValues() {
		return values.iterator();
	}

	public Iterator<K> getKeys() {
		return keys.iterator();
	}

	public int size() {
		return values.size();
	}
}