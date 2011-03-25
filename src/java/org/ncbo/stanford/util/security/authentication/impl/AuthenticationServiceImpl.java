package org.ncbo.stanford.util.security.authentication.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.service.encryption.EncryptionService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.security.authentication.AuthenticationService;

public class AuthenticationServiceImpl implements AuthenticationService {
	private static final Log log = LogFactory
			.getLog(AuthenticationServiceImpl.class);
	private UserService userService;
	private EncryptionService encryptionService;

	public void authenticate(String apiKey) throws AuthenticationException {

	}

	public void authenticate(String username, String password)
			throws AuthenticationException {
		UserBean userBean = userService.findUser(username);

		if (userBean == null) {
			log
					.error("There is no user in the system corresponding to username: "
							+ username);
			throw new AuthenticationException("Invalid credentials supplied");
		}

	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @param encryptionService
	 *            the encryptionService to set
	 */
	public void setEncryptionService(EncryptionService encryptionService) {
		this.encryptionService = encryptionService;
	}
}
