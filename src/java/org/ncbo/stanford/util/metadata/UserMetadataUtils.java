package org.ncbo.stanford.util.metadata;

import java.util.Collection;
import java.util.Date;

import org.ncbo.stanford.bean.UserBean;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

public class UserMetadataUtils extends MetadataUtils {
	
	private static final String PROPERTY_HAS_ROLES = PREFIX_METADATA + "hasRoles";
	//private static final String PROPERTY_ID = PREFIX_METADATA + "id";
	//private static final String PROPERTY_OMV_DESCRIPTION = PREFIX_OMV + "description";
	private static final String PROPERTY_OMV_EMAIL = PREFIX_OMV + "eMail";
	private static final String PROPERTY_OMV_FIRST_NAME = PREFIX_OMV + "firstName";
	private static final String PROPERTY_OMV_LAST_NAME = PREFIX_OMV + "lastName";
	//private static final String PROPERTY_OMV_NAME = PREFIX_OMV + "name";
	private static final String PROPERTY_OMV_PHONE_NUMBER = PREFIX_OMV + "phoneNumber";
	private static final String PROPERTY_PASSWORD = PREFIX_METADATA + "password";
	private static final String PROPERTY_TIMESTAMP_CREATION = PREFIX_METADATA + "timestampCreation";
	private static final String PROPERTY_USERNAME = PREFIX_METADATA + "username";

	
	public static void fillInUserInstancePropertiesFromBean(OWLIndividual userInd,
			UserBean ub, Collection<OWLIndividual> rolesIndividuals) throws Exception {
		
		if (userInd == null || ub == null) {
			throw new Exception("The method fillInUserInstancePropertiesFromBean can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = userInd.getOWLModel();
		
		RDFSLiteral litDateCreated = owlModel.createRDFSLiteral(ub.getDateCreated().toString(), owlModel.getXSDdateTime());
		setPropertyValue(owlModel, userInd, PROPERTY_TIMESTAMP_CREATION, litDateCreated);
		setPropertyValue(owlModel, userInd, PROPERTY_OMV_EMAIL, ub.getEmail());
		setPropertyValue(owlModel, userInd, PROPERTY_OMV_FIRST_NAME, ub.getFirstname());
		setPropertyValue(owlModel, userInd, PROPERTY_ID, ub.getId());
		setPropertyValue(owlModel, userInd, PROPERTY_OMV_LAST_NAME, ub.getLastname());
		setPropertyValue(owlModel, userInd, PROPERTY_PASSWORD, ub.getPassword());
		setPropertyValue(owlModel, userInd, PROPERTY_OMV_PHONE_NUMBER, ub.getPhone());
		//ub.getRoles();
		setPropertyValue(owlModel, userInd, PROPERTY_HAS_ROLES, rolesIndividuals);//TODO check this
		setPropertyValue(owlModel, userInd, PROPERTY_USERNAME, ub.getUsername());
	}

	
	public static void fillInUserBeanFromInstance(UserBean ub,
			OWLIndividual userInd) throws Exception {
		
		if (userInd == null || ub == null) {
			throw new Exception("The method fillInUserBeanFromInstance can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = userInd.getOWLModel();
		
		ub.setDateCreated(getPropertyValue(owlModel, userInd, PROPERTY_TIMESTAMP_CREATION, Date.class));
		ub.setEmail( getPropertyValue(owlModel, userInd, PROPERTY_OMV_EMAIL, String.class));
		ub.setFirstname( getPropertyValue(owlModel, userInd, PROPERTY_OMV_FIRST_NAME, String.class));
		ub.setId( getPropertyValue(owlModel, userInd, PROPERTY_ID, Integer.class));
		ub.setLastname( getPropertyValue(owlModel, userInd, PROPERTY_OMV_LAST_NAME, String.class));
		ub.setPassword( getPropertyValue(owlModel, userInd, PROPERTY_PASSWORD, String.class));
		ub.setPhone( getPropertyValue(owlModel, userInd, PROPERTY_OMV_PHONE_NUMBER, String.class));
		//ub.setRoles(List<String>);
		//TODO uncomment it when property is added to ontology
		
		ub.setUsername( getPropertyValue(owlModel, userInd, PROPERTY_USERNAME, String.class));	
	}

	public static void fillInUserRoleInstancePropertiesWithValues(OWLIndividual roleInd,
			Integer id, String name, String description) throws Exception {
		
		if (roleInd == null || id == null || name == null) {
			throw new Exception("The method fillInUserRoleInstancePropertiesWithValues can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = roleInd.getOWLModel();
		
		setPropertyValue(owlModel, roleInd, PROPERTY_ID, id);
		setPropertyValue(owlModel, roleInd, PROPERTY_OMV_NAME, name);
		setPropertyValue(owlModel, roleInd, PROPERTY_OMV_DESCRIPTION, description);
	}

	public static String getUserRoleName(OWLIndividual roleInd) throws Exception {
		return getPropertyValue(roleInd.getOWLModel(), roleInd, PROPERTY_OMV_NAME, String.class);
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
