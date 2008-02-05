package org.ncbo.stanford.manager.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.OntologyLoadManager;
import org.ncbo.stanford.manager.wrapper.AbstractOntologyManagerWrapperLexGrid;
import org.ncbo.stanford.manager.wrapper.AbstractOntologyManagerWrapperProtege;

public class OntologyLoadManagerImpl implements OntologyLoadManager {

	private static final Log log = LogFactory
			.getLog(OntologyLoadManagerImpl.class);

	private AbstractOntologyManagerWrapperLexGrid ontologyLoadManagerWrapperLexGrid;
	private AbstractOntologyManagerWrapperProtege ontologyLoadManagerWrapperProtege;

	public void loadOntology(OntologyBean ontology) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the ontologyLoadManagerWrapperLexGrid
	 */
	public AbstractOntologyManagerWrapperLexGrid getOntologyLoadManagerWrapperLexGrid() {
		return ontologyLoadManagerWrapperLexGrid;
	}

	/**
	 * @param ontologyLoadManagerWrapperLexGrid
	 *            the ontologyLoadManagerWrapperLexGrid to set
	 */
	public void setOntologyLoadManagerWrapperLexGrid(
			AbstractOntologyManagerWrapperLexGrid ontologyLoadManagerWrapperLexGrid) {
		this.ontologyLoadManagerWrapperLexGrid = ontologyLoadManagerWrapperLexGrid;
	}

	/**
	 * @return the ontologyLoadManagerWrapperProtege
	 */
	public AbstractOntologyManagerWrapperProtege getOntologyLoadManagerWrapperProtege() {
		return ontologyLoadManagerWrapperProtege;
	}

	/**
	 * @param ontologyLoadManagerWrapperProtege
	 *            the ontologyLoadManagerWrapperProtege to set
	 */
	public void setOntologyLoadManagerWrapperProtege(
			AbstractOntologyManagerWrapperProtege ontologyLoadManagerWrapperProtege) {
		this.ontologyLoadManagerWrapperProtege = ontologyLoadManagerWrapperProtege;
	}
}
