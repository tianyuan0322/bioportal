package org.ncbo.stanford.util.metadata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Expression;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.util.MessageUtils;

import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.ui.widget.OWLDateWidget;

public class OntologyMetadataUtils extends MetadataUtils {

	private static final Log log = LogFactory.getLog(OntologyMetadataUtils.class);

	private static final String CLASS_OMV_ONTOLOGY = PREFIX_OMV + "Ontology";
	private static final String CLASS_OMV_ONTOLOGY_DOMAIN = PREFIX_OMV + "OntologyDomain";
	private static final String CLASS_OMV_ONTOLOGY_LANGUAGE = PREFIX_OMV + "OntologyLanguage";
	private static final String CLASS_VIRTUAL_ONTOLOGY = PREFIX_METADATA + "VirtualOntology";
	
	private static final String CLASS_ONTOLOGY_VIEW = PREFIX_METADATA + "OntologyView";
	private static final String CLASS_VIEW_DEFINITION_LANGUAGE = PREFIX_METADATA + "ViewDefinitionLanguage";
	private static final String CLASS_VIEW_GENERATION_ENGINE = PREFIX_METADATA + "ViewGenerationEngine";
	private static final String CLASS_VIRTUAL_VIEW = PREFIX_METADATA + "VirtualView";
	
	private static final String PROPERTY_ADMINISTERED_BY = PREFIX_METADATA + "administeredBy";
	private static final String PROPERTY_CODING_SCHEME = PREFIX_METADATA + "codingScheme";
	private static final String PROPERTY_FILE_NAMES = PREFIX_METADATA + "fileNames";
	private static final String PROPERTY_FILE_PATH = PREFIX_METADATA + "filePath";
	private static final String PROPERTY_HAS_CONTACT_EMAIL = PREFIX_METADATA + "hasContactEmail";
	private static final String PROPERTY_HAS_CONTACT_NAME = PREFIX_METADATA + "hasContactName";
	//private static final String PROPERTY_ID = PREFIX_METADATA + "id";
	private static final String PROPERTY_INTERNAL_VERSION_NUMBER = PREFIX_METADATA + "internalVersionNumber";
	private static final String PROPERTY_IS_FOUNDRY = PREFIX_METADATA + "isFoundry";
	private static final String PROPERTY_IS_MANUAL = PREFIX_METADATA + "isManual";
	private static final String PROPERTY_IS_REMOTE = PREFIX_METADATA + "isRemote";
	private static final String PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY = PREFIX_METADATA + "isVersionOfVirtualOntology";
	private static final String PROPERTY_OBO_FOUNDRY_ID = PREFIX_METADATA + "oboFoundryID";
	private static final String PROPERTY_OMV_ACRONYM = PREFIX_OMV + "acronym";
	private static final String PROPERTY_OMV_CREATION_DATE = PREFIX_OMV + "creationDate";
	//private static final String PROPERTY_OMV_DESCRIPTION = PREFIX_OMV + "description";
	private static final String PROPERTY_OMV_DOCUMENTATION = PREFIX_OMV + "documentation";
	private static final String PROPERTY_OMV_HAS_DOMAIN = PREFIX_OMV + "hasDomain";
	private static final String PROPERTY_OMV_HAS_ONTOLOGY_LANGUAGE = PREFIX_OMV + "hasOntologyLanguage";
	//private static final String PROPERTY_OMV_NAME = PREFIX_OMV + "name";
	private static final String PROPERTY_OMV_STATUS = PREFIX_OMV + "status";
	private static final String PROPERTY_OMV_URI = PREFIX_OMV + "URI";
	private static final String PROPERTY_OMV_VERSION = PREFIX_OMV + "version";
	private static final String PROPERTY_PREFERRED_NAME_PROPERTY = PREFIX_METADATA + "preferredNameProperty";
	private static final String PROPERTY_STATUS_ID = PREFIX_METADATA + "statusID";
	private static final String PROPERTY_SYNONYM_PROPERTY = PREFIX_METADATA + "synonymProperty";
	private static final String PROPERTY_TARGET_TERMINOLOGIES = PREFIX_METADATA + "targetTerminologies";
	private static final String PROPERTY_UPLOAD_DATE = PREFIX_METADATA + "timestampCreation";
	private static final String PROPERTY_URL_HOMEPAGE = PREFIX_METADATA + "urlHomepage";
	private static final String PROPERTY_URL_PUBLICATIONS = PREFIX_METADATA + "urlPublications";
	
//	private static final String PROPERTY_CURRENT_VERSION = PREFIX_METADATA + "currentVersion";
	private static final String PROPERTY_HAS_VERSION = PREFIX_METADATA + "hasVersion";

