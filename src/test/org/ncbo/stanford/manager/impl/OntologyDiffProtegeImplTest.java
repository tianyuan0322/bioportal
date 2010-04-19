/*
 * Contributor(s): Natasha Noy noy@smi.stanford.edu
 */
package org.ncbo.stanford.manager.impl;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.diff.impl.OntologyDiffManagerProtegeImpl;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.springframework.beans.factory.annotation.Autowired;

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
	private OntologyMetadataManager ontologyMetadataManager;

	@Autowired
	private OntologyDiffManagerProtegeImpl diffManagerProtege;

	@Test
	public void testDiff() throws Exception {
		System.out.println("Starting diff test.");

		OntologyBean oldVersion = ontologyMetadataManager
				.findOntologyOrViewVersionById(OLD_VERSION_ID);

		OntologyBean newVersion = ontologyMetadataManager
				.findOntologyOrViewVersionById(NEW_VERSION_ID);

		System.out.println("old: " + oldVersion + ", new: " + newVersion);
		diffManagerProtege.createDiff(NEW_VERSION_ID, OLD_VERSION_ID);
	}

	@Test
	public void testDiffByOntologyId() throws Exception {
		System.out.println("Starting diff test.");

		diffManagerProtege.createDiffForLatestActiveOntologyVersionPair(new Integer(
				ONTOLOGY_ID));
	}
}
