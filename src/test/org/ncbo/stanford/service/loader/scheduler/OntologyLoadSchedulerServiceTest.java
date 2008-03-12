package org.ncbo.stanford.service.loader.scheduler;

import org.ncbo.stanford.AbstractBioPortalTest;

public class OntologyLoadSchedulerServiceTest extends AbstractBioPortalTest {

	public void testProcessOntologyLoad() throws Exception {
		OntologyLoadSchedulerService service = (OntologyLoadSchedulerService) applicationContext
				.getBean("ontologyLoadSchedulerService",
						OntologyLoadSchedulerService.class);
		service.processOntologyLoad();
	}
}
