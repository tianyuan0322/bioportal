package org.ncbo.stanford.util.security;

import org.ncbo.stanford.bean.UserBean;

public class SecurityContextHolder {
	private String apiKey;
	private String appApiKey;
	private UserBean userBean;

	public SecurityContextHolder(String apiKey, String appApiKey,
			UserBean userBean) {
		super();
		this.apiKey = apiKey;
		this.appApiKey = appApiKey;
		this.userBean = userBean;
	}

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @return the appApiKey
	 */
	public String getAppApiKey() {
		return appApiKey;
	}

	/**
	 * @return the user
	 */
	public UserBean getUserBean() {
		return userBean;
	}
}
