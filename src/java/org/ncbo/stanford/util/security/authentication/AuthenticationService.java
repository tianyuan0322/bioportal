package org.ncbo.stanford.util.security.authentication;

import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.service.session.RESTfulSession;

public interface AuthenticationService {
	public RESTfulSession authenticate(String userApiKey)
			throws AuthenticationException;

	public RESTfulSession authenticate(String username, String password)
			throws AuthenticationException;

	public void logout(String userApiKey);
}
