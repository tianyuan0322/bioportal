package org.ncbo.stanford.view.session.container.impl;

import org.ncbo.stanford.view.session.container.HashbeltContainer;
import org.ncbo.stanford.view.session.container.HashbeltContainerFactory;

/**
 * Factory for creating Fast Iterating hashbelt containers
 * 
 * @author Michael Dorf
 * 
 * @param <K>
 * @param <V>
 */
public class FastIteratingHashbeltContainerFactory<K, V> implements
		HashbeltContainerFactory<K, V> {

	public HashbeltContainer<K, V> getNewContainer() {
		return new FastIteratingHashbeltContainer<K, V>();
	}
}
