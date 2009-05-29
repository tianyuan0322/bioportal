package org.ncbo.stanford.manager.metadata.impl;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metadata.OntologyCategoryMetadataManager;
import org.ncbo.stanford.util.metadata.MetadataUtils;
import org.ncbo.stanford.util.metadata.OntologyCategoryMetadataUtils;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * Provides the functionality to deal with ontology category metadata
 * 
 * @author Csongor Nyulas
 * 
 */
public class OntologyCategoryMetadataManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyCategoryMetadataManager {

	private static final Log log = LogFactory
			.getLog(OntologyCategoryMetadataManagerProtegeImpl.class);

	private static final String CLASS_ONTOLOGY_DOMAIN = MetadataUtils.PREFIX_OMV + "OntologyDomain";

	private static final boolean CREATE_IF_MISSING = true;
	private static final boolean DO_NOT_CREATE_IF_MISSING = false;


	public void saveOntologyCategory(CategoryBean cb) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontDomainInd = getOntologyDomainInstance(metadata, cb.getId(), CREATE_IF_MISSING);

		//TODO check if we will ever deal with multiple inheritance
		OWLIndividual parentCat = getOntologyDomainInstance(metadata, cb.getParentId(), DO_NOT_CREATE_IF_MISSING);
		Collection<OWLIndividual> parentCatInd = Collections.singletonList(parentCat);

		OntologyCategoryMetadataUtils.fillInCategoryInstancePropertiesFromBean(ontDomainInd, cb, parentCatInd);
	}

	public CategoryBean findCategoryById(Integer categoryId) {
		OWLModel metadata = getMetadataOWLModel();

		OWLIndividual ontDomaimInd = getOntologyDomainInstance(metadata, categoryId, DO_NOT_CREATE_IF_MISSING);
		
		CategoryBean cb = new CategoryBean();
		try {
			OntologyCategoryMetadataUtils.fillInCategoryBeanFromInstance(cb, ontDomaimInd);
			return cb;
		} catch (Exception e) {
			return null;
		}
	}
	
	private OWLIndividual getOntologyDomainInstance(OWLModel metadata, int id, boolean createIfMissing) {
		String ontDomainInstName = getOntologyDomainIndividualName(id);
		OWLIndividual ontDomainInd = metadata.getOWLIndividual(ontDomainInstName);
		
		if (ontDomainInd == null && createIfMissing) {
			ontDomainInd = createOntologyDomainInstance(metadata, ontDomainInstName);
		}
		
		return ontDomainInd;
	}
	
	private OWLIndividual createOntologyDomainInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_ONTOLOGY_DOMAIN);

		return ontClass.createOWLIndividual(indName);
	}
	
}