	private static final String PROPERTY_HAS_VIEW = PREFIX_METADATA + "hasView";

	
	private static final String PROPERTY_IS_VIEW_ON_ONTOLOGY_VERSION = PREFIX_METADATA + "isViewOnOntologyVersion";
	private static final String PROPERTY_VIEW_DEFINITION = PREFIX_METADATA + "viewDefinition";
	private static final String PROPERTY_VIEW_DEFINITION_LANGUAGE = PREFIX_METADATA + "viewDefinitionLanguage";
	private static final String PROPERTY_VIEW_GENERATION_ENGINE = PREFIX_METADATA + "viewGenerationEngine";

	private static final String PROPERTY_VIRTUAL_VIEW_OF = PREFIX_METADATA + "virtualViewOf";

	public static void ensureOntologyBeanDoesNotInvalidateOntologyInstance(
			OWLIndividual ontologyInd, OntologyBean ob, OWLIndividual vOntInd) throws Exception {
		
		if (ontologyInd == null || ob == null) {
			throw new MetadataException("The method ensureOntologyBeanDoesNotInvalidateOntologyInstance can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = ontologyInd.getOWLModel();
		
		if (ob.getInternalVersionNumber() == null ) {
			Integer internalVerNr = getPropertyValue(owlModel, ontologyInd, PROPERTY_INTERNAL_VERSION_NUMBER, Integer.class);
			if (internalVerNr != null) {
				ob.setInternalVersionNumber(internalVerNr);
			}
		}
		
		if (ob.getStatusId() == null ) {
			Integer statusId = getPropertyValue(owlModel, ontologyInd, PROPERTY_STATUS_ID, Integer.class);
			
			if (statusId != null) {
				ob.setStatusId(statusId);
			}
			else {
				ob.setStatusId(ob.getDefaultStatus());
			}
		}
		
		if (ob.getIsManual() == null ) {
			Boolean isManual = getPropertyValue(owlModel, vOntInd, PROPERTY_IS_MANUAL, Boolean.class);
			if (isManual != null) {
				ob.setIsManual(convertBooleanToByte(isManual));
			}
			else {
				ob.setIsManual((byte) 0);
			}
		}
		
		if (ob.getIsRemote() == null ) {
			Boolean isRemote = getPropertyValue(owlModel, ontologyInd, PROPERTY_IS_REMOTE, Boolean.class);
			if (isRemote != null) {
				ob.setIsRemote(convertBooleanToByte(isRemote));
			}
			else {
				ob.setIsRemote((byte) 0);
			}
		}
		
		if (ob.getIsFoundry() == null ) {
			Boolean isFoundry = getPropertyValue(owlModel, ontologyInd, PROPERTY_IS_FOUNDRY, Boolean.class);
			if (isFoundry != null) {
				ob.setIsFoundry(convertBooleanToByte(isFoundry));
			}
			else {
				ob.setIsFoundry((byte) 0);
			}
		}
		
		if (ob.getUserId() == null ) {
			//TODO temporary solution, until multiple administrators will be allowed:
			Integer userId = getFirstElement(getPropertyValueIds(owlModel, ontologyInd, PROPERTY_ADMINISTERED_BY));
			if (userId != null) {
				ob.setUserId( userId );
			}
		}
		
		if (ob.getDateCreated() == null ) {
			ob.setDateCreated(Calendar.getInstance().getTime());
		}
	}
	
	public static void ensureOntologyViewBeanDoesNotInvalidateOntologyViewInstance(
			OWLIndividual ontologyViewInd, OntologyViewBean ob, OWLIndividual vViewInd) throws Exception {
		
		ensureOntologyBeanDoesNotInvalidateOntologyInstance(ontologyViewInd, ob, vViewInd);
		
		//TODO continue
	}
	
	
	public static void fillInOntologyInstancePropertiesFromBean(OWLIndividual ontologyInd,
			OntologyBean ob, OWLIndividual vOntInd, OWLIndividual userInd, 
			Collection<OWLIndividual> domainIndividuals, 
			Collection<OWLIndividual> viewIndividuals) throws MetadataException {
		
		if (ontologyInd == null || ob == null) {
			throw new MetadataException("The method fillInOntologyInstancePropertiesFromBean can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = ontologyInd.getOWLModel();
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_ACRONYM, ob.getAbbreviation());
		
		//ob.getCategoryIds();
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_HAS_DOMAIN, domainIndividuals);
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_CODING_SCHEME, ob.getCodingScheme());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_EMAIL, ob.getContactEmail());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_NAME, ob.getContactName());
		RDFSLiteral litDateCreated = owlModel.createRDFSLiteral(ob.getDateCreated().toString(), owlModel.getXSDdate());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_UPLOAD_DATE, litDateCreated);
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_CREATION_DATE, ob.getDateReleased().toString());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NAME, ob.getDisplayLabel());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DESCRIPTION, ob.getDescription());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DOCUMENTATION, ob.getDocumentation());
		
		//ob.getFileItem();
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_FILE_NAMES, ob.getFilenames());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_FILE_PATH, ob.getFilePath());
		RDFIndividual ontLangInd = getOntologyLanguageInstance(owlModel, ob.getFormat());
		
		if (ontLangInd != null) {
			setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_HAS_ONTOLOGY_LANGUAGE, ontLangInd);
		}
		else {
			//TODO what to do?
			//throw Exception?
			//log.error("No OMV:OntologyLanguage individual found for ontology: " + ontologyInd);
		}
		
		//ob.getHasViews();//FIXME
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_URL_HOMEPAGE, ob.getHomepage());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_ID, ob.getId());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_INTERNAL_VERSION_NUMBER, ob.getInternalVersionNumber());
		RDFSLiteral litIsFoundry = owlModel.createRDFSLiteral(ob.getIsFoundry()==0 ? "false" : "true", owlModel.getXSDboolean());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_IS_FOUNDRY, litIsFoundry); //TODO check this for correct type conversion
		RDFSLiteral litIsManual = owlModel.createRDFSLiteral(ob.getIsManual()==0 ? "false" : "true", owlModel.getXSDboolean());
		setPropertyValue(owlModel, vOntInd, PROPERTY_IS_MANUAL, litIsManual);
		RDFSLiteral litIsRemote = owlModel.createRDFSLiteral(ob.getIsRemote()==0 ? "false" : "true", owlModel.getXSDboolean());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_IS_REMOTE, litIsRemote);
		
		//ob.getIsReviewed();
		
		setPropertyValue(owlModel, vOntInd, PROPERTY_OBO_FOUNDRY_ID, ob.getOboFoundryId());
		
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
		
		setPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_VIEW, viewIndividuals);
	}

	public static void fillInOntologyViewInstancePropertiesFromBean(OWLIndividual ontologyViewInd,
			OntologyViewBean ob, OWLIndividual vViewInd, OWLIndividual userInd, 
			Collection<OWLIndividual> domainIndividuals, 
			Collection<OWLIndividual> viewIndividuals, 
			Collection<OWLIndividual> ontologyIndividuals) throws MetadataException {
		
		fillInOntologyInstancePropertiesFromBean(ontologyViewInd, ob, vViewInd, userInd, domainIndividuals, viewIndividuals);
		
		OWLModel owlModel = ontologyViewInd.getOWLModel();
		
		setPropertyValue(owlModel, ontologyViewInd, PROPERTY_IS_VIEW_ON_ONTOLOGY_VERSION, ontologyIndividuals);

		setPropertyValue(owlModel, ontologyViewInd, PROPERTY_VIEW_DEFINITION, ob.getViewDefinition());
		RDFIndividual viewDefLangInd = getViewDefinitionLanguageInstance(owlModel, ob.getViewDefinitionLanguage());
		
		if (viewDefLangInd != null) {
			setPropertyValue(owlModel, ontologyViewInd, PROPERTY_VIEW_DEFINITION_LANGUAGE, viewDefLangInd);
		}
		else {
			//TODO what to do?
			//throw Exception?
			//log.error("No metadata:ViewDefinitionLanguage individual found for ontology view: " + ontologyViewInd);
		}
		
		OWLIndividual viewGenEngInd = getViewGenerationEngineInstance(owlModel, ob.getViewGenerationEngine());
		
		if (viewGenEngInd != null) {
			setPropertyValue(owlModel, ontologyViewInd, PROPERTY_VIEW_GENERATION_ENGINE, viewGenEngInd);
		}
		else {
			//TODO what to do?
			//throw Exception?
			//log.error("No metadata:ViewGenerationEngine individual found for ontology view: " + ontologyViewInd);
		}
		
		//TODO see if we have to deal with virtualViewOf property or not
	}
	
	
	@SuppressWarnings("deprecation")
	public static void fillInOntologyBeanFromInstance(OntologyBean ob,
			OWLIndividual ontologyInd) throws Exception {
		
		if (ontologyInd == null || ob == null) {
			throw new MetadataException("The method fillInOntologyBeanFromInstance can't take null arguments. Please make sure that both arguments are properly initialized.");
		}
		
		OWLModel owlModel = ontologyInd.getOWLModel();
		
		ob.setAbbreviation( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_ACRONYM, String.class));
		ob.setCategoryIds( getPropertyValueIds(owlModel, ontologyInd, PROPERTY_OMV_HAS_DOMAIN));
		ob.setCodingScheme( getPropertyValue(owlModel, ontologyInd, PROPERTY_CODING_SCHEME, String.class));
		ob.setContactEmail( getPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_EMAIL, String.class));
		ob.setContactName( getPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_NAME, String.class));
		ob.setDateCreated( getPropertyValue(owlModel, ontologyInd, PROPERTY_UPLOAD_DATE, Date.class));
		//ob.setDateReleased( new Date(Date.parse(getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_CREATION_DATE, String.class))));
		ob.setDateReleased( OWLDateWidget.getDate(getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_CREATION_DATE, String.class)));
		ob.setDisplayLabel( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NAME, String.class));
		ob.setDescription( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DESCRIPTION, String.class));
		ob.setDocumentation( getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DOCUMENTATION, String.class));
		
		//ob.setFileItem(null);
		
		ob.setFilenames( getPropertyValues(owlModel, ontologyInd, PROPERTY_FILE_NAMES, String.class));
		ob.setFilePath( getPropertyValue(owlModel, ontologyInd, PROPERTY_FILE_PATH, String.class));
		ob.setFormat( getOntologyFormatValue(
				owlModel, getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_HAS_ONTOLOGY_LANGUAGE, RDFIndividual.class)) );
		
		//ob.setHasViews(null);//FIXME
		
		ob.setHomepage( getPropertyValue(owlModel, ontologyInd, PROPERTY_URL_HOMEPAGE, String.class));
		ob.setId( getPropertyValue(owlModel, ontologyInd, PROPERTY_ID, Integer.class));
		ob.setInternalVersionNumber( getPropertyValue(owlModel, ontologyInd, PROPERTY_INTERNAL_VERSION_NUMBER, Integer.class));
		ob.setIsFoundry( convertBooleanToByte(getPropertyValue(owlModel, ontologyInd, PROPERTY_IS_FOUNDRY, Boolean.class)) );
		
		OWLIndividual vOntInd = getPropertyValue(owlModel, ontologyInd, PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY, OWLIndividual.class);
		ob.setIsManual( convertBooleanToByte(getPropertyValue(owlModel, vOntInd, PROPERTY_IS_MANUAL, Boolean.class)) );
		ob.setIsRemote( convertBooleanToByte(getPropertyValue(owlModel, ontologyInd, PROPERTY_IS_REMOTE, Boolean.class)) );
		
		//ob.setIsReviewed(null);
		
		ob.setOboFoundryId( getPropertyValue(owlModel, vOntInd, PROPERTY_OBO_FOUNDRY_ID, String.class));
		ob.setOntologyId( getPropertyValue(owlModel, vOntInd, PROPERTY_ID, Integer.class));
