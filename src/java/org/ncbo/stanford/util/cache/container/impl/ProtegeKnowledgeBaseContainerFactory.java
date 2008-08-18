package org.ncbo.stanford.util.cache.container.impl;

import org.ncbo.stanford.util.cache.container.HashbeltContainer;
import org.ncbo.stanford.util.cache.container.HashbeltContainerFactory;

import edu.stanford.smi.protege.model.KnowledgeBase;

/**
 * Container factory specific to Protege knowledge bases. Allows to pass any
 * base container implementation (such as FastIteratingHashbeltContainer or
 * HashlistBasedHashbeltContainer) via a factory
 * 
 * @author Michael Dorf
 * 
 */
public class ProtegeKnowledgeBaseContainerFactory implements
		HashbeltContainerFactory<Integer, KnowledgeBase> {

	private HashbeltContainerFactory<Integer, KnowledgeBase> baseFactory = null;

	public HashbeltContainer<Integer, KnowledgeBase> getNewContainer() {
		return new ProtegeKnowledgeBaseContainer(baseFactory.getNewContainer());
	}

	/**
	 * @param factory
	 *            the factory to set
	 */
	public void setBaseFactory(
			HashbeltContainerFactory<Integer, KnowledgeBase> baseFactory) {
		this.baseFactory = baseFactory;
	}
}
