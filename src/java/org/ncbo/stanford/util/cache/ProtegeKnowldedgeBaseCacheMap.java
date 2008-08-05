package org.ncbo.stanford.util.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.stanford.smi.protege.model.KnowledgeBase;

/**
 * A Protege specific CacheMap implementation for storing knowledge bases
 * 
 * @author Michael Dorf
 * 
 */
public class ProtegeKnowldedgeBaseCacheMap extends
		CacheMap<Integer, KnowledgeBase> {

	private static final Log log = LogFactory.getLog(ProtegeKnowldedgeBaseCacheMap.class);
	
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
			log.debug("Disposing of the knowledgebase: " + val.getName());
			val.getProject().dispose();
		}

		super.dispose(val);
	}
}