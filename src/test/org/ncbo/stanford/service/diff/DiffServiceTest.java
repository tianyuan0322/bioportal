package org.ncbo.stanford.service.diff;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.ncbo.stanford.*;
import org.springframework.beans.factory.annotation.*;

/**
 * 
 * @author Natasha Noy
 */
public class DiffServiceTest extends AbstractBioPortalTest {

	private final static Integer OLD_VERSION_ID = 39522;
	private final static Integer NEW_VERSION_ID = 39523;
	
	private final static Integer ONTOLOGY_ID = 1262;

	@Autowired
	DiffService diffService;

	@Test
	public void testGetAllDiffsForOntology() throws Exception {

		System.out.println ("DiffServiceTest: testGetAllDiffsForOntology().......................BEGIN");

		System.out.println ("Good ontology id");		
		List<ArrayList<String>> diffs = diffService.getAllDiffsForOntology(ONTOLOGY_ID);

		System.out.println(diffs.toString());

		System.out.println ("Bad ontology id");		
		
		try {
			diffs = diffService.getAllDiffsForOntology(ONTOLOGY_ID + 1000);
		} catch (Exception e) {
			System.out.println("File not found exception: " + e.getMessage());
		}

		System.out.println(diffs.toString());
		

		System.out.println ("DiffServiceTest: testGetAllDiffsForOntology()........................DONE");

	}


	@Test
	public void testGetTextDiffFileForOntologyVersions() throws Exception {

		System.out.println ("DiffServiceTest: testGetTextDiffFileForOntologyVersions().......................BEGIN");
		File diffFileName = null;
		
		System.out.println ("Good version ids in correct order");		
		diffFileName = diffService.getDiffFileForOntologyVersions(OLD_VERSION_ID, NEW_VERSION_ID, "txt");
		System.out.println(diffFileName);

		System.out.println ("Good version ids in reverse order");		
		diffFileName = diffService.getDiffFileForOntologyVersions(NEW_VERSION_ID, OLD_VERSION_ID, "txt");
		System.out.println(diffFileName);

		System.out.println ("Correct version ids, but no file");		
		try {
			diffFileName = diffService.getDiffFileForOntologyVersions(NEW_VERSION_ID, NEW_VERSION_ID, "txt");
		} catch (FileNotFoundException fnfe) {
			System.out.println("File not found exception: " + fnfe.getMessage());
		}
			
		System.out.println ("Bad version ids");		
		try {
			diffFileName = diffService.getDiffFileForOntologyVersions(NEW_VERSION_ID, NEW_VERSION_ID-3, "txt");
		} catch (FileNotFoundException fnfe) {
			System.out.println("File not found exception: " + fnfe.getMessage());
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());			
		}

		System.out.println ("DiffServiceTest: testGetTextDiffFileForOntologyVersions()..........................DONE");

	}

}