//		Integer virtOntId = getFirstElement(getPropertyValueIds(owlModel, ontologyInd, PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY));
//		if (virtOntId != null) {
//			ob.setOntologyId( virtOntId);
//		}
//		else {
//			// TODO what to do?
//			// throw Exception?
//			// log.error("No metadata:isVersionOfVirtualOntology individual found for ontology: " + ontologyInd);
//		}
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
		
		ob.setHasViews( getPropertyValueIds(owlModel, ontologyInd, PROPERTY_HAS_VIEW));
	}

	public static void fillInOntologyViewBeanFromInstance(OntologyViewBean ob,
			OWLIndividual ontologyViewInd) throws Exception {
		
		fillInOntologyBeanFromInstance(ob, ontologyViewInd);
		
		OWLModel owlModel = ontologyViewInd.getOWLModel();
		
		ob.setViewOnOntologyVersionId( getPropertyValueIds(owlModel, ontologyViewInd, PROPERTY_IS_VIEW_ON_ONTOLOGY_VERSION));
		ob.setViewDefinition( getPropertyValue(owlModel, ontologyViewInd, PROPERTY_VIEW_DEFINITION, String.class));
		ob.setViewDefinitionLanguage( getViewDefinitionLanguageValue(
				owlModel, getPropertyValue(owlModel, ontologyViewInd, PROPERTY_VIEW_DEFINITION_LANGUAGE, RDFIndividual.class)) );
		ob.setViewGenerationEngine( getViewGenerationEngineValue(
				owlModel, getPropertyValue(owlModel, ontologyViewInd, PROPERTY_VIEW_GENERATION_ENGINE, RDFIndividual.class)) );
		
		//TODO see if we have to deal with virtualViewOf property or not
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
		return getInstanceWithName(metadata, CLASS_OMV_ONTOLOGY_LANGUAGE, format);
	}
	
	private static OWLIndividual getViewDefinitionLanguageInstance(OWLModel metadata,
			String viewDefLanguage) {
		return getInstanceWithName(metadata, CLASS_VIEW_DEFINITION_LANGUAGE, viewDefLanguage);
	}
	
	private static OWLIndividual getViewGenerationEngineInstance(OWLModel metadata,
			String viewGenEngine) {
		return getInstanceWithName(metadata, CLASS_VIEW_GENERATION_ENGINE, viewGenEngine);
	}
	
	private static OWLIndividual getInstanceWithName(OWLModel metadata,
			String className, String name) {
		OWLIndividual instance = null;

		if (name != null) {
			OWLNamedClass owlClass = metadata.getOWLNamedClass(className);
			RDFProperty nameProp = metadata.getRDFProperty(PROPERTY_OMV_NAME);
			RDFProperty labelProp = metadata
					.getRDFProperty(PROPERTY_RDFS_LABEL);

			Collection<?> matchingResources = metadata.getMatchingResources(
					nameProp, name, -1);
			OWLIndividual matchingInd = getIndividualWithType(
					matchingResources, owlClass);

			if (matchingInd != null) {
				instance = matchingInd;
			} else {

				// metadata.getFramesWithValue("rdfs:label", null, false, name);
				// metadata.getMatchingFrames(labelProp, null, false, name, -1);
				matchingResources = metadata.getMatchingResources(labelProp,
						name, -1);
				matchingInd = getIndividualWithType(matchingResources, owlClass);

				if (matchingInd != null) {
					instance = matchingInd;
				} else {
					// TODO check for acronyms too!
					instance = metadata.getOWLIndividual(name);
				}
			}
		}

		return instance;
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
	
	private static String getOntologyFormatValue(OWLModel metadata, RDFIndividual ontologyLanguageInd) throws Exception {
		return getNameOfIndividual(metadata, ontologyLanguageInd);
	}

	private static String getViewDefinitionLanguageValue(OWLModel metadata, RDFIndividual viewDefLanguageInd) throws Exception {
		return getNameOfIndividual(metadata, viewDefLanguageInd);
	}
	
	private static String getViewGenerationEngineValue(OWLModel metadata, RDFIndividual viewGenEngineInd) throws Exception {
		return getNameOfIndividual(metadata, viewGenEngineInd);
	}
	
	private static String getNameOfIndividual(OWLModel metadata, RDFIndividual ind) throws Exception {
		if (ind == null) {
			return null;
		}
		String res;
		res = getPropertyValue(metadata, ind, PROPERTY_OMV_NAME, String.class);
		if (res != null) {
			return res;
		}
		
//		res = getPropertyValue(metadata, ind, PROPERTY_RDFS_LABEL, String.class);
//		if (res != null) {
//			return res;
//		}
		Collection<?> labels = ind.getLabels();
		if (labels != null && labels.size()>0) {
			Object label1 = labels.iterator().next();
			if (label1 != null) {
				return label1.toString();
			}
		}
		
		//TODO check for acronyms too!
		
		return ind.getLocalName();
	}
	
	public static OWLIndividual getLatestVersion(OWLIndividual virtualOntologyInd, boolean onlyActive) throws Exception {
		OWLModel owlModel = virtualOntologyInd.getOWLModel();
		//return getPropertyValue(owlModel, virtualOntologyInd, PROPERTY_CURRENT_VERSION, OWLIndividual.class);
		List<OWLIndividual> propValues = getPropertyValues(owlModel, virtualOntologyInd, PROPERTY_HAS_VERSION, OWLIndividual.class);
		OWLIndividual latest = null;
		int maxVerNr = -1;
		boolean doNotFilterForActive = ! onlyActive;
		
		for (Iterator<OWLIndividual> it = propValues.iterator(); it.hasNext();) {
			OWLIndividual ontologyInd = (OWLIndividual) it.next();
			Integer verNr = getPropertyValue(owlModel, ontologyInd, PROPERTY_INTERNAL_VERSION_NUMBER, Integer.class);
			if ((doNotFilterForActive || isReady(ontologyInd)) 
					&& (latest == null || verNr > maxVerNr)) {
				latest = ontologyInd;
				maxVerNr = verNr;
			}
		}
		
		return latest;
	}
	
//	public static void setLatestVersion(OWLIndividual virtualOntologyInd,
//			OWLIndividual ontologyInd) throws Exception {
//		OWLModel owlModel = virtualOntologyInd.getOWLModel();
//		setPropertyValue(owlModel, virtualOntologyInd, PROPERTY_CURRENT_VERSION, ontologyInd);
//	}

	private static boolean isReady(OWLIndividual ontologyInd) throws Exception {
		OWLModel owlModel = ontologyInd.getOWLModel();
		Integer status = getPropertyValue(owlModel, ontologyInd, PROPERTY_STATUS_ID, Integer.class);
		return status.equals(new Integer(MessageUtils.getMessage("ncbo.status.ready")))
				|| status.equals(new Integer(MessageUtils.getMessage("ncbo.status.notapplicable")));
	}
	
	public static List<Integer> getAllOntologyVersionIDs(OWLModel metadata,
			OWLIndividual virtualOntologyInd) throws Exception {
		//List<Integer> res = new ArrayList<Integer>();
		return getPropertyValueIds(metadata, virtualOntologyInd, PROPERTY_HAS_VERSION);
	}


	public static List<Integer> getAllVirtualOntologyIDs(OWLModel metadata) {
		OWLNamedClass vOntClass = metadata.getOWLNamedClass(CLASS_VIRTUAL_ONTOLOGY);
		List<Integer> res = new ArrayList<Integer>();
		Collection<?> vOntologies = vOntClass.getInstances(true);
		for (Object vOnt : vOntologies) {
			if (vOnt instanceof RDFIndividual) {
				RDFIndividual vOntInst = (RDFIndividual)vOnt;
				try {
					Integer id = getId(metadata, vOntInst);
					res.add(id);
				}
				catch(Exception e) {
					log.error("Exception while getting ID of virtual ontology " + vOntInst.getBrowserText());
				}
			}
			else {
				log.warn("Invalid instance of class " + vOntClass.getBrowserText() + ": " + vOnt);
			}
		}
		return res;
	}
	
	public static List<Integer> getAllVirtualViewIDs(OWLModel metadata) {
		OWLNamedClass vViewClass = metadata.getOWLNamedClass(CLASS_VIRTUAL_VIEW);
		List<Integer> res = new ArrayList<Integer>();
		Collection<?> vViews = vViewClass.getInstances(true);
		for (Object vView : vViews) {
			if (vView instanceof RDFIndividual) {
				RDFIndividual vOntInst = (RDFIndividual)vView;
				try {
					Integer id = getId(metadata, vOntInst);
					res.add(id);
				}
				catch(Exception e) {
					log.error("Exception while getting ID of virtual view " + vOntInst.getBrowserText());
				}
			}
			else {
				log.warn("Invalid instance of class " + vViewClass.getBrowserText() + ": " + vView);
			}
		}
		return res;
	}
	
	public static List<Integer> getAllCategoryIDs(OWLModel metadata) {
		OWLNamedClass ontDomClass = metadata.getOWLNamedClass(CLASS_OMV_ONTOLOGY_DOMAIN);
		List<Integer> res = new ArrayList<Integer>();
		Collection<?> ontDomains = ontDomClass.getInstances(true);
		for (Object ontDomain : ontDomains) {
			if (ontDomain instanceof RDFIndividual) {
				RDFIndividual ontDomainInst = (RDFIndividual)ontDomain;
				try {
					Integer id = getId(metadata, ontDomainInst);
					res.add(id);
				}
				catch(Exception e) {
					log.error("Exception while getting ID of virtual view " + ontDomainInst.getBrowserText());
				}
			}
			else {
				log.warn("Invalid instance of class " + ontDomClass.getBrowserText() + ": " + ontDomain);
			}
		}
		return res;
	}

	
	public static int getNextAvailableVirtualOntologyId(OWLModel metadata) {
		try {
			Integer newId = getNextAvailableIdForClass(metadata.getOWLNamedClass(CLASS_VIRTUAL_ONTOLOGY));
			while (getVirtualViewWithId(metadata, newId) != null) {
				newId = ((newId + 1500) / 1000) * 1000;
			}
			return newId;
		} catch (Exception e) {
			e.printStackTrace();
			return -5;
		}
	}

	public static int getNextAvailableVirtualViewId(OWLModel metadata) {
		try {
			Integer newId = getNextAvailableIdForClass(metadata.getOWLNamedClass(CLASS_VIRTUAL_VIEW));
			while (getVirtualOntologyWithId(metadata, newId) != null) {
				newId = ((newId + 1500) / 1000) * 1000;
			}
			return newId;
		} catch (Exception e) {
			e.printStackTrace();
			return -5;
		}
	}
	
	
	public static int getNextAvailableOntologyVersionId(OWLModel metadata) {
		try {
			Integer newId = getNextAvailableIdForClass(metadata.getOWLNamedClass(CLASS_OMV_ONTOLOGY));
			while (getOntologyViewWithId(metadata, newId) != null) {
				newId = ((newId + 1500) / 1000) * 1000;
			}
			return newId;
		} catch (Exception e) {
			e.printStackTrace();
			return -5;
		}
	}
	
	public static int getNextAvailableOntologyViewVersionId(OWLModel metadata) {
		try {
			Integer newId = getNextAvailableIdForClass(metadata.getOWLNamedClass(CLASS_ONTOLOGY_VIEW));
			while (getOntologyWithId(metadata, newId) != null) {
				newId = ((newId + 1500) / 1000) * 1000;
			}
			return newId;
		} catch (Exception e) {
			e.printStackTrace();
			return -5;
		}
	}
	
	public static OWLIndividual getVirtualOntologyWithId(OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_VIRTUAL_ONTOLOGY, id, false);
	}
	
	public static OWLIndividual getVirtualOntologyOrViewWithId(OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_VIRTUAL_ONTOLOGY, id, true);
	}
	
	public static OWLIndividual getVirtualViewWithId(OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_VIRTUAL_VIEW, id, false);
	}

	public static OWLIndividual getOntologyWithId(OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_OMV_ONTOLOGY, id, false);
	}
	
	public static OWLIndividual getOntologyOrViewWithId(OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_OMV_ONTOLOGY, id, true);
	}
	
	public static OWLIndividual getOntologyViewWithId(OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_ONTOLOGY_VIEW, id, false);
	}

	public static List<OWLIndividual> searchOntologyMetadata(OWLModel metadata, String query) {
		return searchMetadataOnClass(metadata, CLASS_OMV_ONTOLOGY, query);
	}
	
	public static List<OWLIndividual> searchOntologyViewMetadata(OWLModel metadata, String query) {
		return searchMetadataOnClass(metadata, CLASS_ONTOLOGY_VIEW, query);
	}
	
	public static List<OWLIndividual> searchMetadataOnClass(OWLModel metadata,
			String class_name, String query) {
		HashSet<OWLIndividual> res = new HashSet<OWLIndividual>();

		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_URL_PUBLICATIONS, query, false));
		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_URL_HOMEPAGE, query, false));
		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_HAS_CONTACT_EMAIL, query, false));
		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_HAS_CONTACT_NAME, query, false));
		// TODO check corresponding properties for displayLabel and format!
		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_OMV_NAME, query, false));
		// res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
		// 		PROPERTY_format????, query, false));

		return new ArrayList<OWLIndividual>(res);
	}

}
