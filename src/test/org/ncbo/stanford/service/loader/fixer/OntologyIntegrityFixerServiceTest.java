package org.ncbo.stanford.service.loader.fixer;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyIntegrityFixerServiceTest  extends AbstractBioPortalTest {

	@Autowired
	OntologyIntegrityFixerService service;

	@Test
	public void testFixOntologies() {
		service.fixOntologies();
	}
}
