package org.ncbo.stanford.util.security.providers.dao.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ApplicationBean;
import org.ncbo.stanford.util.security.providers.dao.UserApplicationCache;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.providers.dao.cache.EhCacheBasedUserCache;

/**
 * Caches <code>ApplicationBean</code> objects using a Spring IoC defined <A
 * HREF="http://ehcache.sourceforge.net">EHCACHE</a>. This class extends
 * Acegi's {@link EhCacheBasedUserCache} to add application information caching.
 * 
 * @author Michael Dorf
 */
public class EhCacheBasedUserApplicationCache extends EhCacheBasedUserCache
		implements UserApplicationCache {
	// ~ Static fields/initializers
	// =====================================================================================

	private static final Log logger = LogFactory
			.getLog(EhCacheBasedUserApplicationCache.class);

	// ~ Methods
	// ========================================================================================================

	public ApplicationBean getApplicationFromCache(String applicationId) {
		Element element = null;

		try {
			element = getCache().get(applicationId);
		} catch (CacheException cacheException) {
			throw new DataRetrievalFailureException("Cache failure: "
					+ cacheException.getMessage());
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Cache hit: " + (element != null)
					+ "; applicationId: " + applicationId);
		}

		if (element == null) {
			return null;
		} else {
			return (ApplicationBean) element.getValue();
		}
	}

	public void putApplicationInCache(ApplicationBean application) {
		Element element = new Element(application.getApplicationId(),
				application);

		if (logger.isDebugEnabled()) {
			logger.debug("Cache put: " + element.getKey());
		}

		getCache().put(element);
	}

	public void removeApplicationFromCache(ApplicationBean application) {
		if (logger.isDebugEnabled()) {
			logger.debug("Cache remove: " + application.getApplicationId());
		}

		this.removeApplicationFromCache(application.getApplicationId());
	}

	public void removeApplicationFromCache(String applicationId) {
		getCache().remove(applicationId);
	}
}
