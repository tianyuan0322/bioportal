package org.ncbo.stanford;

import org.hibernate.SessionFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class AbstractBioPortalTest extends
		AbstractDependencyInjectionSpringContextTests {

	protected SessionFactory sessionFactory;

	protected String[] getConfigLocations() {
		return new String[] { "classpath:applicationContext-datasources.xml",
				"classpath:applicationContext-services.xml",
				"classpath:applicationContext-rest.xml",
				"classpath:applicationContext-security.xml" };
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
