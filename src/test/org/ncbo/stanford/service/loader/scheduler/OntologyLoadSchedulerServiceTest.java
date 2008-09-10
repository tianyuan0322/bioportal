package org.ncbo.stanford.service.loader.scheduler;

import org.ncbo.stanford.AbstractBioPortalTest;

public class OntologyLoadSchedulerServiceTest extends AbstractBioPortalTest {

	public void testParseOntology() throws Exception {
		OntologyLoadSchedulerService service = (OntologyLoadSchedulerService) applicationContext
				.getBean("ontologyLoadSchedulerService",
						OntologyLoadSchedulerService.class);
		// lexgrid sample
		// service.parseOntology(3231);

		// protege sample
		// 34253
		// 34265
		service.parseOntology("3905");
	}

	public void testIndexOntology() throws Exception {
		OntologyLoadSchedulerService service = (OntologyLoadSchedulerService) applicationContext
				.getBean("ontologyLoadSchedulerService",
						OntologyLoadSchedulerService.class);
		// lexgrid sample
		// service.parseOntology(3231);

		// protege sample
		// 34253
		// 34265
		service.indexOntology("3905");
	}
}
