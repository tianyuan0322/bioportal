package org.ncbo.stanford.service.ontology.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.OntologyLoadManager;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
import org.ncbo.stanford.service.ontology.OntologyService;

public class OntologyServiceImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);

	private OntologyRetrievalManager ontologyRetrievalManager;
	private OntologyLoadManager ontologyLoadManager;

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions() {
		return ontologyRetrievalManager.findLatestOntologyVersions();
	}

	public OntologyBean findOntology(int id, String version) {
		return ontologyRetrievalManager.findOntology(id, version);
	}

	public List<OntologyBean> findOntologyVersions(int id) {
		return ontologyRetrievalManager.findOntologyVersions(id);
	}

	public List<String> findProperties(int id) {
		return ontologyRetrievalManager.findProperties(id);
	}

	/**
	 * Load the specified ontology into the BioPortal repository.  At minimum, the ontology bean contain the 
	 * ontology file/uri reference.
	 */
	public void loadOntology(OntologyBean ontology) {
		ontologyLoadManager.loadOntology(ontology);
	}

	/**
	 * @return the ontologyRetrievalManager
	 */
	public OntologyRetrievalManager getOntologyRetrievalManager() {
		return ontologyRetrievalManager;
	}

	/**
	 * @param ontologyRetrievalManager the ontologyRetrievalManager to set
	 */
	public void setOntologyRetrievalManager(
			OntologyRetrievalManager ontologyRetrievalManager) {
		this.ontologyRetrievalManager = ontologyRetrievalManager;
	}

	/**
	 * @return the ontologyLoadManager
	 */
	public OntologyLoadManager getOntologyLoadManager() {
		return ontologyLoadManager;
	}

	/**
	 * @param ontologyLoadManager the ontologyLoadManager to set
	 */
	public void setOntologyLoadManager(OntologyLoadManager ontologyLoadManager) {
		this.ontologyLoadManager = ontologyLoadManager;
	}
}
