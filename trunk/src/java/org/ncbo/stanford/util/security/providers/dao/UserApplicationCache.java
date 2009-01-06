package org.ncbo.stanford.util.security.providers.dao;

import org.acegisecurity.providers.dao.UserCache;
import org.ncbo.stanford.bean.ApplicationBean;

/**
 * Provides a cache of {@link ApplicationBean} objects.
 * 
 * <P>
 * Implementations should provide appropriate methods to set their cache
 * parameters (eg time-to-live) and/or force removal of entities before their
 * normal expiration. These are not part of the <code>UserCache</code>
 * interface contract because they vary depending on the type of caching system
 * used (eg in-memory vs disk vs cluster vs hybrid).
 * </p>
 * 
 * @author Michael Dorf
 */
public interface UserApplicationCache extends UserCache {
	// ~ Methods
	// ========================================================================================================

	/**
	 * Obtains a {@link ApplicationBean} from the cache.
	 * 
	 * @param applicationId
	 * 
	 * @return the populated <code>ApplicationBean</code> or <code>null</code>
	 *         if the application could not be found or if the cache entry has
	 *         expired
	 */
	ApplicationBean getApplicationFromCache(String applicationId);

	/**
	 * Places a {@link ApplicationBean} in the cache. The
	 * <code>applicationId</code> is the key used to subsequently retrieve the
	 * <code>ApplicationBean</code>.
	 * 
	 * @param application
	 *            the fully populated <code>ApplicationBean</code> to place in
	 *            the cache
	 */
	void putApplicationInCache(ApplicationBean application);

	/**
	 * Removes the specified application from the cache. The
	 * <code>applicationId</code> is the key used to remove the user. If the
	 * application is not found, the method should simply return (not thrown an
	 * exception).
	 * <P>
	 * Some cache implementations may not support eviction from the cache, in
	 * which case they should provide appropriate behavior to alter the
	 * application in either its documentation, via an exception, or through a
	 * log message.
	 * </p>
	 * 
	 * @param applicationId
	 *            to be evicted from the cache
	 */
	void removeApplicationFromCache(String applicationId);
}
