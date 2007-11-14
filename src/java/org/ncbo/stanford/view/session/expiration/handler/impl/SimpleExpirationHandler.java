package org.ncbo.stanford.view.session.expiration.handler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.view.session.expiration.handler.AbstractSimpleExpirationHandler;

/**
 * Implements AbstractSimpleExpirationHandler but for now does nothing when an
 * object expires. The current behavior is identical to NullExpirationHandler
 * 
 * @author Michael Dorf
 * 
 * @param <K>
 * @param <V>
 */
public class SimpleExpirationHandler<K, V> extends
		AbstractSimpleExpirationHandler<K, V> {
	private static final Log log = LogFactory
			.getLog(SimpleExpirationHandler.class);

	@Override
	protected void timeExpired(V object) {
		// do nothing
	}
}
