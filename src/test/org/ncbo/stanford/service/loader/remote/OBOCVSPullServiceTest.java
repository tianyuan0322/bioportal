package org.ncbo.stanford.service.loader.remote;

import org.ncbo.stanford.AbstractBioPortalTest;

public class OBOCVSPullServiceTest extends AbstractBioPortalTest {

	public void testDoCVSPull() {
		OBOCVSPullService service = (OBOCVSPullService) applicationContext
				.getBean("oboCVSPullService", OBOCVSPullService.class);
		service.doCVSPull();
	}
}
