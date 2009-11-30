package org.ncbo.stanford.service.concept;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.springframework.beans.factory.annotation.Autowired;

// Note: has been requiring a bit more than 256M RAM to run.

/**
 * Test output of ontology as RDF N3 triples.
 * 
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public class DumpRDFServiceTest extends AbstractBioPortalTest {
	
	// Local ID -- will need to be changed to run on stagerest.
	private static final Integer ONTOLOGY_VERSION_ID = new Integer(10001);
	
	@Autowired
	DumpRDFService dumpRDFService;
	
	@Test
	public void testGenerateRDFDump() throws Exception {
		String result = dumpRDFService.generateRDFDump(ONTOLOGY_VERSION_ID);
		System.out.println("Here is the result:");
		System.out.print(result);
	}
}
