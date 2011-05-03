package org.ncbo.stanford.util.security;

import org.ncbo.stanford.bean.UserBean;

public class SecurityContextHolder {
	private String apiKey;
	private UserBean userBean;

	public SecurityContextHolder(String apiKey, UserBean userBean) {
		this.apiKey = apiKey;
		this.userBean = userBean;
	}

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @return the user
	 */
	public UserBean getUserBean() {
		return userBean;
	}
}
