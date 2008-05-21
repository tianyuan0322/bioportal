package org.ncbo.stanford;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Contains setup and configuration functionality common to all BioPortal tests
 * 
 * @author Michael Dorf
 * 
 */
public class AbstractBioPortalTest extends
		AbstractDependencyInjectionSpringContextTests {

	private SessionFactory sessionFactory;
	protected Session session;
	
	protected void onSetUp() {
		session = SessionFactoryUtils.getSession(this.sessionFactory, true);
		session.setFlushMode(FlushMode.AUTO);
		TransactionSynchronizationManager.bindResource(this.sessionFactory,
				new SessionHolder(session));
	}

	protected void onTearDown() {
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		SessionFactoryUtils.releaseSession(session, sessionFactory);
	}

	protected String[] getConfigLocations() {
		return new String[] { "classpath:applicationContext-datasources.xml",
				"classpath:applicationContext-services.xml",
				"classpath:applicationContext-rest.xml",
				"classpath:applicationContext-security.xml" };
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
