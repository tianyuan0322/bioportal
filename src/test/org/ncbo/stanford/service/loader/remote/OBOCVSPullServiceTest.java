package org.ncbo.stanford.service.loader.remote;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.springframework.beans.factory.annotation.Autowired;

public class OBOCVSPullServiceTest extends AbstractBioPortalTest {

	@Autowired
	OBOCVSPullService service;

	@Test
	public void testDoCVSPull() {
		service.doOntologyPull();
	}
}
