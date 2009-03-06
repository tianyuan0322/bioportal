package org.ncbo.stanford.manager.diff;

/**
 * An interface designed to provide an abstraction layer to ontology diff 
 * operation. The service layer will consume this interface instead of directly
 * calling a specific implementation (i.e. LexGrid, Protege etc.). This
 * interface should be used by internal services and should not be exposed to
 * upper layers.
 * 
 * @author Natasha Noy
 * 
 */

import java.io.*;
import java.util.*;

import org.ncbo.stanford.domain.custom.entity.*;

public interface OntologyDiffManager {
	/** 
	 * Creates a diff between two ontology versions
	 * 
	 * @param ontologyVersionOld, ontologyVersionNew
	 * 
	 * @throws Exception
	 */

	public void createDiff (VNcboOntology ontologyVersionOld, VNcboOntology ontologyVersionNew)
	throws Exception;

	/**
	 * Creates a diff between the ontology that has just been loaded and its
	 * previous version(if one exists in BioPortal).
	 * Save the diff in a file
	 * 
	 * @param ontologyBean
	 *	 
	 * @throws Exception
	 */

	public void createDiffForTwoLatestVersions (Integer ontologyId)
	throws Exception;

	/**
	 * Returns the file for the diff between two ontology versions in th specified format
	 * (in any order)
	 * 
	 * @param ontologyVersionId1, ontologyVersionId2
	 * @throws FileNotFoundException
	 */

	public File getDiffFileForOntologyVersions (Integer ontologyVerisonId1,
			Integer ontologyVersionId2, String format) 
	throws FileNotFoundException, Exception;

	/**
	 * Returns a list of all diffs that are available for a given ontology 
	 * in the following format:
	 * <versionId1, versionId2>, <versionId2, versionId3>...
	 * 
	 * @param onotlogyId
	 */
	
	public List<ArrayList<String>> getAllDiffsForOntology(Integer ontologyId);

}