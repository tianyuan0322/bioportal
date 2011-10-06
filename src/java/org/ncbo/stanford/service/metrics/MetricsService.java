package org.ncbo.stanford.service.metrics;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;

/**
 * Provides access to high-level metrics methods for both Protege and LexGrid.
 * 
 * @author Paul Alexander
 */
public interface MetricsService {

	/**
	 * Updates the metadata of an ontology version specified by the ontologyBean
	 * with ontology metrics information specified by metricsBean.
	 * 
	 * @param ontologyBean
	 * @param metricsBean
	 * @throws Exception
	 */
	public void updateOntologyMetrics(OntologyBean ontologyBean,
			OntologyMetricsBean metricsBean) throws Exception;

	/**
	 * Returns the OntologyMetricsBean belonging to the ontology or view
	 * represented by the argument.
	 * 
	 * @param ontologyId
	 *            the versioned id for a given ontology
	 * @return the metrics bean corresponding to an ontology or view
	 * @throws Exception
	 */
	public OntologyMetricsBean getOntologyMetrics(OntologyBean ontologyBean)
			throws Exception;

	/**
	 * Returns all latest ontologies that have metrics calculated.
	 * 
	 * @return list of metrics objects for all latest ontologies that have
	 *         metrics calculated.
	 * @throws Exception
	 */
	public List<OntologyMetricsBean> getAllOntologyMetrics() throws Exception;

	/**
	 * Extract metrics from the given ontology and store them into the metadata
	 * ontology.
	 * 
	 * @param ontologyVersionIds
	 *            the list of ontologies to process.
	 */
	public void extractOntologyMetrics(List<Integer> ontologyVersionIds);

	/**
	 * Returns info about ontologies that did not process successfully
	 * 
	 * @return
	 */
	public List<String> getErrorOntologies();

}
