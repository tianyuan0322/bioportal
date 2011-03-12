package org.ncbo.stanford.service.loader.fixer.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.search.OntologyHitBean;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.service.loader.fixer.OntologyIntegrityFixerService;
import org.ncbo.stanford.service.ontology.AbstractOntologyService;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Michael Dorf
 * 
 *         Class responsible for checking fixing the ontology integrity. Fixes
 *         dependent artifacts such as search index or metrics that may be out
 *         of sync with the latest ontology version
 */
@Transactional
public class OntologyIntegrityFixerServiceImpl extends AbstractOntologyService
		implements OntologyIntegrityFixerService {

	private static final Log log = LogFactory
			.getLog(OntologyIntegrityFixerServiceImpl.class);

	private OntologyMetadataManager ontologyMetadataManager;
	private IndexSearchService indexSearchService;

	/**
	 * Goes through all latest versions of parsed ontologies and fixes dependent
	 * artifacts such as search index or metrics that may be out of sync with
	 * the latest ontology version
	 */
	public void fixOntologies() {
		errorOntologies.clear();

		try {
			List<OntologyBean> ontologies = ontologyMetadataManager
					.findLatestActiveOntologyOrOntologyViewVersions();
			// fix index
			fixSearchIndex(ontologies);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Rebuilds index for "problem" ontologies - those that are either not in
	 * the index or have a wrong version in the index
	 * 
	 * @param ontologies
	 */
	public void fixSearchIndex(List<OntologyBean> ontologies) {
		List<Integer> problemOntologies = new ArrayList<Integer>(0);

		for (OntologyBean ontology : ontologies) {
			try {
				Integer ontologyId = ontology.getOntologyId();
				OntologyHitBean ontologyHit = indexSearchService
						.checkOntologyInIndex(ontologyId);
				Integer ontologyVersionId = ontologyHit.getOntologyVersionId();

				if (ontologyVersionId == null
						|| !ontologyVersionId.equals(ontology.getId())) {
					problemOntologies.add(ontologyId);
				}
				indexSearchService.indexOntologies(problemOntologies, false,
						true);
			} catch (Exception e) {
				addErrorOntology(errorOntologies, ontology.getId().toString(),
						ontology, e.getMessage());
				log.error(e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param ontologyMetadataManager
	 *            the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}

	/**
	 * @param indexSearchService
	 *            the indexSearchService to set
	 */
	public void setIndexSearchService(IndexSearchService indexSearchService) {
		this.indexSearchService = indexSearchService;
	}
}
