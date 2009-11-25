package org.ncbo.stanford.util.metadata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;

public class OntologyMetadataUtils extends MetadataUtils {

	private static final Log log = LogFactory
			.getLog(OntologyMetadataUtils.class);

	public static final int INVALID_ID = -5;
	public static final String CLASS_OMV_ONTOLOGY = PREFIX_OMV + "Ontology";
	public static final String CLASS_OMV_ONTOLOGY_DOMAIN = PREFIX_OMV
			+ "OntologyDomain";
	public static final String CLASS_OMV_ONTOLOGY_LANGUAGE = PREFIX_OMV
			+ "OntologyLanguage";
	public static final String CLASS_VIRTUAL_ONTOLOGY = PREFIX_METADATA
			+ "VirtualOntology";
	public static final String CLASS_ONTOLOGY_GROUP = PREFIX_METADATA
			+ "OntologyGroup";

	public static final String CLASS_ONTOLOGY_VIEW = PREFIX_METADATA
			+ "OntologyView";
	public static final String CLASS_VIEW_DEFINITION_LANGUAGE = PREFIX_METADATA
			+ "ViewDefinitionLanguage";
	public static final String CLASS_VIEW_GENERATION_ENGINE = PREFIX_METADATA
			+ "ViewGenerationEngine";
	public static final String CLASS_VIRTUAL_VIEW = PREFIX_METADATA
			+ "VirtualView";

	public static final String CLASS_BIOPORTAL_USER = PREFIX_METADATA
			+ "BioPortalUser";

	public static final String PROPERTY_ADMINISTERED_BY = PREFIX_METADATA
			+ "administeredBy";
	public static final String PROPERTY_CODING_SCHEME = PREFIX_METADATA
			+ "codingScheme";
	public static final String PROPERTY_FILE_NAMES = PREFIX_METADATA
			+ "fileNames";
	public static final String PROPERTY_FILE_PATH = PREFIX_METADATA
			+ "filePath";
	public static final String PROPERTY_HAS_CONTACT_EMAIL = PREFIX_METADATA
			+ "hasContactEmail";
	public static final String PROPERTY_HAS_CONTACT_NAME = PREFIX_METADATA
			+ "hasContactName";
	// public static final String PROPERTY_ID = PREFIX_METADATA + "id";
	public static final String PROPERTY_INTERNAL_VERSION_NUMBER = PREFIX_METADATA
			+ "internalVersionNumber";
	public static final String PROPERTY_IS_FOUNDRY = PREFIX_METADATA
			+ "isFoundry";
	public static final String PROPERTY_IS_MANUAL = PREFIX_METADATA
			+ "isManual";
	public static final String PROPERTY_IS_REMOTE = PREFIX_METADATA
			+ "isRemote";
	public static final String PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY = PREFIX_METADATA
			+ "isVersionOfVirtualOntology";
	public static final String PROPERTY_OBO_FOUNDRY_ID = PREFIX_METADATA
			+ "oboFoundryID";
	public static final String PROPERTY_OMV_CREATION_DATE = PREFIX_OMV
			+ "creationDate";
	// public static final String PROPERTY_OMV_DESCRIPTION = PREFIX_OMV +
	// "description";
	public static final String PROPERTY_OMV_DOCUMENTATION = PREFIX_OMV
			+ "documentation";
	public static final String PROPERTY_OMV_HAS_DOMAIN = PREFIX_OMV
			+ "hasDomain";
	public static final String PROPERTY_OMV_HAS_ONTOLOGY_LANGUAGE = PREFIX_OMV
			+ "hasOntologyLanguage";
	// public static final String PROPERTY_OMV_NAME = PREFIX_OMV + "name";
	public static final String PROPERTY_OMV_STATUS = PREFIX_OMV + "status";
	public static final String PROPERTY_OMV_URI = PREFIX_OMV + "URI";
	public static final String PROPERTY_OMV_VERSION = PREFIX_OMV + "version";
	public static final String PROPERTY_PREFERRED_NAME_PROPERTY = PREFIX_METADATA
			+ "preferredNameProperty";
	public static final String PROPERTY_STATUS_ID = PREFIX_METADATA
			+ "statusID";
	public static final String PROPERTY_SYNONYM_PROPERTY = PREFIX_METADATA
			+ "synonymProperty";
	public static final String PROPERTY_TARGET_TERMINOLOGIES = PREFIX_METADATA
			+ "targetTerminologies";
	public static final String PROPERTY_UPLOAD_DATE = PREFIX_METADATA
			+ "timestampCreation";
	public static final String PROPERTY_URL_HOMEPAGE = PREFIX_METADATA
			+ "urlHomepage";
	public static final String PROPERTY_URL_PUBLICATIONS = PREFIX_METADATA
			+ "urlPublications";

	// public static final String PROPERTY_CURRENT_VERSION = PREFIX_METADATA +
	// "currentVersion";
	public static final String PROPERTY_HAS_VERSION = PREFIX_METADATA
			+ "hasVersion";

	public static final String PROPERTY_BELONGS_TO_GROUP = PREFIX_METADATA
			+ "belongsToGroup";
	public static final String PROPERTY_HAS_VIEW = PREFIX_METADATA + "hasView";
	public static final String PROPERTY_HAS_VIRTUAL_VIEW = PREFIX_METADATA
			+ "hasVirtualView";

	public static final String PROPERTY_DOCUMENTATION_PROPERTY = PREFIX_METADATA
			+ "documentationProperty";
	public static final String PROPERTY_AUTHOR_PROPERTY = PREFIX_METADATA
			+ "authorProperty";
	public static final String PROPERTY_PROPERTY_WITH_UNIQUE_VALUE = PREFIX_METADATA
			+ "propertyWithUniqueValue";
	public static final String PROPERTY_METRICS_PREFERRED_MAXIMUM_SUBCLASS_LIMIT = PREFIX_METRICS
			+ "preferredMaximumSubclassLimit";

	public static final String PROPERTY_OMV_NUMBER_OF_AXIOMS = PREFIX_OMV
			+ "numberOfAxioms";
	public static final String PROPERTY_OMV_NUMBER_OF_CLASSES = PREFIX_OMV
			+ "numberOfClasses";
	public static final String PROPERTY_OMV_NUMBER_OF_INDIVIDUALS = PREFIX_OMV
			+ "numberOfIndividuals";
	public static final String PROPERTY_OMV_NUMBER_OF_PROPERTIES = PREFIX_OMV
			+ "numberOfProperties";

