package org.ncbo.stanford.util.cache.expiration.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.cache.expiration.handler.AbstractSimpleExpirationHandler;

import edu.stanford.smi.protege.model.KnowledgeBase;

public class ProtegeKnowledgeBaseExpirationHandler extends
		AbstractSimpleExpirationHandler<Integer, KnowledgeBase> {

	private static final Log log = LogFactory
			.getLog(ProtegeKnowledgeBaseExpirationHandler.class);

	@Override
	protected void timeExpired(KnowledgeBase object) {
		if (object != null) {
			log.debug("Disposing of the knowledgebase: " + object.getName());
			object.getProject().dispose();
		}
	}
}
