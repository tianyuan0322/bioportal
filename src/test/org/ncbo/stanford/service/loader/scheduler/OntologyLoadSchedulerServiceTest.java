package org.ncbo.stanford.service.loader.scheduler;

import org.ncbo.stanford.AbstractBioPortalTest;

public class OntologyLoadSchedulerServiceTest extends AbstractBioPortalTest {

	public void testParseOntology() throws Exception {
		OntologyLoadSchedulerService service = (OntologyLoadSchedulerService) applicationContext
				.getBean("ontologyLoadSchedulerService",
						OntologyLoadSchedulerService.class);
		//lexgrid sample
		//service.parseOntology(3231);
		
		//protege sample
		service.parseOntology("34253");
	}

/*	public void testParseOntologies() throws Exception {
		OntologyLoadSchedulerService service = (OntologyLoadSchedulerService) applicationContext
				.getBean("ontologyLoadSchedulerService",
						OntologyLoadSchedulerService.class);
		service.parseOntologies();
	}
*/
}
