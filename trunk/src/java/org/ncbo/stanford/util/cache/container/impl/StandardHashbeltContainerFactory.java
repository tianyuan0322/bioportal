package org.ncbo.stanford.util.cache.container.impl;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;
import org.ncbo.stanford.util.cache.container.HashbeltContainerFactory;

/**
 * Factory for creating Standard hashbelt containers
 * 
 * @author Michael Dorf
 * 
 * @param <K>
 * @param <V>
 */
public class StandardHashbeltContainerFactory<K, V> implements
		HashbeltContainerFactory<K, V> {

	public HashbeltContainer<K, V> getNewContainer() {
		return new StandardHashbeltContainer<K, V>();
	}
}
