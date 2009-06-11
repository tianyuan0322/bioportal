package org.ncbo.stanford.service.loader.scheduler.impl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.manager.diff.OntologyDiffManager;
import org.ncbo.stanford.manager.load.OntologyLoadManager;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.manager.metadata.OntologyViewMetadataManager;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.util.CompressionUtils;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the scheduler service that runs periodically, checking for
 * new ontologies that have been loaded into the system but not yet parsed by
 * the API.
 * 
 * @author Michael Dorf
 * 
 */
@Transactional
public class OntologyLoadSchedulerServiceImpl implements
		OntologyLoadSchedulerService {

	private static final Log log = LogFactory
			.getLog(OntologyLoadSchedulerServiceImpl.class);
	private static final int LONG_ERROR_MESSAGE_LENGTH = 1000;
	private static final int SHORT_ERROR_MESSAGE_LENGTH = 100;
	private static final String ONTOLOGY_VERSION_DOES_NOT_EXIST_ERROR = "Ontology version with the given id does not exist";
	private static final String ONTOLOGY_QUEUE_DOES_NOT_EXIST_ERROR = "No load queue record exists for the ontology";

	private IndexSearchService indexService;
	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private OntologyMetadataManager ontologyMetadataManagerProtege;
	private OntologyViewMetadataManager ontologyViewMetadataManagerProtege;

	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	private Map<String, OntologyLoadManager> ontologyLoadHandlerMap = new HashMap<String, OntologyLoadManager>(
			0);
	private List<String> errorOntologies = new ArrayList<String>(0);
	private Map<String, OntologyDiffManager> ontologyDiffHandlerMap = new HashMap<String, OntologyDiffManager>(
			0);

	/**
	 * Gets the list of ontologies that need to be loaded and processed each
	 * using the appropriate loader API.
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void parseOntologies() {
		errorOntologies.clear();
		List<NcboOntologyLoadQueue> ontologiesToLoad = ncboOntologyLoadQueueDAO
				.getOntologiesToLoad();

		backupIndex();

		for (NcboOntologyLoadQueue loadQueue : ontologiesToLoad) {
			if (ncboOntologyLoadQueueDAO.needsParsing(loadQueue.getId())
					.booleanValue()) {
				log.debug("parsing ontology: ID = "
						+ loadQueue.getOntologyVersionId());
				processRecord(loadQueue, null, null);
			} else {
				log.debug("ontology ID = " + loadQueue.getOntologyVersionId()
						+ " does not require parsing");
			}
		}

		optimizeIndex();
	}

	@Transactional(propagation = Propagation.NEVER)
	public void parseOntologies(List<Integer> ontologyVersionIdList,
			String formatHandler) {
		OntologyBean ob = null;
		errorOntologies.clear();
		List<Integer> errorVersionIdList = new ArrayList<Integer>(ontologyVersionIdList);
		
		for (Integer ontologyVersionId : ontologyVersionIdList) {
			try {
				ob = ontologyMetadataManagerProtege
						.findOntologyOrOntologyViewById(ontologyVersionId);

				if (ob == null) {
					continue;
				}

				errorVersionIdList.remove(ontologyVersionId);

				NcboOntologyLoadQueue loadQueue = ncboOntologyLoadQueueDAO
						.findByOntologyVersionId(ontologyVersionId);

				if (loadQueue == null) {
					String error = addErrorOntology(ontologyVersionId
							.toString(), ob,
							ONTOLOGY_QUEUE_DOES_NOT_EXIST_ERROR);
					log.error(error);
				} else {
					processRecord(loadQueue, formatHandler, ob);
				}
			} catch (Exception e) {
				addErrorOntology(ontologyVersionId.toString(), null, e
						.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}

		optimizeIndex();

		for (Integer errorVersionId : errorVersionIdList) {
			String error = addErrorOntology(errorVersionId.toString(), null,
					ONTOLOGY_VERSION_DOES_NOT_EXIST_ERROR);
			log.error(error);
		}
	}

	private String getOntologyDisplay(String ontologyVersionId,
			OntologyBean ontologyVersion) {
		String ontologyDisplay = "(Id: " + ontologyVersionId + ")";

		if (ontologyVersion != null) {
			ontologyDisplay = ontologyVersion.getDisplayLabel() + " "
					+ ontologyDisplay;
		}

		return ontologyDisplay;
	}

	/**
	 * Parse a single record from the ontology load queue
	 * 
	 * @param loadQueue
	 * @param formatHandler
	 */
	private void processRecord(NcboOntologyLoadQueue loadQueue,
			String formatHandler, OntologyBean ontologyBean) {
		String errorMessage = null;
		Integer ontologyVersionId = loadQueue.getOntologyVersionId();
		StatusEnum status = StatusEnum.STATUS_ERROR;

		// parse
		try {
			// in some cases the ontologyBean is passed as null, so we need to
			// retrieve it from backend
			if (ontologyBean == null) {
				ontologyBean = ontologyMetadataManagerProtege
						.findOntologyOrOntologyViewById(ontologyVersionId);
			}

			if (ontologyBean == null) {
				throw new OntologyNotFoundException(
						OntologyNotFoundException.DEFAULT_MESSAGE
								+ " (Version Id: " + ontologyVersionId + ")");
			}

			status = StatusEnum.STATUS_WAITING;

			updateOntologyStatus(loadQueue, ontologyBean, status, errorMessage);

			List<String> filenames = ontologyBean.getFilenames();

			if (filenames.isEmpty()) {
				status = StatusEnum.STATUS_ERROR;

				updateOntologyStatus(
						loadQueue,
						ontologyBean,
						status,
						MessageUtils
								.getMessage("msg.error.noontologyfilessubmitted"));
			} else {
				// set the status as "Parsing"
				status = StatusEnum.STATUS_PARSING;
				updateOntologyStatus(loadQueue, ontologyBean, status,
						errorMessage);

				// load ontology
				loadOntology(ontologyBean, formatHandler);

				status = StatusEnum.STATUS_READY;

				// ******************************************
				// We will call create Diff when we are ready to include this
				// process in the scheduler
				// Commenting it out for now
				//
				// createDiff (ontologyBean);
				//
				// ************************************
			}

			updateOntologyStatus(loadQueue, ontologyBean, status, errorMessage);

			// index ontology
			if (status == StatusEnum.STATUS_READY) {
				errorMessage = indexOntology(errorMessage, ontologyBean);
				updateOntologyStatus(loadQueue, ontologyBean, status,
						errorMessage);
			}
		} catch (Exception e) {
			status = StatusEnum.STATUS_ERROR;
			errorMessage = getLongErrorMessage(e);
			addErrorOntology(ontologyVersionId.toString(), ontologyBean,
					errorMessage);
			e.printStackTrace();
			log.error(e);
		}
	}

	private String indexOntology(String errorMessage, OntologyBean ontologyBean) {
		try {
			indexService.indexOntology(ontologyBean.getOntologyId(), false,
					false);
		} catch (Exception e) {
			int halfError = LONG_ERROR_MESSAGE_LENGTH / 2 - 2;
			String newErrorMessage = getLongErrorMessage(e);

			if (errorMessage != null) {
				errorMessage = (errorMessage.length() > halfError) ? errorMessage
						.substring(0, halfError)
						: errorMessage;

				newErrorMessage = "\n\n"
						+ ((newErrorMessage.length() > halfError) ? newErrorMessage
								.substring(0, halfError)
								: newErrorMessage);
			} else {
				errorMessage = "";
			}

			errorMessage += newErrorMessage;
			addErrorOntology(ontologyBean.getId().toString(), ontologyBean,
					errorMessage);
			e.printStackTrace();
			log.error(e);
		}

		return errorMessage;
	}

	private String addErrorOntology(String ontologyVersionId,
			OntologyBean ontologyVersion, String errorMessage) {
		String error = getOntologyDisplay(ontologyVersionId, ontologyVersion);

		if (!StringHelper.isNullOrNullString(errorMessage)) {
			int errorLen = errorMessage.length();
			int messageLen = (errorLen < SHORT_ERROR_MESSAGE_LENGTH) ? errorLen
					: SHORT_ERROR_MESSAGE_LENGTH;

			error += " - " + errorMessage.substring(0, messageLen);
		}

		errorOntologies.add(error);

		return error;
	}

	private String getLongErrorMessage(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString();
		int stackTraceLen = stackTrace.length();
		int messageLen = (stackTraceLen < LONG_ERROR_MESSAGE_LENGTH) ? stackTraceLen
				: LONG_ERROR_MESSAGE_LENGTH;

		return stackTrace.substring(0, messageLen);
	}

	private void updateOntologyStatus(NcboOntologyLoadQueue loadQueue,
			OntologyBean ontologyBean, StatusEnum status, String errorMessage)
			throws Exception {
		NcboLStatus ncboStatus = new NcboLStatus();
		Integer statusId = status.getStatus();
		ncboStatus.setId(statusId);

		// update ontology metadata
		ontologyBean.setStatusId(statusId);

		if (ontologyBean.isView()) {
			ontologyViewMetadataManagerProtege
					.saveOntologyView((OntologyViewBean) ontologyBean);
		} else {
			ontologyMetadataManagerProtege.saveOntology(ontologyBean);
		}

		// update loadQueue table
		loadQueue.setErrorMessage(errorMessage);
		loadQueue.setDateProcessed(Calendar.getInstance().getTime());
		loadQueue.setNcboLStatus(ncboStatus);
		ncboOntologyLoadQueueDAO.saveNcboOntologyLoadQueue(loadQueue);
	}

	/**
	 * Loads the specified ontology into the BioPortal repository. The minimum
	 * requirement is that the ontology file/uri exists and is in the right
	 * format. If the ontology id already exists, the invocation assumes
	 * overwrite of the existing ontology.
	 * 
	 * @param ontologyBean
	 *            bean
	 * @param formatHandler
	 * @throws Exception
	 */
	private void loadOntology(OntologyBean ontologyBean, String formatHandler)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("loadOntology BEGIN..............");
		}

		List<String> filenames = ontologyBean.getFilenames();

		// for UMLS, pass empty string to LexGrid to indicate a directory
		for (String filename : filenames) {
			if (!CompressionUtils.isCompressed(filename)) {
				log.debug("......loading filename " + filename);
				String filePath = AbstractFilePathHandler.getOntologyFilePath(
						ontologyBean, filename);
				File file = new File(filePath);

				getLoadManager(ontologyBean, formatHandler).loadOntology(
						file.toURI(), ontologyBean);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("..................loadOntology END");
		}
	}

	/**
	 * Return the appropriate ontology load manager based on either the passed
	 * in formatHandler or a default handler for a given ontology format
	 * 
	 * @param ontologyBean
	 * @param formatHandler
	 * @return
	 * @throws Exception
	 */
	private OntologyLoadManager getLoadManager(OntologyBean ontologyBean,
			String formatHandler) throws Exception {
		if (formatHandler == null
				|| !ontologyFormatHandlerMap.containsKey(formatHandler)) {
			formatHandler = ontologyFormatHandlerMap.get(ontologyBean
					.getFormat());
		}

		OntologyLoadManager loadManager = ontologyLoadHandlerMap
				.get(formatHandler);

		if (loadManager == null) {
			log.error("Cannot find formatHandler for "
					+ ontologyBean.getFormat());
			throw new InvalidOntologyFormatException(
					"Cannot find formatHandler for " + ontologyBean.getFormat());
		}

		return loadManager;
	}

	/**
	 * Creates a diff between the two latest versions of the specified ontology
	 * This method is called after the ontology has been successfully parsed.
	 * So, one version is the bean that is being passed in
	 * 
	 * @param ontologyBean
	 *            bean
	 * @throws Exception
	 */
	private void createDiff(OntologyBean ontologyBean) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("createDiff BEGIN..............");
		}

		getDiffManager(ontologyBean).createDiffForTwoLatestVersions(
				ontologyBean.getOntologyId());

		if (log.isDebugEnabled()) {
			log.debug("..................createDiff END");
		}
	}

	private OntologyDiffManager getDiffManager(OntologyBean ontologyBean)
			throws Exception {
		String formatHandler = ontologyFormatHandlerMap.get(ontologyBean
				.getFormat());
		OntologyDiffManager diffManager = ontologyDiffHandlerMap
				.get(formatHandler);

		if (diffManager == null) {
			log
					.error("Cannot find diffHandler for "
							+ ontologyBean.getFormat());
			throw new InvalidOntologyFormatException(
					"Cannot find formatHandler for " + ontologyBean.getFormat());
		}

		return diffManager;
	}

	private void backupIndex() {
		try {
			indexService.backupIndex();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to backup index due to: " + e);
		}
	}

	private void optimizeIndex() {
		try {
			indexService.optimizeIndex();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to optimize index due to: " + e);
		}
	}

	/**
	 * @param indexService
	 *            the indexService to set
	 */
	public void setIndexService(IndexSearchService indexService) {
		this.indexService = indexService;
	}

	/**
	 * @param ncboOntologyLoadQueueDAO
	 *            the ncboOntologyLoadQueueDAO to set
	 */
	public void setNcboOntologyLoadQueueDAO(
			CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO) {
		this.ncboOntologyLoadQueueDAO = ncboOntologyLoadQueueDAO;
	}

	/**
	 * @param ontologyLoadHandlerMap
	 *            the ontologyLoadHandlerMap to set
	 */
	public void setOntologyLoadHandlerMap(
			Map<String, OntologyLoadManager> ontologyLoadHandlerMap) {
		this.ontologyLoadHandlerMap = ontologyLoadHandlerMap;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

	/**
	 * @return the errorOntologies
	 */
	public List<String> getErrorOntologies() {
		return errorOntologies;
	}

	/**
	 * @param ontologyDiffHandlerMap
	 *            the ontologyDiffHandlerMap to set
	 */
	public void setOntologyDiffHandlerMap(
			Map<String, OntologyDiffManager> ontologyDiffHandlerMap) {
		this.ontologyDiffHandlerMap = ontologyDiffHandlerMap;
	}

	/**
	 * @param ontologyMetadataManagerProtege
	 *            the ontologyMetadataManagerProtege to set
	 */
	public void setOntologyMetadataManagerProtege(
			OntologyMetadataManager ontologyMetadataManagerProtege) {
		this.ontologyMetadataManagerProtege = ontologyMetadataManagerProtege;
	}

	/**
	 * @param ontologyViewMetadataManagerProtege
	 *            the ontologyViewMetadataManagerProtege to set
	 */
	public void setOntologyViewMetadataManagerProtege(
			OntologyViewMetadataManager ontologyViewMetadataManagerProtege) {
		this.ontologyViewMetadataManagerProtege = ontologyViewMetadataManagerProtege;
	}
}
