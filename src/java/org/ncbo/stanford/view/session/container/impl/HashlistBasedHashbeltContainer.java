package org.ncbo.stanford.view.session.container.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.ncbo.stanford.util.collections.DoublyLinkedList;
import org.ncbo.stanford.util.collections.Node;
import org.ncbo.stanford.view.session.container.HashbeltContainer;

/**
 * A slight variation on the standard hashbelt container. This maintains three
 * data structures:
 * 
 * a hashmap from keys to values a hashmap from keys to nodes a linked list (of
 * nodes)
 * 
 * The idea is that this lets us have fast updates and removals, but also fairly
 * fast iterations at the end.
 * 
 * We can't use the java.util linked list class for this because, unfortunately,
 * they don't expose the node class (and the get methods are insanely painful).
 * 
 * @author Michael Dorf
 */
public class HashlistBasedHashbeltContainer<K, V> implements
		HashbeltContainer<K, V> {
	
	private HashMap<K, V> keysToExpirableObjects = new HashMap<K, V>();
	private HashMap<K, Node<V>> keysToNodes = new HashMap<K, Node<V>>();
	private DoublyLinkedList<V> expirableObjects = new DoublyLinkedList<V>();

	public void clear() {
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

		Node<V> node = keysToNodes.get(key);
		node.remove();

		return returnValue;
	}

	public synchronized void put(K key, V value) {
		keysToExpirableObjects.put(key, value);
		Node<V> node = expirableObjects.add(value);
		keysToNodes.put(key, node);
	}

	public Iterator<V> getValues() {
		return expirableObjects.iterator();
	}

	public Iterator<K> getKeys() {
		ArrayList<K> keys = new ArrayList<K>(keysToExpirableObjects.keySet());

		return keys.iterator();
	}

	public int size() {
		return keysToExpirableObjects.size();
	}
}
