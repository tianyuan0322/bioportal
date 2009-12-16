package org.ncbo.stanford.util.security.providers.dao.cache;

import org.ncbo.stanford.bean.ApplicationBean;
import org.ncbo.stanford.util.security.providers.dao.UserApplicationCache;
import org.springframework.security.userdetails.UserDetails;

/**
 * Does not perform any caching.
 * 
 * @author Michael Dorf
 */
public class NullUserApplicationCache implements UserApplicationCache {
	// ~ Methods
	// ========================================================================================================

	public UserDetails getUserFromCache(String username) {
		return null;
	}

	public void putUserInCache(UserDetails user) {
	}

	public void removeUserFromCache(String username) {
	}

	public ApplicationBean getApplicationFromCache(String applicationId) {
		return null;
	}

	public void putApplicationInCache(ApplicationBean application) {
	}

	public void removeApplicationFromCache(String applicationId) {
	}
}
