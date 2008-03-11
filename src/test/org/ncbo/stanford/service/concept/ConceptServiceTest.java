package org.ncbo.stanford.service.concept;

import java.util.ArrayList;

import org.hibernate.Session;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ConceptServiceTest extends AbstractBioPortalTest {

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
}
