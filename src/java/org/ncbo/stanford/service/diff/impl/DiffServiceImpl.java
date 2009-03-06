/**
 * @author: Natasha Noy noy@smi.stanford.edu
 */
package org.ncbo.stanford.service.diff.impl;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.ncbo.stanford.bean.*;
import org.ncbo.stanford.exception.*;
import org.ncbo.stanford.manager.diff.*;
import org.ncbo.stanford.service.diff.*;
import org.ncbo.stanford.service.ontology.*;
import org.ncbo.stanford.service.ontology.impl.*;
import org.springframework.transaction.annotation.*;

@Transactional
public class DiffServiceImpl implements DiffService {

	private static final Log log = LogFactory
			.getLog(OntologyServiceImpl.class);
	private OntologyService ontologyService;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	private Map<String, OntologyDiffManager> ontologyDiffHandlerMap = new HashMap<String, OntologyDiffManager>(
			0);

	/**
	 * Return the list of all diff pairs for a given ontology id
	 * 
	 * @param ontologyId
	 * @return
	 * @throws Exception 
	 */
	public List<ArrayList<String>> getAllDiffsForOntology(Integer ontologyId) throws Exception {
		OntologyBean ontologyBean = findOntologyBeanByOntologyId (ontologyId);
		
		OntologyDiffManager ontologyDiffManager = getDiffManager (ontologyBean);
		return ontologyDiffManager.getAllDiffsForOntology (ontologyId);
	}

	/**
	 * Get file object for rdf-formated diff between specified ontology verisons
	 * 
	 * @param ontologyVersionId1, ontologyVerisonId2
	 * @param format
	 * @return
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */
	public File getDiffFileForOntologyVersions(Integer ontologyVerisonId1,
			Integer ontologyVersionId2, String format) throws FileNotFoundException, Exception {
		OntologyBean ontologyBean = findOntologyBeanByOntologyVersionId (ontologyVerisonId1);
		OntologyDiffManager ontologyDiffManager = getDiffManager (ontologyBean);
		
		return ontologyDiffManager.getDiffFileForOntologyVersions (ontologyVerisonId1, ontologyVersionId2, format);
		
	}
	
	
	// Non-interface methods
	
	private OntologyBean findOntologyBeanByOntologyId (Integer ontologyId) throws Exception {
		OntologyBean ontologyBean = ontologyService.findLatestOntologyVersion(ontologyId);
		if (ontologyBean == null) {
			throw new Exception ();
		}
		return ontologyBean;
	}

	private OntologyBean findOntologyBeanByOntologyVersionId (Integer ontologyVersionId) throws Exception {
		OntologyBean ontologyBean = ontologyService.findOntology(ontologyVersionId);
		if (ontologyBean == null) {
			throw new Exception ();
		}
		return ontologyBean;
	}


	
	private OntologyDiffManager getDiffManager(OntologyBean ontologyBean)
	throws Exception {
		String formatHandler = ontologyFormatHandlerMap.get(ontologyBean.getFormat());
		OntologyDiffManager diffManager = ontologyDiffHandlerMap
		.get(formatHandler);

		if (diffManager == null) {
			log.error("Cannot find diffHandler for "
					+ ontologyBean.getFormat());
			throw new InvalidOntologyFormatException(
					"Cannot find formatHandler for " + ontologyBean.getFormat());
		}

		return diffManager;
	}

	/**
	 * @param ontologyDiffHandlerMap
	 *            the ontologyDiffHandlerMap to set
	 */
	public void setOntologyDiffHandlerMap(
			Map<String, OntologyDiffManager> ontologyDiffHandlerMap) {
		this.ontologyDiffHandlerMap = ontologyDiffHandlerMap;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}



}
