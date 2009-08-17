package org.ncbo.stanford.util.metadata;

import org.ncbo.stanford.bean.GroupBean;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class OntologyGroupMetadataUtils extends MetadataUtils {

	public static final String CLASS_ONTOLOGY_GROUP = PREFIX_METADATA + "OntologyGroup";

	public static final String PROPERTY_CONTAINS_ONTOLOGY = PREFIX_METADATA + "containsOntology";
	public static final String PROPERTY_BELONGS_TO_GROUP = PREFIX_METADATA + "belongsToGroup";
	
	private static final String PROPERTY_URL_HOMEPAGE = OntologyMetadataUtils.PROPERTY_URL_HOMEPAGE;

	public static void fillInGroupInstancePropertiesFromBean(
			OWLIndividual ontologyInd, GroupBean gb) throws Exception {
		if (ontologyInd == null || gb == null) {
			throw new Exception(
					"The method fillInGroupInstancePropertiesFromBean can't take null arguments. Please make sure that both arguments are properly initialized.");
		}

		OWLModel owlModel = ontologyInd.getOWLModel();

		setPropertyValue(owlModel, ontologyInd, PROPERTY_ID, gb.getId());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NAME, gb.getName());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_ACRONYM, gb.getAcronym());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DESCRIPTION, gb.getDescription());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_URL_HOMEPAGE, gb.getUrlHomepage());
	}

	public static void fillInGroupBeanFromInstance(GroupBean gb,
			OWLIndividual ontGroupInd) throws Exception {
		if (ontGroupInd == null || gb == null) {
			throw new Exception(
					"The method fillInGroupBeanFromInstance can't take null arguments. Please make sure that both arguments are properly initialized.");
		}

		OWLModel owlModel = ontGroupInd.getOWLModel();

		gb.setId(getPropertyValue(owlModel, ontGroupInd, PROPERTY_ID,
				Integer.class));
		gb.setName(getPropertyValue(owlModel, ontGroupInd, PROPERTY_OMV_NAME,
				String.class));
		gb.setAcronym(getPropertyValue(owlModel, ontGroupInd, PROPERTY_OMV_ACRONYM,
				String.class));
		gb.setDescription(getPropertyValue(owlModel, ontGroupInd, PROPERTY_OMV_DESCRIPTION,
				String.class));
		gb.setUrlHomepage(getPropertyValue(owlModel, ontGroupInd, PROPERTY_URL_HOMEPAGE,
				String.class));
	}
	

	public static OWLIndividual getOntologyGroupWithId(OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_ONTOLOGY_GROUP, id, false);
	}

}
