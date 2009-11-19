package org.ncbo.stanford.manager.metadata.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.manager.AbstractOntologyMetadataManager;
import org.ncbo.stanford.manager.metadata.OntologyCategoryMetadataManager;
import org.ncbo.stanford.util.metadata.OntologyCategoryMetadataUtils;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * Provides the functionality to deal with ontology category metadata
 * 
 * @author Csongor Nyulas
 * 
 */
public class OntologyCategoryMetadataManagerImpl extends
		AbstractOntologyMetadataManager implements OntologyCategoryMetadataManager {

	private static final Log log = LogFactory
			.getLog(OntologyCategoryMetadataManagerImpl.class);

	private static final boolean CREATE_IF_MISSING = true;
	private static final boolean DO_NOT_CREATE_IF_MISSING = false;


	@SuppressWarnings("unchecked")
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
			log.error("Ontology domain with category id " + categoryId + " was not found.");
			return null;
		}
	}

	public List<CategoryBean> findCategoriesByOBOFoundryNames(
			String[] oboFoundryNames) {
		List<CategoryBean> res = new ArrayList<CategoryBean>();
		//for (int i = 0; i <obo)
		for (String oboFoundryName : oboFoundryNames) {
			List<OWLIndividual> matchingOntDomains = OntologyCategoryMetadataUtils.getOntologyDomainWithOboFoundryName(
					getMetadataOWLModel(), oboFoundryName);
			if (matchingOntDomains != null) {
				//matchingDomains should contain only one instance
				for (OWLIndividual ontDomainInd : matchingOntDomains) {
					CategoryBean cb = new CategoryBean();
					try {
						OntologyCategoryMetadataUtils.fillInCategoryBeanFromInstance(cb, ontDomainInd);
						res.add(cb);
					} catch (Exception e) {
						log.error("Error in filling in CategoryBean from OntologyDomain instance " + ontDomainInd);
					}
				}
			}
		}
		return res;
	}

	public List<CategoryBean> findAllCategories() {
		OWLModel metadata = getMetadataOWLModel();
		
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllCategoryIDs(metadata);
		List<CategoryBean> res = new ArrayList<CategoryBean>();
		
		for (Integer id : ontologyIds) {
			res.add(findCategoryById(id));
		}
		
		return res;
	}
	
}
