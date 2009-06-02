package org.ncbo.stanford.service.loader.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyLoadSchedulerServiceTest extends AbstractBioPortalTest {

	@Autowired
	OntologyLoadSchedulerService service;
	
	@Test
	public void testParseOntology() throws Exception {
		// lexgrid sample: 3231
		// protege samples: 34253, 34265
		List<Integer> ontologies = new ArrayList<Integer>(1);
		ontologies.add(3905);
		service.parseOntologies(ontologies, null);
	}
}
