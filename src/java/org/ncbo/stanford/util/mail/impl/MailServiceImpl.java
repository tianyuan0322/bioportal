/**
 * 
 */
package org.ncbo.stanford.util.mail.impl;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.mail.MailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * @author g.prakash
 * 
 */
public class MailServiceImpl implements MailService {
	private static final Log log = LogFactory.getLog(MailServiceImpl.class);

	private JavaMailSender mailsender;

	/**
	 * 
	 * @param mailsender
	 */
	public void setMailsender(JavaMailSender mailsender) {
		this.mailsender = mailsender;
	}

	/**
	 * Declaring the Method For Controlling the Mail Message
	 */
	public void sendMail(String from, String email, String subject,
			String sendingMessage)
	{
		try {
			MimeMessage message = mailsender.createMimeMessage();
			MimeMessageHelper msghelper = new MimeMessageHelper(message, true);
			msghelper.setFrom(from);
			msghelper.setTo(email);
			msghelper.setSubject(subject);
			msghelper.setText(sendingMessage, true); 

			this.mailsender.send(message);
		} catch (Exception ex) {
			log.error(" Sending Mail is failed", ex);
			 
		}
	}
}
