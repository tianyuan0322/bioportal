package org.ncbo.stanford.service.loader.scheduler;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyLoadSchedulerServiceTest extends AbstractBioPortalTest {

	@Autowired
	OntologyLoadSchedulerService service;
	
	@Test
	public void testParseOntology() throws Exception {
		// lexgrid sample
		// service.parseOntology(3231);

		// protege sample
		// 34253
		// 34265
		service.parseOntology("3905");
	}

	@Test
	public void testIndexOntology() throws Exception {
		// lexgrid sample
		// service.parseOntology(3231);

		// protege sample
		// 34253
		// 34265
		service.indexOntology("3905");
	}
}
