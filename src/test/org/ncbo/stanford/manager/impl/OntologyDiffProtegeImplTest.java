/*
 * Contributor(s): Natasha Noy noy@smi.stanford.edu
 */
package org.ncbo.stanford.manager.impl;

import org.junit.*;
import org.ncbo.stanford.*;
import org.ncbo.stanford.domain.custom.dao.*;
import org.ncbo.stanford.domain.custom.entity.*;
import org.ncbo.stanford.manager.diff.impl.*;
import org.springframework.beans.factory.annotation.*;


/**
 * Tests comparing ontologies using PromptDiff
 * 
 * @author Natasha Noy
 */

public class OntologyDiffProtegeImplTest extends AbstractBioPortalTest {

	private final static int OLD_VERSION_ID = 39522;
	private final static int NEW_VERSION_ID = 39523;
	
	private final static int ONTOLOGY_ID = 1262;


	@Autowired
	CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	
	@Autowired
	OntologyDiffManagerProtegeImpl diffManagerProtege;
	

	@Test
	public void testDiff () throws Exception {
		System.out.println ("Starting diff test.");

		VNcboOntology oldVersion = 	ncboOntologyVersionDAO.findOntologyVersion(OLD_VERSION_ID);

		VNcboOntology newVersion = ncboOntologyVersionDAO.findOntologyVersion(NEW_VERSION_ID);
		
		System.out.println ("old: " + oldVersion + ", new: " + newVersion);
		diffManagerProtege.createDiff(oldVersion, newVersion);
	}

	@Test
	public void testDiffByOntologyId () throws Exception {
		System.out.println ("Starting diff test.");

		diffManagerProtege.createDiffForTwoLatestVersions(new Integer (ONTOLOGY_ID));
	}

}
