package org.ncbo.stanford.service.concept;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ConceptServiceTest extends
		AbstractDependencyInjectionSpringContextTests {

	private SessionFactory sessionFactory;

	protected String[] getConfigLocations() {
		return new String[] { "classpath:applicationContext-datasources.xml",
				"classpath:applicationContext-services.xml",
				"classpath:applicationContext-rest.xml",
				"classpath:applicationContext-security.xml" };
	}

	public void testfindRoot() {

		ConceptService service = (ConceptService) applicationContext.getBean(
				"conceptService", ConceptService.class);

		Session session = SessionFactoryUtils.getSession(this.sessionFactory,
				true);
		TransactionSynchronizationManager.bindResource(this.sessionFactory,
				new SessionHolder(session));

		
		
		
		
		ClassBean root = service.findRoot(2854);

		
		
		
		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) root
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel() + " " + subclass.getId());
		}

		
		
		
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		SessionFactoryUtils.releaseSession(session, sessionFactory);

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
