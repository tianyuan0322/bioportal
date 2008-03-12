package org.ncbo.stanford.service.concept;

import java.util.ArrayList;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;

public class ConceptServiceTest extends AbstractBioPortalTest {

	public void testfindRoot() throws Exception {
		ConceptService service = (ConceptService) applicationContext.getBean(
				"conceptService", ConceptService.class);
		ClassBean root = service.findRoot(2854).get(0);

		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) root
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());

		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel() + " " + subclass.getId());
		}
	}
}