	public static final String PROPERTY_METRICS_MAXIMUM_DEPTH = PREFIX_METRICS
			+ "maximumDepth";
	public static final String PROPERTY_METRICS_MAXIMUM_NUMBER_OF_SIBLINGS = PREFIX_METRICS
			+ "maximumNumberOfSiblings";
	public static final String PROPERTY_METRICS_AVERAGE_NUMBER_OF_SIBLINGS = PREFIX_METRICS
			+ "averageNumberOfSiblings";
	public static final String PROPERTY_METRICS_CLASSES_WITH_NO_DOCUMENTATION = PREFIX_METRICS
			+ "classesWithNoDocumentation";
	public static final String PROPERTY_METRICS_CLASSES_WITH_NO_AUTHOR = PREFIX_METRICS
			+ "classesWithNoAuthor";
	public static final String PROPERTY_METRICS_CLASSES_WITH_SINGLE_SUBCLASS = PREFIX_METRICS
			+ "classesWithSingleSubclass";
	public static final String PROPERTY_METRICS_CLASSES_WITH_MORE_THAN_X_SUBCLASSES = PREFIX_METRICS
			+ "classesWithMoreThanXSubclasses";
	public static final String PROPERTY_METRICS_CLASSES_WITH_MORE_THAN_ONE_PROPERTY_VALUE_FOR_PROPERTY_WITH_UNIQUE_VALUE = PREFIX_METRICS
			+ "classesWithMoreThanOnePropertyValueForPropertyWithUniqueValue";

	public static final String PROPERTY_IS_VIEW_ON_ONTOLOGY_VERSION = PREFIX_METADATA
			+ "isViewOnOntologyVersion";
	public static final String PROPERTY_VIEW_DEFINITION = PREFIX_METADATA
			+ "viewDefinition";
	public static final String PROPERTY_VIEW_DEFINITION_LANGUAGE = PREFIX_METADATA
			+ "viewDefinitionLanguage";
	public static final String PROPERTY_VIEW_GENERATION_ENGINE = PREFIX_METADATA
			+ "viewGenerationEngine";

	public static final String PROPERTY_VIRTUAL_VIEW_OF = PREFIX_METADATA
			+ "virtualViewOf";

	public static void ensureOntologyBeanDoesNotInvalidateOntologyInstance(
			OWLIndividual ontologyInd, OntologyBean ob, OWLIndividual vOntInd)
			throws Exception {

		if (ontologyInd == null || ob == null) {
			throw new MetadataException(
					"The method ensureOntologyBeanDoesNotInvalidateOntologyInstance can't take null arguments. Please make sure that both arguments are properly initialized.");
		}

		OWLModel owlModel = ontologyInd.getOWLModel();

		if (ob.getInternalVersionNumber() == null) {
			Integer internalVerNr = getPropertyValue(owlModel, ontologyInd,
					PROPERTY_INTERNAL_VERSION_NUMBER, Integer.class);
			if (internalVerNr != null) {
				ob.setInternalVersionNumber(internalVerNr);
			}
		}

		if (ob.getStatusId() == null) {
			Integer statusId = getPropertyValue(owlModel, ontologyInd,
					PROPERTY_STATUS_ID, Integer.class);

			if (statusId != null) {
				ob.setStatusId(statusId);
			} else {
				ob.setStatusId(ob.getDefaultStatus());
			}
		}

		if (ob.getIsManual() == null) {
			Boolean isManual = getPropertyValue(owlModel, vOntInd,
					PROPERTY_IS_MANUAL, Boolean.class);
			if (isManual != null) {
				ob.setIsManual(convertBooleanToByte(isManual));
			} else {
				ob.setIsManual((byte) 0);
			}
		}

		if (ob.getIsRemote() == null) {
			Boolean isRemote = getPropertyValue(owlModel, ontologyInd,
					PROPERTY_IS_REMOTE, Boolean.class);
			if (isRemote != null) {
				ob.setIsRemote(convertBooleanToByte(isRemote));
			} else {
				ob.setIsRemote((byte) 0);
			}
		}

		if (ob.getIsFoundry() == null) {
			Boolean isFoundry = getPropertyValue(owlModel, ontologyInd,
					PROPERTY_IS_FOUNDRY, Boolean.class);
			if (isFoundry != null) {
				ob.setIsFoundry(convertBooleanToByte(isFoundry));
			} else {
				ob.setIsFoundry((byte) 0);
			}
		}

		if (ob.getUserId() == null) {
			// TODO temporary solution, until multiple administrators will be
			// allowed:
			Integer userId = getFirstElement(getPropertyValueIds(owlModel,
					ontologyInd, PROPERTY_ADMINISTERED_BY));
			if (userId != null) {
				ob.setUserId(userId);
			}
		}

		if (ob.getDateCreated() == null) {
			ob.setDateCreated(Calendar.getInstance().getTime());
		}
	}

	public static void fillInOntologyInstancePropertiesFromBean(
			OWLIndividual ontologyInd, OntologyBean ob, OWLIndividual vOntInd,
			OWLIndividual userInd, Collection<OWLIndividual> domainIndividuals,
			Collection<OWLIndividual> viewIndividuals,
			Collection<OWLIndividual> viewOnOntologyIndividuals)
			throws MetadataException {

		if (ontologyInd == null || ob == null) {
			throw new MetadataException(
					"The method fillInOntologyInstancePropertiesFromBean can't take null arguments. Please make sure that both arguments are properly initialized.");
		}

		OWLModel owlModel = ontologyInd.getOWLModel();

		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_ACRONYM, ob
				.getAbbreviation());

