/**
 * 
 */
package org.ncbo.stanford.service.metrics.impl;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.service.metrics.MetricsService;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * @author Paul Alexander
 */
public class MetricsServiceImpl implements MetricsService {
	
	private OntologyMetadataManager ontologyMetadataManager;

	public OntologyMetricsBean getOntologyMetrics(OntologyBean ob)
			throws Exception {
		OWLModel metadata = ontologyMetadataManager.getMetadataOWLModel();
		OWLIndividual ontVerInd = ontologyMetadataManager.getOntologyOrViewInstance(metadata, ob
				.getId());
		
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
		OWLIndividual ontVerInd = ontologyMetadataManager.getOntologyOrViewInstance(metadata, ob
				.getId());
		
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

	/**
	 * @param ontologyMetadataManager the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}

}
