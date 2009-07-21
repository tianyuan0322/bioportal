package org.ncbo.stanford.bean;

public class OBORepositoryInfoHolder {
	String username;
	String password;
	String hostname;
	String module;
	String rootdirectory;
	String argumentstring;
	String checkoutdir;
	String descriptorlocation;

	public String toString() {
		return "username: " + username + ", password: " + password
				+ ", hostname: " + hostname + ", module: " + module
				+ ", rootdirectory: " + rootdirectory + ", argumentstring: "
				+ argumentstring + ", checkoutdir: " + checkoutdir
				+ ", descriptorlocation: " + descriptorlocation;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname
	 *            the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the module
	 */
	public String getModule() {
		return module;
	}

	/**
	 * @param module
	 *            the module to set
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * @return the rootdirectory
	 */
	public String getRootdirectory() {
		return rootdirectory;
	}

	/**
	 * @param rootdirectory
	 *            the rootdirectory to set
	 */
	public void setRootdirectory(String rootdirectory) {
		this.rootdirectory = rootdirectory;
	}

	/**
	 * @return the argumentstring
	 */
	public String getArgumentstring() {
		return argumentstring;
	}

	/**
	 * @param argumentstring
	 *            the argumentstring to set
	 */
	public void setArgumentstring(String argumentstring) {
		this.argumentstring = argumentstring;
	}

	/**
	 * @return the checkoutdir
	 */
	public String getCheckoutdir() {
		return checkoutdir;
	}

	/**
	 * @param checkoutdir
	 *            the checkoutdir to set
	 */
	public void setCheckoutdir(String checkoutdir) {
		this.checkoutdir = checkoutdir;
	}

	/**
	 * @return the descriptorlocation
	 */
	public String getDescriptorlocation() {
		return descriptorlocation;
	}

	/**
	 * @param descriptorlocation
	 *            the descriptorlocation to set
	 */
	public void setDescriptorlocation(String descriptorlocation) {
		this.descriptorlocation = descriptorlocation;
	}
}
