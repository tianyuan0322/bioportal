/**
 * @author: Natasha Noy noy@smi.stanford.edu
 */
package org.ncbo.stanford.manager.diff.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.diff.OntologyDiffManager;

public class OntologyDiffManagerLexGridImpl extends
		AbstractOntologyManagerLexGrid implements OntologyDiffManager {

	public void createDiff(VNcboOntology ontologyVersionOld,
			VNcboOntology ontologyVersionNew) throws Exception {
		throw new UnsupportedOperationException();
	}

	public void createDiffForTwoLatestVersions(Integer ontologyId)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	public List<ArrayList<String>> getAllDiffsForOntology(Integer ontologyId) {
		throw new UnsupportedOperationException();
	}

	public File getDiffFileForOntologyVersions(Integer ontologyVerisonId1,
			Integer ontologyVersionId2, String format)
			throws FileNotFoundException {
		throw new UnsupportedOperationException();
	}

	public boolean diffExists(VNcboOntology ontologyVersionOld,
			VNcboOntology ontologyVersionNew) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
