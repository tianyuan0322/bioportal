package org.ncbo.stanford.manager.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.diff.impl.OntologyDiffManagerLexGridImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Pradip Kanjamala
 */
public class OntologyDiffManagerLexGridImplTest extends AbstractBioPortalTest {

	@Autowired
	OntologyDiffManagerLexGridImpl diffManager;
	Date startDate_;
	Date endDate_;

	@Test
	public void testCellDiff() throws Exception {
		System.out.println("testCellDiff");
		startDate_ = new Date();
		OntologyBean ontologyOld = diffManager.getOntologyBeanByDisplayNameAndOntologyId(
				OntologyLoaderLexGridImplTest.OBO_CELL_OLD_DISPLAY_LABEL,
				OntologyLoaderLexGridImplTest.OBO_CELL_OLD_ONTOLOGY_ID);
		OntologyBean ontologyNew = diffManager.getOntologyBeanByDisplayNameAndOntologyId(
				OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
				OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		// diffManager.printOntologyDiffs(ontologyOld, ontologyNew);
		diffManager.createDiff(ontologyOld, ontologyNew);

		endDate_ = new Date();
		System.out.println(endDate_.getTime() - startDate_.getTime() + " ms");

	}

	@Test
	public void testGetAllDiffsForOntology() throws Exception {
		OntologyBean ontologyOld = diffManager.getOntologyBeanByDisplayNameAndOntologyId(
				OntologyLoaderLexGridImplTest.OBO_CELL_OLD_DISPLAY_LABEL,
				OntologyLoaderLexGridImplTest.OBO_CELL_OLD_ONTOLOGY_ID);
		Integer ontology_id = ontologyOld.getOntologyId();
		System.out
				.println("DiffServiceTest: testGetAllDiffsForOntology().......................BEGIN");

		System.out.println("Good ontology id");
		List<ArrayList<String>> diffs = diffManager
				.getAllDiffsForOntology(ontology_id);

		System.out.println(diffs.toString());

		System.out.println("Bad ontology id");

		try {
			diffs = diffManager.getAllDiffsForOntology(ontology_id + 1000);
			System.out.println(diffs.toString());
		} catch (Exception e) {
			System.out.println("File not found exception: " + e.getMessage());
		}

		System.out
				.println("diffManagerTest: testGetAllDiffsForOntology()........................DONE");

	}

	@Test
	public void testGetTextDiffFileForOntologyVersions() throws Exception {
		OntologyBean ontologyOld = diffManager.getOntologyBeanByDisplayNameAndOntologyId(
				OntologyLoaderLexGridImplTest.OBO_CELL_OLD_DISPLAY_LABEL,
				OntologyLoaderLexGridImplTest.OBO_CELL_OLD_ONTOLOGY_ID);
		Integer old_ov_id = ontologyOld.getId();
		OntologyBean ontologyNew = diffManager.getOntologyBeanByDisplayNameAndOntologyId(
				OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL,
				OntologyLoaderLexGridImplTest.OBO_CELL_ONTOLOGY_ID);
		Integer new_ov_id = ontologyNew.getId();
		System.out
				.println("diffManagerTest: testGetTextDiffFileForOntologyVersions().......................BEGIN");
		File diffFileName = null;

		System.out.println("Good version ids in correct order");
		diffFileName = diffManager.getDiffFileForOntologyVersions(old_ov_id,
				new_ov_id, "txt");
		System.out.println(diffFileName);

		System.out.println("Good version ids in reverse order");
		diffFileName = diffManager.getDiffFileForOntologyVersions(new_ov_id,
				old_ov_id, "txt");
		System.out.println(diffFileName);

		System.out.println("Correct version ids, but no file");
		try {
			diffFileName = diffManager.getDiffFileForOntologyVersions(
					new_ov_id, new_ov_id, "txt");
		} catch (FileNotFoundException fnfe) {
			System.out
					.println("Request not processed. File not found exception thrown: "
							+ fnfe.getMessage());
		}

		System.out.println("Bad version ids");
		try {
			diffFileName = diffManager.getDiffFileForOntologyVersions(
					new_ov_id, new_ov_id - 3, "txt");

		} catch (Exception e) {
			System.out
					.println("Request could not be processed. Exception thrown: "
							+ e.getMessage());
		}

		System.out
				.println("diffManagerTest: testGetTextDiffFileForOntologyVersions()..........................DONE");

	}
}
