package org.ncbo.stanford.util.security.authentication.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.service.encryption.EncryptionService;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.session.SessionService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.security.SecurityContextHolder;
import org.ncbo.stanford.util.security.authentication.AuthenticationService;

public class AuthenticationServiceImpl implements AuthenticationService {
	private static final Log log = LogFactory
			.getLog(AuthenticationServiceImpl.class);
	private UserService userService;
	private EncryptionService encryptionService;
	private SessionService sessionService;

	public RESTfulSession authenticate(String userApiKey)
			throws AuthenticationException {
		UserBean user = userService.findUserByApiKey(userApiKey);
		String apiKeyMsg = "Please visit " + MessageUtils.getMessage("ui.url")
				+ "/account to get your API key.";

		if (user == null) {
			log
					.error("There is no user in the system corresponding to apiKey: "
							+ userApiKey);
			throw new AuthenticationException(
					"Invalid credentials supplied: apiKey = " + userApiKey
							+ ". " + apiKeyMsg);
		}

		return refreshSession(user);
	}

	public RESTfulSession authenticate(String username, String password)
			throws AuthenticationException {
		UserBean user = userService.findUser(username);

		if (user == null) {
			log
					.error("There is no user in the system corresponding to username: "
							+ username);
			throw new AuthenticationException();
		}

		if (!encryptionService.isPasswordValid(user.getPassword(), password)) {
			throw new AuthenticationException();
		}

		return refreshSession(user);
	}

	public void logout(String userApiKey) {
		sessionService.invalidate(userApiKey);
	}

	private RESTfulSession refreshSession(UserBean user) {
		String userApiKey = user.getApiKey();
		sessionService.invalidate(userApiKey);
		RESTfulSession userSession = sessionService
				.createNewSession(userApiKey);
		userSession.setAttribute(ApplicationConstants.SECURITY_CONTEXT_KEY,
				new SecurityContextHolder(userApiKey, user));

		return userSession;
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

	/**
	 * @param sessionService
	 *            the sessionService to set
	 */
	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}
}
