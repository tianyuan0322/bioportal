/**
 *
 */
package org.ncbo.stanford.util.mail.impl;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.mail.MailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.mysql.jdbc.StringUtils;

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
			String messageId, String inReplyTo, String sendingMessage) {
		try {
			// This allows an email address to be set in the build.properties
			// file. This email will be used instead of the original recipient.
			String overrideReceiver = MessageUtils
					.getMessage("notification.mail.override");
			if (!StringUtils.isNullOrEmpty(overrideReceiver)) {
				email = overrideReceiver;
			}

			MimeMessage message = mailsender.createMimeMessage();
			MimeMessageHelper msghelper = new MimeMessageHelper(message, true);
			msghelper.setFrom(from);
			msghelper.setTo(email);
			msghelper.setSubject(subject);
			msghelper.setText(sendingMessage, true);

			// Set extra information if available
			if (messageId != null) {
				message.setHeader("Message-ID", messageId);
			}

			if (inReplyTo != null) {
				message.setHeader("In-Reply-To", inReplyTo);
			}

			this.mailsender.send(message);
		} catch (Exception ex) {
			log.error("Sending mail failed", ex);

		}
	}
}
