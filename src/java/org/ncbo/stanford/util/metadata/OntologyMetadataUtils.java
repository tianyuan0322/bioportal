package org.ncbo.stanford.util.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;

import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

public class OntologyMetadataUtils {

	private static final String PREFIX_OMV = "OMV:";
	private static final String PREFIX_METADATA = "metadata:";
	
	private static final String CLASS_OMV_ONTOLOGY_LANGUAGE = PREFIX_OMV + "OntologyLanguage";
	
	private static final String PROPERTY_ADMINISTERED_BY = PREFIX_METADATA + "administeredBy";
	private static final String PROPERTY_CODING_SCHEME = PREFIX_METADATA + "codingScheme";
	private static final String PROPERTY_FILE_NAMES = PREFIX_METADATA + "fileNames";
	private static final String PROPERTY_FILE_PATH = PREFIX_METADATA + "filePath";
	private static final String PROPERTY_HAS_CONTACT_EMAIL = PREFIX_METADATA + "hasContactEmail";
	private static final String PROPERTY_HAS_CONTACT_NAME = PREFIX_METADATA + "hasContactName";
	 public static final String PROPERTY_ID = PREFIX_METADATA + "id";
	private static final String PROPERTY_INTERNAL_VERSION_NUMBER = PREFIX_METADATA + "internalVersionNumber";
	private static final String PROPERTY_IS_FOUNDRY = PREFIX_METADATA + "isFoundry";
	private static final String PROPERTY_IS_MANUAL = PREFIX_METADATA + "isManual";
	private static final String PROPERTY_IS_REMOTE = PREFIX_METADATA + "isRemote";
	private static final String PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY = PREFIX_METADATA + "isVersionOfVirtualOntology";
	private static final String PROPERTY_OBO_FOUNDRY_ID = PREFIX_METADATA + "oboFoundryID";
	private static final String PROPERTY_OMV_ACRONYM = PREFIX_OMV + "acronym";
	private static final String PROPERTY_OMV_CREATION_DATE = PREFIX_OMV + "creationDate";
	private static final String PROPERTY_OMV_DESCRIPTION = PREFIX_OMV + "description";
	private static final String PROPERTY_OMV_DOCUMENTATION = PREFIX_OMV + "documentation";
	private static final String PROPERTY_OMV_HAS_DOMAIN = PREFIX_OMV + "hasDomain";
	private static final String PROPERTY_OMV_HAS_ONTOLOGY_LANGUAGE = PREFIX_OMV + "hasOntologyLanguage";
	 public static final String PROPERTY_OMV_NAME = PREFIX_OMV + "name";
	private static final String PROPERTY_OMV_STATUS = PREFIX_OMV + "status";
	private static final String PROPERTY_OMV_URI = PREFIX_OMV + "URI";
	private static final String PROPERTY_OMV_VERSION = PREFIX_OMV + "version";
	private static final String PROPERTY_PREFERRED_NAME_PROPERTY = PREFIX_METADATA + "preferredNameProperty";
	private static final String PROPERTY_STATUS_ID = PREFIX_METADATA + "statusID";
	private static final String PROPERTY_SYNONYM_PROPERTY = PREFIX_METADATA + "synonymProperty";
	private static final String PROPERTY_TARGET_TERMINOLOGIES = PREFIX_METADATA + "targetTerminologies";
	private static final String PROPERTY_UPLOAD_DATE = PREFIX_METADATA + "uploadDate";
	private static final String PROPERTY_URL_HOMEPAGE = PREFIX_METADATA + "urlHomepage";
	private static final String PROPERTY_URL_PUBLICATIONS = PREFIX_METADATA + "urlPublications";

	
	private static final String PROPERTY_CURRENT_VERSION = PREFIX_METADATA + "currentVersion";

	
	private static final String PROPERTY_RDFS_LABEL = "rdfs:label";

	
	public static void fillInOntologyInstancePropertiesFromBean(OWLIndividual ontologyInd,
			OntologyBean ob, OWLIndividual vOntInd, OWLIndividual userInd, 
			Collection<OWLIndividual> domainIndividuals) throws Exception {
		
		if (ontologyInd == null || ob == null) {
			throw new Exception("The method fillInInstancePropertiesFromBean can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = ontologyInd.getOWLModel();
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_ACRONYM, ob.getAbbreviation());
		
		//ob.getCategoryIds();
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_HAS_DOMAIN, domainIndividuals);
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_CODING_SCHEME, ob.getCodingScheme());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_EMAIL, ob.getContactEmail());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_NAME, ob.getContactName());
		//owlModel.createRDFSLiteral(ob.getDateCreated(), PROPERTY_UPLOAD_DATE);
		RDFSLiteral litDateCreated = owlModel.createRDFSLiteral(ob.getDateCreated().toString(), owlModel.getXSDdate());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_UPLOAD_DATE, litDateCreated);	//TODO check this for correct type conversion
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_CREATION_DATE, ob.getDateReleased().toString());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NAME, ob.getDisplayLabel());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DESCRIPTION, ob.getDescription());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DOCUMENTATION, ob.getDocumentation());
		
		//ob.getFileItem();
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_FILE_NAMES, ob.getFilenames());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_FILE_PATH, ob.getFilePath());
		OWLIndividual ontLangInd = getOntologyLanguageInstance(owlModel, ob.getFormat());
		if (ontLangInd != null) {
			setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_HAS_ONTOLOGY_LANGUAGE, ontLangInd);
		}
		else {
			//TODO what to do?
			//throw Exception?
			//log.error("No OMV:OntologyLanguage individual found for format: " + format);
		}
		setPropertyValue(owlModel, ontologyInd, PROPERTY_URL_HOMEPAGE, ob.getHomepage());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_ID, ob.getId());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_INTERNAL_VERSION_NUMBER, ob.getInternalVersionNumber());
		RDFSLiteral litIsFoundry = owlModel.createRDFSLiteral(ob.getIsFoundry()==0 ? "false" : "true", owlModel.getXSDboolean());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_IS_FOUNDRY, litIsFoundry); //TODO check this for correct type conversion
		RDFSLiteral litIsManual = owlModel.createRDFSLiteral(ob.getIsManual()==0 ? "false" : "true", owlModel.getXSDboolean());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_IS_MANUAL, litIsManual);
		RDFSLiteral litIsRemote = owlModel.createRDFSLiteral(ob.getIsRemote()==0 ? "false" : "true", owlModel.getXSDboolean());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_IS_REMOTE, litIsRemote);
		
		//ob.getIsReviewed();
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OBO_FOUNDRY_ID, ob.getOboFoundryId());
		
		//ob.getOntologyId();
		setPropertyValue(owlModel, ontologyInd, PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY, vOntInd);

		setPropertyValue(owlModel, ontologyInd, PROPERTY_PREFERRED_NAME_PROPERTY, ob.getPreferredNameSlot());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_URL_PUBLICATIONS, ob.getPublication());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_STATUS_ID, ob.getStatusId());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_SYNONYM_PROPERTY, ob.getSynonymSlot());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_TARGET_TERMINOLOGIES, ob.getTargetTerminologies());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_URI, ob.getUrn());
		
		//ob.getUserId();
		setPropertyValue(owlModel, ontologyInd, PROPERTY_ADMINISTERED_BY, userInd);
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_VERSION, ob.getVersionNumber());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_STATUS, ob.getVersionStatus());
	}

	
	public static void fillInOntologyBeanFromInstance(OntologyBean ob,
			OWLIndividual ontologyInd) throws Exception {
		
		if (ontologyInd == null || ob == null) {
			throw new Exception("The method fillInOntologyBeanFromInstance can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = ontologyInd.getOWLModel();
		
		ob.setAbbreviation( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_ACRONYM, String.class));
		ob.setCategoryIds( getPropertyValueIds(owlModel, ontologyInd, PROPERTY_OMV_HAS_DOMAIN));
		ob.setCodingScheme( getPropertyValue(owlModel, ontologyInd, PROPERTY_CODING_SCHEME, String.class));
		ob.setContactEmail( getPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_EMAIL, String.class));
		ob.setContactName( getPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_NAME, String.class));
		ob.setDateCreated( getPropertyValue(owlModel, ontologyInd, PROPERTY_UPLOAD_DATE, Date.class));
		ob.setDateReleased( new Date(Date.parse(getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_CREATION_DATE, String.class))));
		ob.setDisplayLabel( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NAME, String.class));
		ob.setDescription( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DESCRIPTION, String.class));
		ob.setDocumentation( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DOCUMENTATION, String.class));
		
		//ob.setFileItem(null);
		
		ob.setFilenames( getPropertyValues(owlModel, ontologyInd, PROPERTY_FILE_NAMES, String.class));
		ob.setFilePath( getPropertyValue(owlModel, ontologyInd, PROPERTY_FILE_PATH, String.class));
		ob.setFormat( getOntologyFormatValue(
				owlModel, getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_HAS_ONTOLOGY_LANGUAGE, OWLIndividual.class)) );
		ob.setHomepage( getPropertyValue(owlModel, ontologyInd, PROPERTY_URL_HOMEPAGE, String.class));
		ob.setId( getPropertyValue(owlModel, ontologyInd, PROPERTY_ID, Integer.class));
		ob.setInternalVersionNumber( getPropertyValue(owlModel, ontologyInd, PROPERTY_INTERNAL_VERSION_NUMBER, Integer.class));
		ob.setIsFoundry( convertBooleanToByte(getPropertyValue(owlModel, ontologyInd, PROPERTY_IS_FOUNDRY, Boolean.class)) );
		ob.setIsManual( convertBooleanToByte(getPropertyValue(owlModel, ontologyInd, PROPERTY_IS_MANUAL, Boolean.class)) );
		ob.setIsRemote( convertBooleanToByte(getPropertyValue(owlModel, ontologyInd, PROPERTY_IS_REMOTE, Boolean.class)) );
		
		//ob.setIsReviewed(null);
		
		ob.setOboFoundryId( getPropertyValue(owlModel, ontologyInd, PROPERTY_OBO_FOUNDRY_ID, String.class));
		Integer virtOntId = getFirstElement(getPropertyValueIds(owlModel, ontologyInd, PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY));
		if (virtOntId != null) {
			ob.setOntologyId( virtOntId);
		}
		else {
			// TODO what to do?
			// throw Exception?
			// log.error("No OMV:OntologyLanguage individual found for format: " + format);
		}
		ob.setPreferredNameSlot( getPropertyValue(owlModel, ontologyInd, PROPERTY_PREFERRED_NAME_PROPERTY, String.class));
		ob.setPublication( getPropertyValue(owlModel, ontologyInd, PROPERTY_URL_PUBLICATIONS, String.class));
		ob.setStatusId( getPropertyValue(owlModel, ontologyInd, PROPERTY_STATUS_ID, Integer.class));
		ob.setSynonymSlot( getPropertyValue(owlModel, ontologyInd, PROPERTY_SYNONYM_PROPERTY, String.class));
		ob.setTargetTerminologies( getPropertyValue(owlModel, ontologyInd, PROPERTY_TARGET_TERMINOLOGIES, String.class));
		ob.setUrn( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_URI, String.class));
		//TODO temporary solution, until multiple administrators will be allowed:
		ob.setUserId( getFirstElement(getPropertyValueIds(owlModel, ontologyInd, PROPERTY_ADMINISTERED_BY)) );
		ob.setVersionNumber( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_VERSION, String.class));
		ob.setVersionStatus( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_STATUS, String.class));
	}

	
	private static void setPropertyValue(OWLModel owlModel, OWLIndividual owlInd, String propName, Object value) throws Exception {
		OWLProperty prop = owlModel.getOWLProperty(propName);
		if (prop == null) {
			throw new Exception("Metadata ontology does not contain property " + propName);
		}
		if (value instanceof Collection && ((Collection)value).isEmpty()) {
			value = null;
		}
		owlInd.setPropertyValue(prop, value);
	}
	
	private static <T> T getPropertyValue(OWLModel owlModel, OWLIndividual owlInd, String propName, Class<T> type) throws Exception {
		OWLProperty prop = owlModel.getOWLProperty(propName);
		if (prop == null) {
			throw new Exception("Metadata ontology does not contain property " + propName);
		}
		Collection<?> propVals = owlInd.getPropertyValues(prop);
		if (propVals==null || propVals.size() == 0) {
			return null;
		} else if (propVals.size() == 1) {
			Object val = propVals.iterator().next();
			if (val instanceof RDFSLiteral) {
				RDFSLiteral lit = (RDFSLiteral)val;
				if (lit.getPlainValue() != null) {
					val = lit.getPlainValue();
				}
				else {
					//try to extract some non-standard values like date, time, datetime, etc.
					RDFSDatatype datatype = lit.getDatatype();
					if (owlModel.getXSDdate().equals(datatype) ||
							owlModel.getXSDtime().equals(datatype) ||
							owlModel.getXSDdateTime().equals(datatype) ) {
						val = new Date(Date.parse(lit.getString()));
					}
				}
			}
			return (T)val;
		}
		else {
			throw new RuntimeException("Multiple values attached to individual: " + owlInd.getLocalName() + " for property: " + propName);
		}
	}
	
	private static <T> List<T> getPropertyValues(OWLModel owlModel, OWLIndividual owlInd, String propName, Class<T> type) throws Exception {
		OWLProperty prop = owlModel.getOWLProperty(propName);
		if (prop == null) {
			throw new Exception("Metadata ontology does not contain property " + propName);
		}
		Collection<?> propVals = owlInd.getPropertyValues(prop);
		if (propVals != null && propVals.isEmpty()) {
			return null;
		}
		else {
			List<T> res = new ArrayList<T>();
			for (Object propVal : propVals) {
				res.add( (T)propVal );
			}
			return res;
		}
	}

	private static List<Integer> getPropertyValueIds(OWLModel owlModel,
			OWLIndividual owlInd, String propName) throws Exception {
		Collection<?> propVals = getPropertyValues(owlModel, owlInd, propName, Object.class);
		if (propVals == null) {
			return null;
		} else {
			List<Integer> idList = new ArrayList<Integer>();
			for (Object propVal : propVals) {
				if (propVal instanceof OWLIndividual) {
					OWLIndividual ind = (OWLIndividual) propVal;
					idList.add((Integer) getPropertyValue(owlModel, ind,
							PROPERTY_ID, Integer.class));
				} else {
					// TODO what to do?
					// throw Exception?
					// log.error("No OMV:OntologyLanguage individual found for format: " + format);
				}
			}
			return idList;
		}
	}
	
	private static <T> T getFirstElement(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		else {
			return list.iterator().next();
		}
	}

	private static Byte convertBooleanToByte(Boolean boolValue) {
		if (boolValue == null) {
			return null;
		}
		else {
			 return (boolValue ? (byte)1 : (byte)0);
		}
	}

	private static OWLIndividual getOntologyLanguageInstance(OWLModel metadata,
			String format) {
		OWLNamedClass ontLangClass = metadata.getOWLNamedClass(CLASS_OMV_ONTOLOGY_LANGUAGE);
		RDFProperty nameProp = metadata.getRDFProperty(PROPERTY_OMV_NAME);
		RDFProperty labelProp = metadata.getRDFProperty(PROPERTY_RDFS_LABEL);
		
		Collection<?> matchingResources = metadata.getMatchingResources(nameProp, format, -1);
		OWLIndividual matchingInd = getIndividualWithType(matchingResources, ontLangClass);
		if (matchingInd != null) {
			return matchingInd;
		}
		
		//metadata.getFramesWithValue("rdfs:label", null, false, format);
		//metadata.getMatchingFrames(labelProp, null, false, format, -1);
		matchingResources = metadata.getMatchingResources(labelProp, format, -1);
		matchingInd = getIndividualWithType(matchingResources, ontLangClass);
		if (matchingInd != null) {
			return matchingInd;
		}
		
		return metadata.getOWLIndividual(format);
	}

	private static OWLIndividual getIndividualWithType(Collection<?> matchingResources, OWLClass type) {
		for (Object matchingRes : matchingResources) {
			if (matchingRes instanceof OWLIndividual) {
				OWLIndividual res = (OWLIndividual) matchingRes;
				if (res.hasRDFType(type)) {
					return res;
				}
			}
		}
		
		return null;
	}

	private static String getOntologyFormatValue(OWLModel metadata, OWLIndividual ontologyLanguageInd) throws Exception {
		if (ontologyLanguageInd == null) {
			return null;
		}
		String res;
		res = getPropertyValue(metadata, ontologyLanguageInd, PROPERTY_OMV_NAME, String.class);
		if (res != null) {
			return res;
		}
		
//		res = getPropertyValue(metadata, ontologyLanguageInd, PROPERTY_RDFS_LABEL, String.class);
//		if (res != null) {
//			return res;
//		}
		Collection<String> labels = ontologyLanguageInd.getLabels();
		if (labels != null && labels.size()>0) {
			res = labels.iterator().next();
			return res;
		}
		
		return ontologyLanguageInd.getLocalName();
	}
	
	public static void setLatestVersion(OWLIndividual virtualOntologyInd,
			OWLIndividual ontologyInd) throws Exception {
		OWLModel owlModel = virtualOntologyInd.getOWLModel();
		setPropertyValue(owlModel, virtualOntologyInd, PROPERTY_CURRENT_VERSION, ontologyInd);
	}
	
}
