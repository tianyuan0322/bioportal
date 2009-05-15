package org.ncbo.stanford.util.metadata;

import java.util.Collection;

import org.ncbo.stanford.bean.CategoryBean;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class OntologyCategoryMetadataUtils extends MetadataUtils {

//	private static final String CLASS_OMV_ONTOLOGY_LANGUAGE = PREFIX_OMV + "OntologyLanguage";
	
	//private static final String PROPERTY_ID = PREFIX_METADATA + "id";
	private static final String PROPERTY_OBO_FOUNDRY_ID = PREFIX_METADATA + "oboFoundryID";
	private static final String PROPERTY_OMV_IS_SUB_DOMAIN_OF = PREFIX_OMV + "isSubDomainOf";
	private static final String PROPERTY_OMV_NAME = PREFIX_OMV + "name";

	
	public static void fillInCategoryInstancePropertiesFromBean(OWLIndividual ontologyInd,
			CategoryBean cb, Collection<OWLIndividual> parentCatIndividuals) throws Exception {
		
		if (ontologyInd == null || cb == null) {
			throw new Exception("The method fillInCategoryInstancePropertiesFromBean can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = ontologyInd.getOWLModel();
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_ID, cb.getId());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NAME, cb.getName());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OBO_FOUNDRY_ID, cb.getOboFoundryName());
		
		//cb.getParentId();
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_IS_SUB_DOMAIN_OF, parentCatIndividuals);//TODO check this
	}

	
	public static void fillInCategoryBeanFromInstance(CategoryBean cb,
			OWLIndividual categoryInd) throws Exception {
		
		if (categoryInd == null || cb == null) {
			throw new Exception("The method fillInCategoryBeanFromInstance can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = categoryInd.getOWLModel();
		
		cb.setId( getPropertyValue(owlModel, categoryInd, PROPERTY_ID, Integer.class));
		cb.setName( getPropertyValue(owlModel, categoryInd, PROPERTY_OMV_NAME, String.class));
		cb.setOboFoundryName( getPropertyValue(owlModel, categoryInd, PROPERTY_OBO_FOUNDRY_ID, String.class));
		Integer parentCatId = getFirstElement(getPropertyValueIds(owlModel, categoryInd, PROPERTY_OMV_IS_SUB_DOMAIN_OF));
		if (parentCatId != null) {
			cb.setParentId( parentCatId);
		}
		else {
			// TODO what to do?
			// throw Exception?
			// log.error("No OMV:isSubDomainOf individual found for category: " + categoryInd);
		}
		
	}

	
	
//	private static OWLIndividual getOntologyLanguageInstance(OWLModel metadata,
//			String format) {
//		OWLNamedClass ontLangClass = metadata.getOWLNamedClass(CLASS_OMV_ONTOLOGY_LANGUAGE);
//		RDFProperty nameProp = metadata.getRDFProperty(PROPERTY_OMV_NAME);
//		RDFProperty labelProp = metadata.getRDFProperty(PROPERTY_RDFS_LABEL);
//		
//		Collection<?> matchingResources = metadata.getMatchingResources(nameProp, format, -1);
//		OWLIndividual matchingInd = getIndividualWithType(matchingResources, ontLangClass);
//		if (matchingInd != null) {
//			return matchingInd;
//		}
//		
//		//metadata.getFramesWithValue("rdfs:label", null, false, format);
//		//metadata.getMatchingFrames(labelProp, null, false, format, -1);
//		matchingResources = metadata.getMatchingResources(labelProp, format, -1);
//		matchingInd = getIndividualWithType(matchingResources, ontLangClass);
//		if (matchingInd != null) {
//			return matchingInd;
//		}
//		
//		return metadata.getOWLIndividual(format);
//	}
//
//	private static OWLIndividual getIndividualWithType(Collection<?> matchingResources, OWLClass type) {
//		for (Object matchingRes : matchingResources) {
//			if (matchingRes instanceof OWLIndividual) {
//				OWLIndividual res = (OWLIndividual) matchingRes;
//				if (res.hasRDFType(type)) {
//					return res;
//				}
//			}
//		}
//		
//		return null;
//	}
//
//	private static String getOntologyFormatValue(OWLModel metadata, OWLIndividual ontologyLanguageInd) throws Exception {
//		if (ontologyLanguageInd == null) {
//			return null;
//		}
//		String res;
//		res = getPropertyValue(metadata, ontologyLanguageInd, PROPERTY_OMV_NAME, String.class);
//		if (res != null) {
//			return res;
//		}
//		
////		res = getPropertyValue(metadata, ontologyLanguageInd, PROPERTY_RDFS_LABEL, String.class);
////		if (res != null) {
////			return res;
////		}
//		Collection<String> labels = ontologyLanguageInd.getLabels();
//		if (labels != null && labels.size()>0) {
//			res = labels.iterator().next();
//			return res;
//		}
//		
//		return ontologyLanguageInd.getLocalName();
//	}
	
}
