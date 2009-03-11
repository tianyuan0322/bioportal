package org.ncbo.stanford.manager.metadata.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metadata.OntologyCategoryMetadataManager;
import org.ncbo.stanford.manager.metadata.UserMetadataManager;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * Provides the functionality to deal with user metadata
 * 
 * @author Csongor Nyulas
 * 
 */
public class OntologyCategoryMetadataManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyCategoryMetadataManager {

	private static final Log log = LogFactory
			.getLog(OntologyCategoryMetadataManagerProtegeImpl.class);

	private static final String CLASS_ONTOLOGY_DOMAIN = "OMV:OntologyDomain";


	public void saveOntologyCategory(CategoryBean categoryBean) throws Exception {
		//TODO continue here
	}
	
	private OWLIndividual getOntologyDomainInstance(OWLModel metadata, int id) {
		String ontDomainInstName = getOntologyDomainIndividualName(id);
		OWLIndividual ontDomainInd = metadata.getOWLIndividual(ontDomainInstName);
		if (ontDomainInd == null) {
			ontDomainInd = createOntologyDomainInstance(metadata, ontDomainInstName);
		}
		return ontDomainInd;
	}
	
	private OWLIndividual createOntologyDomainInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_ONTOLOGY_DOMAIN);
		return ontClass.createOWLIndividual(indName);
	}
	
}
