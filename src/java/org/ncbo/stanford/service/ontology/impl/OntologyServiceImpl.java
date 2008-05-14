package org.ncbo.stanford.service.ontology.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.wrapper.OntologyRetrievalManagerWrapper;
import org.ncbo.stanford.service.ontology.OntologyService;

public class OntologyServiceImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);
	
	private static int MAX_RESULTS = 100;

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
//	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>();
//	private Map<String, OntologyRetrievalManagerWrapper> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManagerWrapper>();
	
	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions(){
		
		ArrayList<OntologyBean> ontBeanList = new ArrayList<OntologyBean>(1);
		List<NcboOntology> ontEntityList = ncboOntologyVersionDAO.findLatestOntologyVersions();

		for (NcboOntology ncboOntology : ontEntityList) {
			OntologyBean ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
			ontBeanList.add(ontologyBean);
		}

		return ontBeanList;
	}
		

	/**
	 * Returns a single ontology version record
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public OntologyBean findOntology(Integer ontologyVersionId){
		
		return null;
	}

	/**
	 * Finds the latest version of a given ontology
	 * 
	 * @param ontologyId
	 * @return
	 */
	public OntologyBean findLatestOntologyVersion(Integer ontologyId){
		
		return null;
	}
	
	/**
	 * Find all versions of the given ontology in the system
	 * 
	 * @param ontologyId
	 * @return
	 */
	public List<OntologyBean> findAllOntologyVersions(Integer ontologyId){
		
		return new ArrayList();
		
	}

	/**
	 * Find ontology properties, such as "definition", "synonyms", "cui"...
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public List<String> findProperties(Integer ontologyVersionId){
		
		return new ArrayList();
		
	}
/*
	private OntologyRetrievalManager ontologyRetrievalManager;


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


	public OntologyRetrievalManager getOntologyRetrievalManager() {
		return ontologyRetrievalManager;
	}


	public void setOntologyRetrievalManager(
			OntologyRetrievalManager ontologyRetrievalManager) {
		this.ontologyRetrievalManager = ontologyRetrievalManager;
	}

	public List<OntologyBean> findAllOntologyVersions(Integer ontologyId) {
		// TODO Auto-generated method stub
		return null;
	}

	public OntologyBean findLatestOntologyVersion(Integer ontologyId) {
		// TODO Auto-generated method stub
		return null;
	}

	public OntologyBean findOntology(Integer ontologyVersionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> findProperties(Integer ontologyVersionId) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
}
