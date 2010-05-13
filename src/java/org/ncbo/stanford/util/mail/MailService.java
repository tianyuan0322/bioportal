package org.ncbo.stanford.util.mail;

/**
 * @author g.prakash
 * 
 */
public interface MailService {
	/**
	 * /** Forwards to the equivalent MailServiceImpl method.
	 * 
	 * @see org.ncbo.stanford.util.mail.impl.MailServiceImpl#sendMail() 'from'
	 *      is used for source 'email' is used for destination 'subject'
	 *      'message' is used for message which is going with the mail
	 */
	public void sendMail(String from, String email, String subject,
			String message);
}