		// CategoryIds;
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_HAS_DOMAIN,
				domainIndividuals);

		setPropertyValue(owlModel, ontologyInd, PROPERTY_CODING_SCHEME, ob
				.getCodingScheme());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_EMAIL, ob
				.getContactEmail());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_CONTACT_NAME, ob
				.getContactName());
		RDFSLiteral litDateCreated = createXsdDateTimePropertyValue(owlModel,
				ob.getDateCreated());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_UPLOAD_DATE,
				litDateCreated);
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_CREATION_DATE,
				convertDateToDateTimeString(ob.getDateReleased()));// or maybe
		// just
		// convertDateToDateString(ob.getDateReleased())
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NAME, ob
				.getDisplayLabel());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DESCRIPTION, ob
				.getDescription());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_DOCUMENTATION, ob
				.getDocumentation());

		setPropertyValue(owlModel, ontologyInd, PROPERTY_FILE_NAMES, ob
				.getFilenames());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_FILE_PATH, ob
				.getFilePath());
		RDFIndividual ontLangInd = getOntologyLanguageInstance(owlModel, ob
				.getFormat());

		if (ontLangInd != null) {
			setPropertyValue(owlModel, ontologyInd,
					PROPERTY_OMV_HAS_ONTOLOGY_LANGUAGE, ontLangInd);
		} else {
			log.error("No OMV:OntologyLanguage individual found for ontology "
					+ ontologyInd + ": " + ob.getFormat());
		}

		setPropertyValue(owlModel, ontologyInd, PROPERTY_URL_HOMEPAGE, ob
				.getHomepage());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_ID, ob.getId());
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_INTERNAL_VERSION_NUMBER, ob.getInternalVersionNumber());
		RDFSLiteral litIsFoundry = owlModel.createRDFSLiteral(ob.getIsFoundry()
				.byteValue() == ApplicationConstants.FALSE ? "false" : "true",
				owlModel.getXSDboolean());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_IS_FOUNDRY,
				litIsFoundry); // TODO check this for correct type conversion
		RDFSLiteral litIsManual = owlModel.createRDFSLiteral(ob.getIsManual()
				.byteValue() == ApplicationConstants.FALSE ? "false" : "true",
				owlModel.getXSDboolean());
		setPropertyValue(owlModel, vOntInd, PROPERTY_IS_MANUAL, litIsManual);
		RDFSLiteral litIsRemote = owlModel.createRDFSLiteral(ob.getIsRemote()
				.byteValue() == ApplicationConstants.FALSE ? "false" : "true",
				owlModel.getXSDboolean());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_IS_REMOTE, litIsRemote);

		setPropertyValue(owlModel, vOntInd, PROPERTY_OBO_FOUNDRY_ID, ob
				.getOboFoundryId());

		// OntologyId;
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY, vOntInd);

		// VirtualViewIds;
		// We don't have to explicitly set the virtual view versions as
		// belonging to the virtual ontology
		// but we create this relation when uploading/updating a view (See below
		// the
		// updateIsViewOnOntologyVersionProperty(...) call)

		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_PREFERRED_NAME_PROPERTY, ob.getPreferredNameSlot());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_URL_PUBLICATIONS, ob
				.getPublication());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_STATUS_ID, ob
				.getStatusId());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_SYNONYM_PROPERTY, ob
				.getSynonymSlot());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_TARGET_TERMINOLOGIES,
				ob.getTargetTerminologies());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_URI, ob.getUrn());

		// UserId;
		setPropertyValue(owlModel, ontologyInd, PROPERTY_ADMINISTERED_BY,
				userInd);

		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_VERSION, ob
				.getVersionNumber());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_STATUS, ob
				.getVersionStatus());

		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_DOCUMENTATION_PROPERTY, ob.getDocumentationSlot());
		setPropertyValue(owlModel, ontologyInd, PROPERTY_AUTHOR_PROPERTY, ob
				.getAuthorSlot());
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_PROPERTY_WITH_UNIQUE_VALUE, ob
						.getSlotWithUniqueValue());
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_METRICS_PREFERRED_MAXIMUM_SUBCLASS_LIMIT, ob
						.getPreferredMaximumSubclassLimit());

		setPropertyValue(owlModel, ontologyInd, PROPERTY_HAS_VIEW,
				viewIndividuals);

		if (ob.isView()) {
			if (!isOntologyViewIndividual(ontologyInd)) {
				throw new MetadataException(
						"The method fillInOntologyInstancePropertiesFromBean must take an OntologyVersion individual when othe ontologyBean has the flag isView set to true. Please make sure that both arguments are properly initialized.");
			}

			// setPropertyValue(owlModel, ontologyInd,
			// PROPERTY_IS_VIEW_ON_ONTOLOGY_VERSION, viewOnOntologyIndividuals);
			updateIsViewOnOntologyVersionProperty(owlModel, ontologyInd,
					viewOnOntologyIndividuals);

			setPropertyValue(owlModel, ontologyInd, PROPERTY_VIEW_DEFINITION,
					ob.getViewDefinition());

			String viewDefinitionLanguage = ob.getViewDefinitionLanguage();
			RDFIndividual viewDefLangInd = getViewDefinitionLanguageInstance(
					owlModel, viewDefinitionLanguage);

			if (viewDefinitionLanguage == null || viewDefLangInd != null) {
				setPropertyValue(owlModel, ontologyInd,
						PROPERTY_VIEW_DEFINITION_LANGUAGE, viewDefLangInd);
			} else {
				log
						.error("No metadata:ViewDefinitionLanguage individual found for ontology view "
								+ ontologyInd + ": " + viewDefinitionLanguage);
			}

			String viewGenerationEngine = ob.getViewGenerationEngine();
			RDFIndividual viewGenEngInd = getViewGenerationEngineInstance(
					owlModel, viewGenerationEngine);

			if (viewGenerationEngine == null || viewGenEngInd != null) {
				setPropertyValue(owlModel, ontologyInd,
						PROPERTY_VIEW_GENERATION_ENGINE, viewGenEngInd);
			} else {
				log
						.error("No metadata:ViewGenerationEngine individual found for ontology view "
								+ ontologyInd + ": " + viewGenerationEngine);
			}
		}
	}

	private static void updateIsViewOnOntologyVersionProperty(
			OWLModel owlModel, OWLIndividual ontologyViewInd,
			Collection<OWLIndividual> viewOnOntologyIndividuals)
			throws MetadataException {
		// this method replaces the more simple:
		// setPropertyValue(owlModel, ontologyViewInd,
		// PROPERTY_IS_VIEW_ON_ONTOLOGY_VERSION, viewOnOntologyIndividuals);
		// method call with a "remove old values" + "add new values" logic since
		// we have to maintain the
		// integrity of the relationships at the virtual ontology/view level
		try {
			OWLIndividual vViewInd = getPropertyValue(owlModel,
					ontologyViewInd, PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY,
					OWLIndividual.class);

			List<OWLIndividual> currValueForViewsOnOntologyIndividuals = getPropertyValues(
					owlModel, ontologyViewInd,
					PROPERTY_IS_VIEW_ON_ONTOLOGY_VERSION, OWLIndividual.class);

			// remove all current values
			for (OWLIndividual ontInd : currValueForViewsOnOntologyIndividuals) {
				OWLIndividual vOntInd = getPropertyValue(owlModel, ontInd,
						PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY,
						OWLIndividual.class);
				removePropertyValue(owlModel, vViewInd,
						PROPERTY_VIRTUAL_VIEW_OF, vOntInd);
			}

			setPropertyValue(owlModel, ontologyViewInd,
					PROPERTY_IS_VIEW_ON_ONTOLOGY_VERSION,
					viewOnOntologyIndividuals);

			// add all new values
			for (OWLIndividual ontInd : viewOnOntologyIndividuals) {
				OWLIndividual vOntInd = getPropertyValue(owlModel, ontInd,
						PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY,
						OWLIndividual.class);
				addPropertyValue(owlModel, vViewInd, PROPERTY_VIRTUAL_VIEW_OF,
						vOntInd);
			}
		} catch (Exception e) {
			throw new MetadataException(
					"updateIsViewOnOntologyVersionproperty failed", e);
		}
	}

	public static void fillInOntologyInstancePropertiesFromBean(
			OWLIndividual ontologyInd, OntologyMetricsBean mb)
			throws MetadataException {

		if (ontologyInd == null || mb == null) {
			throw new MetadataException(
					"The method fillInOntologyInstancePropertiesFromBean can't take null arguments. Please make sure that both arguments are properly initialized.");
		}

		OWLModel owlModel = ontologyInd.getOWLModel();

		setPropertyValue(owlModel, ontologyInd, PROPERTY_ID, mb.getId());

		RDFSLiteral litVal;
		Integer intVal;
		intVal = mb.getNumberOfAxioms();
		litVal = (intVal == null ? null : DefaultRDFSLiteral.create(owlModel,
				intVal.toString(), owlModel.getXSDinteger()));
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NUMBER_OF_AXIOMS,
				litVal);
		intVal = mb.getNumberOfClasses();
		litVal = (intVal == null ? null : DefaultRDFSLiteral.create(owlModel,
				intVal.toString(), owlModel.getXSDinteger()));
		setPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_NUMBER_OF_CLASSES,
				litVal);
		intVal = mb.getNumberOfIndividuals();
		litVal = (intVal == null ? null : DefaultRDFSLiteral.create(owlModel,
				intVal.toString(), owlModel.getXSDinteger()));
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_NUMBER_OF_INDIVIDUALS, litVal);
		intVal = mb.getNumberOfProperties();
		litVal = (intVal == null ? null : DefaultRDFSLiteral.create(owlModel,
				intVal.toString(), owlModel.getXSDinteger()));
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_NUMBER_OF_PROPERTIES, litVal);

		setPropertyValue(owlModel, ontologyInd, PROPERTY_METRICS_MAXIMUM_DEPTH,
				mb.getMaximumDepth());
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_METRICS_MAXIMUM_NUMBER_OF_SIBLINGS, mb
						.getMaximumNumberOfSiblings());
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_METRICS_AVERAGE_NUMBER_OF_SIBLINGS, mb
						.getAverageNumberOfSiblings());
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_METRICS_CLASSES_WITH_NO_DOCUMENTATION, mb
						.getClassesWithNoDocumentation());
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_METRICS_CLASSES_WITH_NO_AUTHOR, mb
						.getClassesWithNoAuthor());
		setPropertyValue(owlModel, ontologyInd,
				PROPERTY_METRICS_CLASSES_WITH_SINGLE_SUBCLASS, mb
						.getClassesWithOneSubclass());

		Map<String, Integer> classesWithMoreThanXSubclasses = mb
				.getClassesWithMoreThanXSubclasses();
		setPropertyValuesFromMap(owlModel, ontologyInd,
				PROPERTY_METRICS_CLASSES_WITH_MORE_THAN_X_SUBCLASSES,
				classesWithMoreThanXSubclasses);
		
		setPropertyValue(
				owlModel,
				ontologyInd,
				PROPERTY_METRICS_CLASSES_WITH_MORE_THAN_ONE_PROPERTY_VALUE_FOR_PROPERTY_WITH_UNIQUE_VALUE,
				mb.getClassesWithMoreThanOnePropertyValue());
	}

	public static void fillInOntologyBeanFromInstance(OntologyBean ob,
			OWLIndividual ontologyInd) throws Exception {
		if (ontologyInd == null || ob == null) {
			throw new MetadataException(
					"The method fillInOntologyBeanFromInstance can't take null arguments. Please make sure that both arguments are properly initialized.");
		}

		OWLModel owlModel = ontologyInd.getOWLModel();

		ob.setAbbreviation(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_ACRONYM, String.class));
		ob.setCategoryIds(getPropertyValueIds(owlModel, ontologyInd,
				PROPERTY_OMV_HAS_DOMAIN));
		// groups will be set below after we retrieve the virtual ontology
		// instance
		ob.setCodingScheme(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_CODING_SCHEME, String.class));
		ob.setContactEmail(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_HAS_CONTACT_EMAIL, String.class));
		ob.setContactName(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_HAS_CONTACT_NAME, String.class));
		ob.setDateCreated(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_UPLOAD_DATE, Date.class));
		ob.setDateReleased(convertStringToDate(getPropertyValue(owlModel,
				ontologyInd, PROPERTY_OMV_CREATION_DATE, String.class)));
		ob.setDisplayLabel(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_NAME, String.class));
		ob.setDescription(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_DESCRIPTION, String.class));
		ob.setDocumentation(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_DOCUMENTATION, String.class));
		ob.setFilenames(getPropertyValues(owlModel, ontologyInd,
				PROPERTY_FILE_NAMES, String.class));
		ob.setFilePath(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_FILE_PATH, String.class));
		ob.setFormat(getOntologyFormatValue(owlModel, getPropertyValue(
				owlModel, ontologyInd, PROPERTY_OMV_HAS_ONTOLOGY_LANGUAGE,
				RDFIndividual.class)));
		ob.setHomepage(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_URL_HOMEPAGE, String.class));
		ob.setId(getPropertyValue(owlModel, ontologyInd, PROPERTY_ID,
				Integer.class));
		ob.setInternalVersionNumber(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_INTERNAL_VERSION_NUMBER, Integer.class));
		ob.setIsFoundry(convertBooleanToByte(getPropertyValue(owlModel,
				ontologyInd, PROPERTY_IS_FOUNDRY, Boolean.class)));

		OWLIndividual vOntInd = getPropertyValue(owlModel, ontologyInd,
				PROPERTY_IS_VERSION_OF_VIRTUAL_ONTOLOGY, OWLIndividual.class);
		ob.setIsManual(convertBooleanToByte(getPropertyValue(owlModel, vOntInd,
				PROPERTY_IS_MANUAL, Boolean.class)));
		ob.setGroupIds(getPropertyValueIds(owlModel, vOntInd,
				PROPERTY_BELONGS_TO_GROUP));
		ob.setOboFoundryId(getPropertyValue(owlModel, vOntInd,
				PROPERTY_OBO_FOUNDRY_ID, String.class));
		ob.setOntologyId(getPropertyValue(owlModel, vOntInd, PROPERTY_ID,
				Integer.class));
		ob.setVirtualViewIds(getPropertyValueIds(owlModel, vOntInd,
				PROPERTY_HAS_VIRTUAL_VIEW));

		ob.setIsRemote(convertBooleanToByte(getPropertyValue(owlModel,
				ontologyInd, PROPERTY_IS_REMOTE, Boolean.class)));
		ob.setPreferredNameSlot(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_PREFERRED_NAME_PROPERTY, String.class));
		ob.setPublication(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_URL_PUBLICATIONS, String.class));
		ob.setStatusId(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_STATUS_ID, Integer.class));
		ob.setSynonymSlot(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_SYNONYM_PROPERTY, String.class));
		ob.setTargetTerminologies(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_TARGET_TERMINOLOGIES, String.class));
		ob.setUrn(getPropertyValue(owlModel, ontologyInd, PROPERTY_OMV_URI,
				String.class));
		// TODO temporary solution, until multiple administrators will be
		// allowed:
		ob.setUserId(getFirstElement(getPropertyValueIds(owlModel, ontologyInd,
				PROPERTY_ADMINISTERED_BY)));
		ob.setVersionNumber(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_VERSION, String.class));
		ob.setVersionStatus(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_STATUS, String.class));
		ob.setDocumentationSlot(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_DOCUMENTATION_PROPERTY, String.class));
		ob.setAuthorSlot(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_AUTHOR_PROPERTY, String.class));
		ob.setSlotWithUniqueValue(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_PROPERTY_WITH_UNIQUE_VALUE, String.class));
		ob.setPreferredMaximumSubclassLimit(getPropertyValue(owlModel,
				ontologyInd, PROPERTY_METRICS_PREFERRED_MAXIMUM_SUBCLASS_LIMIT,
				Integer.class));

		ob.setHasViews(getPropertyValueIds(owlModel, ontologyInd,
				PROPERTY_HAS_VIEW));

		// set view specific properties
		if (isOntologyViewIndividual(ontologyInd)) {
			ob.setView(true);
			ob.setViewOnOntologyVersionId(getPropertyValueIds(owlModel,
					ontologyInd, PROPERTY_IS_VIEW_ON_ONTOLOGY_VERSION));
			ob.setViewDefinition(getPropertyValue(owlModel, ontologyInd,
					PROPERTY_VIEW_DEFINITION, String.class));
			ob.setViewDefinitionLanguage(getViewDefinitionLanguageValue(
					owlModel, getPropertyValue(owlModel, ontologyInd,
							PROPERTY_VIEW_DEFINITION_LANGUAGE,
							RDFIndividual.class)));
			ob.setViewGenerationEngine(getViewGenerationEngineValue(owlModel,
					getPropertyValue(owlModel, ontologyInd,
							PROPERTY_VIEW_GENERATION_ENGINE,
							RDFIndividual.class)));
			// TODO see if we have to deal with virtualViewOf property or not
		} else {
			ob.setView(false);
		}
	}

	private static void fillInDummyOntologyBeanFromVirtualOntology(
			OntologyBean ob, OWLIndividual vOntologyInd) throws Exception {
		if (vOntologyInd == null || ob == null) {
			throw new MetadataException(
					"The method fillInDummyOntologyBeanFromVirtualOntology "
							+ "can't take null arguments. Please make sure that both arguments are properly initialized.");
		}

		ob.setId(INVALID_ID);
		OWLModel owlModel = vOntologyInd.getOWLModel();
		ob.setOntologyId(getPropertyValue(owlModel, vOntologyInd, PROPERTY_ID,
				Integer.class));
		ob.setIsManual(convertBooleanToByte(getPropertyValue(owlModel,
				vOntologyInd, PROPERTY_IS_MANUAL, Boolean.class)));
	}

	@SuppressWarnings("unchecked")
	public static void fillInMetricsBeanFromInstance(OntologyMetricsBean mb,
			OWLIndividual ontologyInd) throws Exception {

		OWLModel owlModel = ontologyInd.getOWLModel();

		mb.setId(getPropertyValue(owlModel, ontologyInd, PROPERTY_ID,
				Integer.class));

		mb.setNumberOfAxioms(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_NUMBER_OF_AXIOMS, Integer.class));
		mb.setNumberOfClasses(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_NUMBER_OF_CLASSES, Integer.class));
		mb.setNumberOfIndividuals(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_NUMBER_OF_INDIVIDUALS, Integer.class));
		mb.setNumberOfProperties(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_OMV_NUMBER_OF_PROPERTIES, Integer.class));
		mb.setMaximumDepth(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_METRICS_MAXIMUM_DEPTH, Integer.class));
		mb.setMaximumNumberOfSiblings(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_METRICS_MAXIMUM_NUMBER_OF_SIBLINGS, Integer.class));
		mb.setAverageNumberOfSiblings(getPropertyValue(owlModel, ontologyInd,
				PROPERTY_METRICS_AVERAGE_NUMBER_OF_SIBLINGS, Integer.class));
		mb.setClassesWithOneSubclass(getPropertyValues(owlModel, ontologyInd,
				PROPERTY_METRICS_CLASSES_WITH_SINGLE_SUBCLASS, String.class));
		mb.setClassesWithMoreThanXSubclasses(getPropertyValuesAsMap(owlModel,
				ontologyInd,
				PROPERTY_METRICS_CLASSES_WITH_MORE_THAN_X_SUBCLASSES,
				String.class, Integer.class));
		mb.setClassesWithNoDocumentation(getPropertyValues(owlModel,
				ontologyInd, PROPERTY_METRICS_CLASSES_WITH_NO_DOCUMENTATION,
				String.class));
		mb.setClassesWithNoAuthor(getPropertyValues(owlModel, ontologyInd,
				PROPERTY_METRICS_CLASSES_WITH_NO_AUTHOR, String.class));
		mb
				.setClassesWithMoreThanOnePropertyValue(getPropertyValues(
						owlModel,
						ontologyInd,
						PROPERTY_METRICS_CLASSES_WITH_MORE_THAN_ONE_PROPERTY_VALUE_FOR_PROPERTY_WITH_UNIQUE_VALUE,
						String.class));

	}

	public static boolean isOntologyViewIndividual(OWLIndividual ind) {
		OWLModel owlModel = ind.getOWLModel();
		OWLNamedClass ontViewClass = owlModel
				.getOWLNamedClass(CLASS_ONTOLOGY_VIEW);
		return ind.hasRDFType(ontViewClass);
	}

	public static boolean isVirtualViewIndividual(OWLIndividual ind) {
		OWLModel owlModel = ind.getOWLModel();
		OWLNamedClass vrtViewClass = owlModel
				.getOWLNamedClass(CLASS_VIRTUAL_VIEW);
		return ind.hasRDFType(vrtViewClass);
	}

	private static Byte convertBooleanToByte(Boolean boolValue) {
		if (boolValue == null) {
			return null;
		} else {
			return (boolValue ? (byte) 1 : (byte) 0);
		}
	}

	private static RDFIndividual getOntologyLanguageInstance(OWLModel metadata,
			String format) {
		return getInstanceWithName(metadata, CLASS_OMV_ONTOLOGY_LANGUAGE,
				format);
	}

	private static RDFIndividual getViewDefinitionLanguageInstance(
			OWLModel metadata, String viewDefLanguage) {
		return getInstanceWithName(metadata, CLASS_VIEW_DEFINITION_LANGUAGE,
				viewDefLanguage);
	}

	private static RDFIndividual getViewGenerationEngineInstance(
			OWLModel metadata, String viewGenEngine) {
		return getInstanceWithName(metadata, CLASS_VIEW_GENERATION_ENGINE,
				viewGenEngine);
	}

	private static RDFIndividual getInstanceWithName(OWLModel metadata,
			String className, String name) {
		RDFIndividual instance = null;

		if (name != null) {
			OWLNamedClass owlClass = metadata.getOWLNamedClass(className);
			RDFProperty nameProp = metadata.getRDFProperty(PROPERTY_OMV_NAME);
			RDFProperty labelProp = metadata
					.getRDFProperty(PROPERTY_RDFS_LABEL);
			RDFProperty acronymProp = metadata
					.getRDFProperty(PROPERTY_OMV_ACRONYM);

			Collection<?> matchingResources = metadata.getMatchingResources(
					nameProp, name, -1);
			RDFIndividual matchingInd = getIndividualWithType(
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
					matchingResources = metadata.getMatchingResources(
							acronymProp, name, -1);
					matchingInd = getIndividualWithType(matchingResources,
							owlClass);

					if (matchingInd != null) {
						instance = matchingInd;
					} else {
						instance = metadata.getOWLIndividual(name);
					}
				}
			}
		}

		return instance;
	}

	private static RDFIndividual getIndividualWithType(
			Collection<?> matchingResources, OWLClass type) {
		for (Object matchingRes : matchingResources) {
			if (matchingRes instanceof RDFIndividual) {
				RDFIndividual res = (RDFIndividual) matchingRes;
				if (res.hasRDFType(type)) {
					return res;
				}
			}
		}

		return null;
	}

	private static String getOntologyFormatValue(OWLModel metadata,
			RDFIndividual ontologyLanguageInd) throws Exception {
		return getNameOfIndividual(metadata, ontologyLanguageInd,
				AcronymUsagePolicy.AcronymAsNamePreferred);
	}

	private static String getViewDefinitionLanguageValue(OWLModel metadata,
			RDFIndividual viewDefLanguageInd) throws Exception {
		return getNameOfIndividual(metadata, viewDefLanguageInd,
				AcronymUsagePolicy.AcronymIfNoName);
	}

	private static String getViewGenerationEngineValue(OWLModel metadata,
			RDFIndividual viewGenEngineInd) throws Exception {
		return getNameOfIndividual(metadata, viewGenEngineInd,
				AcronymUsagePolicy.AcronymIfNoName);
	}

	private static String getNameOfIndividual(OWLModel metadata,
			RDFIndividual ind, AcronymUsagePolicy acronymUsagePolicy)
			throws Exception {
		if (ind == null) {
			return null;
		}
		String res;
		if (acronymUsagePolicy == AcronymUsagePolicy.AcronymAsNamePreferred) {
			res = getPropertyValue(metadata, ind, PROPERTY_OMV_ACRONYM,
					String.class);
			if (!StringHelper.isNullOrNullString(res)) {
				return res;
			}
		}

		res = getPropertyValue(metadata, ind, PROPERTY_OMV_NAME, String.class);
		if (!StringHelper.isNullOrNullString(res)) {
			return res;
		}

		if (acronymUsagePolicy == AcronymUsagePolicy.AcronymIfNoName) {
			res = getPropertyValue(metadata, ind, PROPERTY_OMV_ACRONYM,
					String.class);
			if (!StringHelper.isNullOrNullString(res)) {
				return res;
			}
		}

		// res = getPropertyValue(metadata, ind, PROPERTY_RDFS_LABEL,
		// String.class);
		// if (res != null) {
		// return res;
		// }
		Collection<?> labels = ind.getLabels();
		if (labels != null && labels.size() > 0) {
			Object label1 = labels.iterator().next();
			if (label1 != null) {
				return label1.toString();
			}
		}

		if (acronymUsagePolicy == AcronymUsagePolicy.AcronymAsLastResort) {
			res = getPropertyValue(metadata, ind, PROPERTY_OMV_ACRONYM,
					String.class);
			if (!StringHelper.isNullOrNullString(res)) {
				return res;
			}
		}

		return ind.getLocalName();
	}

	public static OWLIndividual getLatestVersion(
			OWLIndividual virtualOntologyInd, boolean onlyActive)
			throws Exception {
		OWLModel owlModel = virtualOntologyInd.getOWLModel();
		// return getPropertyValue(owlModel, virtualOntologyInd,
		// PROPERTY_CURRENT_VERSION, OWLIndividual.class);
		List<OWLIndividual> propValues = getPropertyValues(owlModel,
				virtualOntologyInd, PROPERTY_HAS_VERSION, OWLIndividual.class);
		OWLIndividual latest = null;
		int maxVerNr = -1;
		boolean doNotFilterForActive = !onlyActive;

		for (Iterator<OWLIndividual> it = propValues.iterator(); it.hasNext();) {
			OWLIndividual ontologyInd = (OWLIndividual) it.next();
			Integer verNr = getPropertyValue(owlModel, ontologyInd,
					PROPERTY_INTERNAL_VERSION_NUMBER, Integer.class);

			if (!isDeprecated(ontologyInd)
					&& (doNotFilterForActive || isReady(ontologyInd))
					&& (latest == null || verNr > maxVerNr)) {
				latest = ontologyInd;
				maxVerNr = verNr;
			}
		}

		return latest;
	}

	// public static void setLatestVersion(OWLIndividual virtualOntologyInd,
	// OWLIndividual ontologyInd) throws Exception {
	// OWLModel owlModel = virtualOntologyInd.getOWLModel();
	// setPropertyValue(owlModel, virtualOntologyInd,
	// PROPERTY_CURRENT_VERSION, ontologyInd);
	// }

	private static boolean isReady(OWLIndividual ontologyInd) throws Exception {
		OWLModel owlModel = ontologyInd.getOWLModel();
		Integer status = getPropertyValue(owlModel, ontologyInd,
				PROPERTY_STATUS_ID, Integer.class);
		return status.equals(StatusEnum.STATUS_READY.getStatus())
				|| status.equals(StatusEnum.STATUS_NOTAPPLICABLE.getStatus());
	}

	private static boolean isDeprecated(RDFResource ontologyInd)
			throws Exception {
		OWLModel owlModel = ontologyInd.getOWLModel();
		Integer status = getPropertyValue(owlModel, ontologyInd,
				PROPERTY_STATUS_ID, Integer.class);
		return status.equals(StatusEnum.STATUS_DEPRECATED.getStatus());
	}

	public static List<Integer> getAllOntologyVersionIDs(OWLModel metadata,
			OWLIndividual virtualOntologyInd, boolean excludeDeprecated)
			throws Exception {
		List<Integer> allVersionIds;

		if (excludeDeprecated) {
			allVersionIds = new ArrayList<Integer>(0);
			List<RDFResource> propValues = getPropertyValues(metadata,
					virtualOntologyInd, PROPERTY_HAS_VERSION, RDFResource.class);

			for (RDFResource ontologyVersionInd : propValues) {
				if (!isDeprecated(ontologyVersionInd)) {
					allVersionIds.add(getId(metadata, ontologyVersionInd));
				}
			}
		} else {
			allVersionIds = getPropertyValueIds(metadata, virtualOntologyInd,
					PROPERTY_HAS_VERSION);
		}

		return allVersionIds;
	}

	public static List<Integer> getAllVirtualOntologyIDs(OWLModel metadata,
			boolean includeSubclasses) {
		OWLNamedClass vOntClass = metadata
				.getOWLNamedClass(CLASS_VIRTUAL_ONTOLOGY);
		List<Integer> res = new ArrayList<Integer>();
		Collection<?> vOntologies = vOntClass.getInstances(includeSubclasses);
		for (Object vOnt : vOntologies) {
			if (vOnt instanceof RDFIndividual) {
				RDFIndividual vOntInst = (RDFIndividual) vOnt;
				try {
					Integer id = getId(metadata, vOntInst);
					res.add(id);
				} catch (Exception e) {
					log.error("Exception while getting ID of virtual ontology "
							+ vOntInst.getBrowserText());
				}
			} else {
				log.warn("Invalid instance of class "
						+ vOntClass.getBrowserText() + ": " + vOnt);
			}
		}
		return res;
	}

	public static List<Integer> getAllVirtualViewIDs(OWLModel metadata) {
		OWLNamedClass vViewClass = metadata
				.getOWLNamedClass(CLASS_VIRTUAL_VIEW);
		List<Integer> res = new ArrayList<Integer>();
		Collection<?> vViews = vViewClass.getInstances(true);
		for (Object vView : vViews) {
			if (vView instanceof RDFIndividual) {
				RDFIndividual vOntInst = (RDFIndividual) vView;
				try {
					Integer id = getId(metadata, vOntInst);
					res.add(id);
				} catch (Exception e) {
					log.error("Exception while getting ID of virtual view "
							+ vOntInst.getBrowserText());
				}
			} else {
				log.warn("Invalid instance of class "
						+ vViewClass.getBrowserText() + ": " + vView);
			}
		}
		return res;
	}

	public static List<Integer> getAllCategoryIDs(OWLModel metadata) {
		OWLNamedClass ontDomClass = metadata
				.getOWLNamedClass(CLASS_OMV_ONTOLOGY_DOMAIN);
		List<Integer> res = new ArrayList<Integer>();
		Collection<?> ontDomains = ontDomClass.getInstances(true);
		for (Object ontDomain : ontDomains) {
			if (ontDomain instanceof RDFIndividual) {
				RDFIndividual ontDomainInst = (RDFIndividual) ontDomain;
				try {
					Integer id = getId(metadata, ontDomainInst);
					res.add(id);
				} catch (Exception e) {
					log.error("Exception while getting ID of virtual view "
							+ ontDomainInst.getBrowserText());
				}
			} else {
				log.warn("Invalid instance of class "
						+ ontDomClass.getBrowserText() + ": " + ontDomain);
			}
		}
		return res;
	}

	public static List<Integer> getAllGroupIDs(OWLModel metadata) {
		OWLNamedClass ontGrpClass = metadata
				.getOWLNamedClass(CLASS_ONTOLOGY_GROUP);
		List<Integer> res = new ArrayList<Integer>();
		Collection<?> ontGroups = ontGrpClass.getInstances(true);
		for (Object ontGroup : ontGroups) {
			if (ontGroup instanceof RDFIndividual) {
				RDFIndividual ontGroupInst = (RDFIndividual) ontGroup;
				try {
					Integer id = getId(metadata, ontGroupInst);
					res.add(id);
				} catch (Exception e) {
					log.error("Exception while getting ID of virtual view "
							+ ontGroupInst.getBrowserText());
				}
			} else {
				log.warn("Invalid instance of class "
						+ ontGrpClass.getBrowserText() + ": " + ontGroup);
			}
		}
		return res;
	}

	public static int getNextAvailableVirtualOntologyId(OWLModel metadata) {
		try {
			Integer startId = Integer.valueOf(MessageUtils
					.getMessage("config.db.ontology.virtualOntologyIdStart"));
			Integer newId = getNextAvailableIdForClass(metadata
					.getOWLNamedClass(CLASS_VIRTUAL_ONTOLOGY), startId);
			while (getVirtualViewWithId(metadata, newId) != null) {
				newId = ((newId + 1500) / 1000) * 1000;
			}
			return newId;
		} catch (Exception e) {
			e.printStackTrace();
			return INVALID_ID;
		}
	}

	public static int getNextAvailableVirtualViewId(OWLModel metadata) {
		try {
			Integer startId = Integer.valueOf(MessageUtils
					.getMessage("config.db.ontology.virtualOntologyIdStart"));
			Integer newId = getNextAvailableIdForClass(metadata
					.getOWLNamedClass(CLASS_VIRTUAL_VIEW), startId);
			while (getVirtualOntologyWithId(metadata, newId) != null) {
				newId = ((newId + 1500) / 1000) * 1000;
			}
			return newId;
		} catch (Exception e) {
			e.printStackTrace();
			return INVALID_ID;
		}
	}

	public static int getNextAvailableOntologyVersionId(OWLModel metadata) {
		try {
			Integer startId = Integer.valueOf(MessageUtils
					.getMessage("config.db.ontology.ontologyVersionIdStart"));
			Integer newId = getNextAvailableIdForClass(metadata
					.getOWLNamedClass(CLASS_OMV_ONTOLOGY), startId);
			while (getOntologyViewWithId(metadata, newId) != null) {
				newId = ((newId + 1500) / 1000) * 1000;
			}
			return newId;
		} catch (Exception e) {
			e.printStackTrace();
			return INVALID_ID;
		}
	}

	public static int getNextAvailableOntologyViewVersionId(OWLModel metadata) {
		try {
			Integer startId = Integer.valueOf(MessageUtils
					.getMessage("config.db.ontology.ontologyVersionIdStart"));
			Integer newId = getNextAvailableIdForClass(metadata
					.getOWLNamedClass(CLASS_ONTOLOGY_VIEW), startId);
			while (getOntologyWithId(metadata, newId) != null) {
				newId = ((newId + 1500) / 1000) * 1000;
			}
			return newId;
		} catch (Exception e) {
			e.printStackTrace();
			return INVALID_ID;
		}
	}

	public static OWLIndividual getVirtualOntologyWithId(OWLModel metadata,
			Integer id) {
		return getIndividualWithId(metadata, CLASS_VIRTUAL_ONTOLOGY, id, false);
	}

	public static OWLIndividual getVirtualOntologyOrViewWithId(
			OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_VIRTUAL_ONTOLOGY, id, true);
	}

	public static OWLIndividual getVirtualViewWithId(OWLModel metadata,
			Integer id) {
		return getIndividualWithId(metadata, CLASS_VIRTUAL_VIEW, id, false);
	}

	public static OWLIndividual getOntologyWithId(OWLModel metadata, Integer id) {
		return getIndividualWithId(metadata, CLASS_OMV_ONTOLOGY, id, false);
	}

	public static OWLIndividual getOntologyOrViewWithId(OWLModel metadata,
			Integer id) {
		return getIndividualWithId(metadata, CLASS_OMV_ONTOLOGY, id, true);
	}

	public static OWLIndividual getOntologyViewWithId(OWLModel metadata,
			Integer id) {
		return getIndividualWithId(metadata, CLASS_ONTOLOGY_VIEW, id, false);
	}

	public static OntologyBean findLatestOntologyVersionByOboFoundryId(
			OWLModel metadata, String oboFoundryId, boolean onlyActive) {
		OntologyBean res = null;
		List<OWLIndividual> vOntIndividuals = getIndividualsWithMatchingProperty(
				metadata, CLASS_VIRTUAL_ONTOLOGY, PROPERTY_OBO_FOUNDRY_ID,
				oboFoundryId, false);// choose "true" if we will return also
		// ontology views (and rename methods
		// appropriately)

		if (vOntIndividuals != null && (!vOntIndividuals.isEmpty())) {
			if (vOntIndividuals.size() > 1) {
				log
						.error("Multiple virtual ontology individuals attached to ontology version: "
								+ oboFoundryId);
			}

			OWLIndividual vOntInd = vOntIndividuals.get(0);
			boolean isView = OntologyMetadataUtils
					.isVirtualViewIndividual(vOntInd);
			res = new OntologyBean(isView);

			try {
				OWLIndividual ontologyInd = OntologyMetadataUtils
						.getLatestVersion(vOntInd, onlyActive);

				if (ontologyInd == null) {
					fillInDummyOntologyBeanFromVirtualOntology(res, vOntInd);
				} else {
					fillInOntologyBeanFromInstance(res, ontologyInd);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception while getting the last version on "
						+ vOntInd, e);
				res = null;
			}
		}

		return res;
	}

	public static List<OWLIndividual> searchOntologyMetadata(OWLModel metadata,
			String query, boolean includeViews) {
		return searchMetadataOnClass(metadata, CLASS_OMV_ONTOLOGY, query,
				includeViews);
	}

	public static List<OWLIndividual> searchOntologyViewMetadata(
			OWLModel metadata, String query) {
		return searchMetadataOnClass(metadata, CLASS_ONTOLOGY_VIEW, query,
				false);
	}

	public static List<OWLIndividual> searchMetadataOnClass(OWLModel metadata,
			String class_name, String query, boolean includeSubclasses) {
		HashSet<OWLIndividual> res = new HashSet<OWLIndividual>();

		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_URL_PUBLICATIONS, query, includeSubclasses));
		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_URL_HOMEPAGE, query, includeSubclasses));
		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_HAS_CONTACT_EMAIL, query, includeSubclasses));
		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_HAS_CONTACT_NAME, query, includeSubclasses));
		// TODO check corresponding properties for displayLabel and format!
		res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
				PROPERTY_OMV_NAME, query, includeSubclasses));
		// res.addAll(getIndividualsWithMatchingProperty(metadata, class_name,
		// PROPERTY_format????, query, includeSubclasses));

		return new ArrayList<OWLIndividual>(res);
	}
}
