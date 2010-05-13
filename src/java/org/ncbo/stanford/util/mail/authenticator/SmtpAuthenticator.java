package org.ncbo.stanford.util.mail.authenticator;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author g.prakash
 * 
 */
public class SmtpAuthenticator extends Authenticator {

	private String username;
	private String password;

	/**
	 * 
	 * @param username
	 * @param password
	 */
	public SmtpAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Providing the Authentication for the mail by taking the Service From The
	 * ApplicationContext-services.xml
	 */
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}
}
