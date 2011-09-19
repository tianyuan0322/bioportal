package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.manager.purl.impl.PurlClientManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class PurlClientManagerImplTest  extends AbstractBioPortalTest {

	@Autowired
	PurlClientManagerImpl purlClientManager;
	
	@Test
	public void testCreateAdvancedPartialPurl()  {
		System.out.println("testCreateAdvancedPartialPurl()");
        String path="/test/bbb";
        String targetUrl= "/virtual/1105";
		boolean success= purlClientManager.createAdvancedPartialPurl(path, targetUrl);
		assertTrue(success);
	}	
	
	@Test
	public void testDoesPurlExist()  {
		System.out.println("testDoesPurlExist()");
        String path="/test/bbb";
		boolean success= purlClientManager.doesPurlExist(path);
		assertTrue(success);
	}		
}
