package org.ncbo.stanford.util.metadata;

import java.util.Collection;
import java.util.List;

import org.ncbo.stanford.bean.CategoryBean;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class OntologyCategoryMetadataUtils extends MetadataUtils {

	public static final String PROPERTY_OMV_IS_SUB_DOMAIN_OF = PREFIX_OMV + "isSubDomainOf";
	public static final String PROPERTY_OBO_FOUNDRY_NAME = PREFIX_METADATA + "oboFoundryName";

	private static final String PROPERTY_OBO_FOUNDRY_ID = OntologyMetadataUtils.PROPERTY_OBO_FOUNDRY_ID;
	private static final String CLASS_ONTOLOGY_DOMAIN = OntologyMetadataUtils.CLASS_OMV_ONTOLOGY_DOMAIN;

	public static void fillInCategoryInstancePropertiesFromBean(
			OWLIndividual ontologyInd, CategoryBean cb,
			Collection<OWLIndividual> parentCatIndividuals) throws Exception {
		if (ontologyInd == null || cb == null) {
			throw new Exception(
					"The method fillInCategoryInstancePropertiesFromBean can't take null arguments. Please make sure that both arguments are properly initialized.");
		}

		OWLModel owlModel = ontologyInd.getOWLModel();

		setPropertyValue(owlModel, ontologyInd, PROPERTY_ID, cb.getId());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NAME, cb.getName());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OBO_FOUNDRY_ID, cb
				.getOboFoundryName());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_IS_SUB_DOMAIN_OF,
				parentCatIndividuals);// TODO check this
	}

	public static void fillInCategoryBeanFromInstance(CategoryBean cb,
			OWLIndividual categoryInd) throws Exception {
		if (categoryInd == null || cb == null) {
			throw new Exception(
					"The method fillInCategoryBeanFromInstance can't take null arguments. Please make sure that both arguments are properly initialized.");
		}

		OWLModel owlModel = categoryInd.getOWLModel();

		cb.setId(getPropertyValue(owlModel, categoryInd, PROPERTY_ID,
				Integer.class));
		cb.setName(getPropertyValue(owlModel, categoryInd, PROPERTY_OMV_NAME,
				String.class));
		cb.setOboFoundryName(getPropertyValue(owlModel, categoryInd,
				PROPERTY_OBO_FOUNDRY_ID, String.class));
		Integer parentCatId = getFirstElement(getPropertyValueIds(owlModel,
				categoryInd, PROPERTY_OMV_IS_SUB_DOMAIN_OF));

		if (parentCatId != null) {
			cb.setParentId(parentCatId);
		} else {
			// TODO what to do?
			// throw Exception?
			// log.error("No OMV:isSubDomainOf individual found for category: "
			// + categoryInd);
		}
	}

	public static OWLIndividual getOntologyDomainWithId(OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_ONTOLOGY_DOMAIN, id, false);
	}


	public static List<OWLIndividual> getOntologyDomainWithOboFoundryName(OWLModel metadata, String oboFoundryName) {
		return getIndividualsWithMatchingProperty(metadata, CLASS_ONTOLOGY_DOMAIN,
				PROPERTY_OBO_FOUNDRY_NAME, oboFoundryName, false);
	}

}

