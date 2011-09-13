package org.ncbo.stanford.service.loader.fixer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.bean.search.OntologyHitBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.service.loader.fixer.OntologyIntegrityFixerService;
import org.ncbo.stanford.service.metrics.MetricsService;
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
	private MetricsService metricsService;

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
			if (log.isInfoEnabled()) {
				log.info("Running search index fixer...");
			}

			fixSearchIndex(ontologies);

			if (log.isInfoEnabled()) {
				log.info("Search index fixer run completed...");
			}

			// fix metrics
			if (log.isInfoEnabled())
				log.info("Running metrics fixer...");

			fixMetrics(ontologies);

			if (log.isInfoEnabled())
				log.info("Metrics fixer run completed...");
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
		Map<Integer, OntologyBean> problemOntologies = new HashMap<Integer, OntologyBean>(
				0);

		for (OntologyBean ontology : ontologies) {
			try {
				Integer ontologyId = ontology.getOntologyId();
				OntologyHitBean ontologyHit = indexSearchService
						.checkOntologyInIndex(ontologyId);
				Integer ontologyVersionId = ontologyHit.getOntologyVersionId();

				if (ontologyVersionId == null
						|| !ontologyVersionId.equals(ontology.getId())) {
					problemOntologies.put(ontologyId, ontology);
				}
			} catch (Exception e) {
				addErrorOntology(errorOntologies, ontology.getId().toString(),
						ontology, e.getMessage());
				log.error(e);
				e.printStackTrace();
			}
		}

		try {
			if (log.isInfoEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb
						.append("The following ontologies were found to be out of sync with the search index and are being re-indexed:\n\n");

				for (Map.Entry<Integer, OntologyBean> entry : problemOntologies
						.entrySet()) {
					sb.append(entry.getValue());
					sb.append("\n\n");
				}
				log.info(sb.toString());
			}
			indexSearchService.indexOntologies(new ArrayList<Integer>(
					problemOntologies.keySet()), false, true);
		} catch (Exception e) {
			log.error("Unable to fix search index due to the following error: "
					+ e);
			e.printStackTrace();
		}
	}

	private void fixMetrics(List<OntologyBean> ontologies) {
		Map<Integer, OntologyBean> problemOntologies = new HashMap<Integer, OntologyBean>(
				0);

		for (OntologyBean ontology : ontologies) {
			try {
				OntologyMetricsBean metricsBean = metricsService
						.getOntologyMetrics(ontology);

				if (metricsBean == null
						|| (metricsBean != null && metricsBean
								.getMaximumDepth() == null)) {
					problemOntologies.put(ontology.getId(), ontology);
				}
			} catch (MetadataException e) {
				problemOntologies.put(ontology.getId(), ontology);
			} catch (Exception e) {
				addErrorOntology(errorOntologies, ontology.getId().toString(),
						ontology, e.getMessage());
				log.error(e);
				e.printStackTrace();
			}
		}

		try {
			if (log.isInfoEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb
						.append("The following ontologies were found to be missing metrics and are being re-calculated:\n\n");

				for (Map.Entry<Integer, OntologyBean> entry : problemOntologies
						.entrySet()) {
					sb.append(entry.getValue());
					sb.append("\n\n");
				}
				log.info(sb.toString());
			}

			metricsService.extractOntologyMetrics(new ArrayList<Integer>(
					problemOntologies.keySet()));
		} catch (Exception e) {
			log.error("Unable to fix search index due to the following error: "
					+ e);
			e.printStackTrace();
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

	/**
	 * @param metricsService
	 *            the metricsService to set
	 */
	public void setMetricsService(MetricsService metricsService) {
		this.metricsService = metricsService;
	}
}
