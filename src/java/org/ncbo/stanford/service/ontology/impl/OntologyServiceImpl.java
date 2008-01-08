package org.ncbo.stanford.service.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.OntologyAndConceptManager;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.manager.OntologyLoader;

public class OntologyServiceImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);

	private OntologyAndConceptManager ontologyAndConceptManager = null;
	private OntologyLoader ontologyLoader = null;

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions() {
		return ontologyAndConceptManager.findLatestOntologyVersions();
	}

	public OntologyBean findOntology(int id, String version) {
		return new OntologyBean();
	}

	public List<OntologyBean> findOntologyVersions(int id) {
		return new ArrayList();
	}

	public List<String> findProperties(int id) {
		return new ArrayList();
	}

	/**
	 * Assumption here is that creat ontology loads the specified ontology into
	 * the BioPortal repository.
	 */
	public void createOntology(OntologyBean ontology) {
		try {
			ontologyLoader.loadOntology(ontology);
		} 
		catch (Exception exc) {
			// This is where one would set the code to indicate a failed ontology load...
			
			// TODO: There needs to be a better mechanism to handle exceptions
			// during ontology loads (e.g. this method could throw an exception
			log.error("Unable load specified ontology: "
					+ ontology.getDisplayLabel());
			exc.printStackTrace();
		}
	}

	/**
	 * @return the ontologyAndConceptManager
	 */
	public OntologyAndConceptManager getOntologyAndConceptManager() {
		return ontologyAndConceptManager;
	}

	/**
	 * @param ontologyAndConceptManager
	 *            the ontologyAndConceptManager to set
	 */
	public void setOntologyAndConceptManager(
			OntologyAndConceptManager ontologyAndConceptManager) {
		this.ontologyAndConceptManager = ontologyAndConceptManager;
	}
	
	/**
	 * @return the ontologyLoader
	 */
	public OntologyLoader getOntologyLoader() {
		return ontologyLoader;
	}
	
	/**
	 * @param ontologyLoader
	 *            the ontologyLoader to set
	 */
	public void setOntologyLoader(OntologyLoader ontologyLoader) {
		this.ontologyLoader = ontologyLoader;
	}
}
