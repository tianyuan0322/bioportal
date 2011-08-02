package org.ncbo.stanford.manager.metrics;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;

/**
 * An interface for all ontology metrics managers to conform to. This allows for
 * a seamless Spring injection.
 * 
 * @author Csongor Nyulas
 * @author Paul Alexander
 */
public interface OntologyMetricsManager {

	public static final int GOOD_DESIGN_SUBCLASS_LIMIT = 25;
	public final Integer CLASS_LIST_LIMIT = 201;
	public final String LIMIT_PASSED = "limitpassed:";
	/**
	 * Extracts ontology metrics of a given ontology version.
	 * 
	 * @param ontologyBean
	 * @return an ontology metrics bean filled in with metrics values
	 * @throws Exception
	 */
	public OntologyMetricsBean extractOntologyMetrics(OntologyBean ontologyBean)
			throws Exception;

}
