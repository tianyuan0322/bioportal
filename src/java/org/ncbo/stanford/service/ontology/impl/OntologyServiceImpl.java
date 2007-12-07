package org.ncbo.stanford.service.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyMetadataDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.OntologyAndConceptManager;
import org.ncbo.stanford.service.ontology.OntologyService;

public class OntologyServiceImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);


	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private CustomNcboOntologyMetadataDAO ncboOntologyMetadataDAO;
	private CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	

	private OntologyAndConceptManager ontologyAndConceptManager;

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

	public void createOntology(OntologyBean ontology) {
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
}
