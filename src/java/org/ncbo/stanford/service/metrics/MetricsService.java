package org.ncbo.stanford.service.metrics;

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
	 * @param ontologyId the versioned id for a given ontology
	 * @return the metrics bean corresponding to an ontology or view
	 * @throws Exception
	 */
	public OntologyMetricsBean getOntologyMetrics(OntologyBean ontologyBean)
			throws Exception;

}
