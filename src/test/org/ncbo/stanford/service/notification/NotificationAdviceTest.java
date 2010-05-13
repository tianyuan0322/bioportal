package org.ncbo.stanford.service.notification;

/**
 * @author g.prakash
 *
 */
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.aop.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
 
public class NotificationAdviceTest extends AbstractBioPortalTest {

	@Autowired
	private Notification notificationMail;

	@Test
	public void testaround() {
		notificationMail.around("3333", "5555");

	}
}