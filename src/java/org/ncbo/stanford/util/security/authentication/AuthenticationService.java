package org.ncbo.stanford.util.security.authentication;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.AuthenticationException;

public interface AuthenticationService {
	public UserBean authenticate(String apiKey) throws AuthenticationException;

	public UserBean authenticate(String username, String password)
			throws AuthenticationException;
}
