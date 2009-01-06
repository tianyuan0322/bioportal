package org.ncbo.stanford;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Contains setup and configuration functionality common to all BioPortal tests
 * 
 * @author Michael Dorf
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-datasources.xml",
		"/applicationContext-services.xml", "/applicationContext-rest.xml",
		"/applicationContext-security.xml" })
public abstract class AbstractBioPortalTest {

	@Autowired
	private SessionFactory sessionFactory;
	protected Session session;

	@Before
	public void setUp() {
		session = SessionFactoryUtils.getSession(this.sessionFactory, true);
		session.setFlushMode(FlushMode.AUTO);
		TransactionSynchronizationManager.bindResource(this.sessionFactory,
				new SessionHolder(session));
	}

	@After
	public void tearDown() {
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		session.flush();
		SessionFactoryUtils.releaseSession(session, sessionFactory);
	}
}
