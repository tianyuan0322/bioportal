/**
 * 
 */
package org.ncbo.stanford.service.metrics.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.manager.metrics.OntologyMetricsManager;
import org.ncbo.stanford.service.metrics.MetricsService;
import org.ncbo.stanford.service.ontology.AbstractOntologyService;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * @author Paul Alexander
 */
public class MetricsServiceImpl extends AbstractOntologyService implements
		MetricsService {

	private static final Log log = LogFactory.getLog(MetricsServiceImpl.class);
	private OntologyMetadataManager ontologyMetadataManager;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	private Map<String, OntologyMetricsManager> ontologyMetricsHandlerMap = new HashMap<String, OntologyMetricsManager>(
			0);

	public OntologyMetricsBean getOntologyMetrics(OntologyBean ob)
			throws Exception {
		OWLModel metadata = ontologyMetadataManager.getMetadataOWLModel();
		OWLIndividual ontVerInd = ontologyMetadataManager
				.getOntologyOrViewInstance(metadata, ob.getId());

		if (ontVerInd == null) {
			throw new MetadataException(
					"Metadata for ontology "
							+ (ob.isView() ? "view" : "")
							+ ob.getId()
							+ " could not be updated with metrics because it could not be found!");
		}

		OntologyMetricsBean mb = new OntologyMetricsBean();
		OntologyMetadataUtils.fillInMetricsBeanFromInstance(mb, ontVerInd);

		return mb;
	}

	public void updateOntologyMetrics(OntologyBean ob, OntologyMetricsBean mb)
			throws Exception {
		OWLModel metadata = ontologyMetadataManager.getMetadataOWLModel();
		OWLIndividual ontVerInd = ontologyMetadataManager
				.getOntologyOrViewInstance(metadata, ob.getId());

		if (ontVerInd == null) {
			throw new MetadataException(
					"Metadata for ontology "
							+ (ob.isView() ? "view" : "")
							+ ob.getId()
							+ " could not be updated with metrics because it could not be found!");
		}

		if (ob.getId() != mb.getId()) {
			throw new MetadataException(
					"Trying to attach ontology metrics information from OntologyMetricsBean with id "
							+ mb.getId()
							+ " to ontology "
							+ (ob.isView() ? "view" : "")
							+ "version with id "
							+ ob.getId() + ". Invalid operation!");
		}

		OntologyMetadataUtils.fillInOntologyInstancePropertiesFromBean(
				ontVerInd, mb);
	}

	public void extractOntologyMetrics(List<Integer> ontologyVersionIds) {
		errorOntologies.clear();

		List<Integer> errorVersionIdList = new ArrayList<Integer>(
				ontologyVersionIds);

		for (Integer ontologyVersionId : ontologyVersionIds) {
			// TODO: It would be good to check for unparsed ontologies and
			// throw a custom exception.
			try {
				// find the OntologyBean from the list
				OntologyBean ontologyBean = ontologyMetadataManager
						.findOntologyOrViewVersionById(ontologyVersionId);

				if (ontologyBean == null) {
					continue;
				}

				errorVersionIdList.remove(ontologyVersionId);

				Long startCalc = System.currentTimeMillis();
				OntologyMetricsBean metricsBean = getMetricsManager(
						ontologyBean).extractOntologyMetrics(ontologyBean);
				log.info("Metrics calculated for '"
						+ ontologyBean.getDisplayLabel() + "' in ~"
						+ (System.currentTimeMillis() - startCalc) / 1000
						+ " seconds");

				updateOntologyMetrics(ontologyBean, metricsBean);
			} catch (Exception e) {
				addErrorOntology(errorOntologies, ontologyVersionId.toString(),
						null, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}

		// Check for non-existant ontology ids
		for (Integer errorVersionId : errorVersionIdList) {
			String error = addErrorOntology(errorOntologies, errorVersionId
					.toString(), null, ONTOLOGY_VERSION_DOES_NOT_EXIST_ERROR);
			log.error(error);
		}
	}

	private OntologyMetricsManager getMetricsManager(OntologyBean ontologyBean)
			throws Exception {
		String formatHandler = ontologyFormatHandlerMap.get(ontologyBean
				.getFormat());
		OntologyMetricsManager metricsManager = ontologyMetricsHandlerMap
				.get(formatHandler);

		if (metricsManager == null) {
			log.error("Cannot find metricsManager for "
					+ ontologyBean.getFormat());
			throw new InvalidOntologyFormatException(
					"Cannot find formatHandler for " + ontologyBean.getFormat());
		}

		return metricsManager;
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
	 * @param ontologyMetricsHandlerMap
	 *            the ontologyMetricsHandlerMap to set
	 */
	public void setOntologyMetricsHandlerMap(
			Map<String, OntologyMetricsManager> ontologyMetricsHandlerMap) {
		this.ontologyMetricsHandlerMap = ontologyMetricsHandlerMap;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

}
