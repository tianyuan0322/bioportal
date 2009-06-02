/**
 * @author: Natasha Noy noy@smi.stanford.edu
 */
package org.ncbo.stanford.manager.diff.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.diff.OntologyDiffManager;

public class OntologyDiffManagerLexGridImpl extends
		AbstractOntologyManagerLexGrid implements OntologyDiffManager {

	public void createDiff(OntologyBean ontologyVersionOld,
			OntologyBean ontologyVersionNew) throws Exception {
		throw new UnsupportedOperationException();
	}

	public void createDiffForTwoLatestVersions(Integer ontologyId)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	public List<ArrayList<String>> getAllDiffsForOntology(Integer ontologyId) {
		return null; //we do not have diffs for LexGrid ontologies currently
	}

	public File getDiffFileForOntologyVersions(Integer ontologyVerisonId1,
			Integer ontologyVersionId2, String format)
			throws FileNotFoundException {
		return null; //we do not have diffs for LexGrid ontologies currently
	}

	public boolean diffExists(OntologyBean ontologyVersionOld,
			OntologyBean ontologyVersionNew) throws Exception {
		throw new UnsupportedOperationException();
	}
}
