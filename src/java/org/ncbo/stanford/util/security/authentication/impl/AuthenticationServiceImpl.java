package org.ncbo.stanford.util.security.authentication.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.service.encryption.EncryptionService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.security.authentication.AuthenticationService;

public class AuthenticationServiceImpl implements AuthenticationService {
	private static final Log log = LogFactory
			.getLog(AuthenticationServiceImpl.class);
	private UserService userService;
	private EncryptionService encryptionService;

	public UserBean authenticate(String apiKey) throws AuthenticationException {
		UserBean userBean = userService.findUserByApiKey(apiKey);
		String apiKeyMsg = "Please visit " + MessageUtils.getMessage("ui.url") + "/account to get your API key.";

		if (userBean == null) {
			log
					.error("There is no user in the system corresponding to apiKey: "
							+ apiKey);
			throw new AuthenticationException(
					"Invalid credentials supplied: apiKey = " + apiKey + ". " + apiKeyMsg);
		}
		
		return userBean;
	}

	public UserBean authenticate(String username, String password)
			throws AuthenticationException {
		UserBean userBean = userService.findUser(username);

		if (userBean == null) {
			log
					.error("There is no user in the system corresponding to username: "
							+ username);
			throw new AuthenticationException();
		}

		if (!encryptionService
				.isPasswordValid(userBean.getPassword(), password)) {
			throw new AuthenticationException();
		}

		return userBean;
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
