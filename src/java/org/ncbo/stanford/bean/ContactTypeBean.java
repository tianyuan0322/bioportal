package org.ncbo.stanford.bean;

import java.io.Serializable;

public class ContactTypeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3012806504438328145L;
	private String email;
	private String emailType;
	private String name;
	private String organization;

	public String toString() {
		return "{Email: " + this.getEmail() + ", Name: " + this.getName() + "}";
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the emailType
	 */
	public String getEmailType() {
		return emailType;
	}

	/**
	 * @param emailType
	 *            the emailType to set
	 */
	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the organization
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * @param organization
	 *            the organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}
}