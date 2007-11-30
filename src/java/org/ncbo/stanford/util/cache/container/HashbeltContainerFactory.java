package org.ncbo.stanford.util.cache.container;

/**
 * Interface for container creation factory
 * 
 * @author Michael Dorf
 * 
 * @param <K>
 * @param <V>
 */
public interface HashbeltContainerFactory<K, V> {
	public HashbeltContainer<K, V> getNewContainer();
}
