package org.ncbo.stanford.util.cache;

import edu.stanford.smi.protege.model.KnowledgeBase;

/**
 * A Protege specific CacheMap implementation for storing knowledge bases
 * 
 * @author Michael Dorf
 * 
 */
public class ProtegeKnowldedgeBaseCacheMap extends
		CacheMap<Integer, KnowledgeBase> {

	/**
	 * 
	 */
	public ProtegeKnowldedgeBaseCacheMap() {
		super();
	}

	/**
	 * @param timeout
	 */
	public ProtegeKnowldedgeBaseCacheMap(long timeout) {
		super(timeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.util.cache.CacheMap#dispose(java.lang.Object)
	 */
	@Override
	protected void dispose(KnowledgeBase val) {
		if (val != null) {
			val.getProject().dispose();
		}

		super.dispose(val);
	}
}