package org.ncbo.stanford.service.loader.scheduler.impl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.manager.diff.OntologyDiffManager;
import org.ncbo.stanford.manager.load.OntologyLoadManager;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.util.CompressionUtils;
import org.ncbo.stanford.util.MessageUtils;
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
	private static final int ERROR_MESSAGE_LENGTH = 1000;

	private IndexSearchService indexService;
	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
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
	 * 
	 * @throws Exception
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
						+ loadQueue.getNcboOntologyVersion().getId());
				processRecord(loadQueue);
			} else {
				log.debug("ontology ID = "
						+ loadQueue.getNcboOntologyVersion().getId()
						+ " does not require parsing");
			}
		}

		optimizeIndex();
	}

	/**
	 * Parses a single ontology
	 * 
	 * @param ontologyVersionId
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void parseOntology(String ontologyVersionId) {
		errorOntologies.clear();
		VNcboOntology ontologyVersion = ncboOntologyVersionDAO
				.findOntologyVersion(Integer.parseInt(ontologyVersionId));

		// null check
		if (ontologyVersion == null) {
			log.error("Error - parseOntology(): Ontology Version ID "
					+ ontologyVersionId + " does not exist!");
			return;
		}

		parseOntology(ontologyVersion);
		optimizeIndex();
	}

	@Transactional(propagation = Propagation.NEVER)
	public void parseOntologies(List<Integer> ontologyVersionIdList) {
		errorOntologies.clear();
		List<VNcboOntology> ontologies = ncboOntologyVersionDAO
				.findOntologyVersions(ontologyVersionIdList);

		for (VNcboOntology ontologyVersion : ontologies) {
			parseOntology(ontologyVersion);
		}

		optimizeIndex();
	}

	private void parseOntology(VNcboOntology ontologyVersion) {
		Set<NcboOntologyLoadQueue> loadQueues = ontologyVersion
				.getNcboOntologyLoadQueues();

		if (loadQueues.isEmpty()) {
			log
					.error("Error - parseOntology(): There is no loadQueue for Ontology: "
							+ getOntologyDisplay(ontologyVersion));
		} else {
			NcboOntologyLoadQueue loadQueue = (NcboOntologyLoadQueue) loadQueues
					.toArray()[0];
			processRecord(loadQueue);
		}
	}

	private String getOntologyDisplay(VNcboOntology ontologyVersion) {
		return ontologyVersion.getDisplayLabel() + "(Id: "
				+ ontologyVersion.getId() + ")";
	}

	/**
	 * Parse a single record from the ontology load queue
	 * 
	 * @param rec
	 */
	private void processRecord(NcboOntologyLoadQueue loadQueue) {
		String errorMessage = null;
		// populate bean
		VNcboOntology ontologyVersion = ncboOntologyVersionDAO
				.findOntologyVersion(loadQueue.getNcboOntologyVersion().getId());
		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.populateFromEntity(ontologyVersion);

		StatusEnum status = StatusEnum.STATUS_WAITING;
		updateOntologyStatus(loadQueue, status, errorMessage);

		// parse
		try {
			List<String> filenames = ontologyBean.getFilenames();

			if (filenames.isEmpty()) {
				status = StatusEnum.STATUS_ERROR;

				updateOntologyStatus(loadQueue, status, MessageUtils
						.getMessage("msg.error.noontologyfilessubmitted"));
			} else {
				// set the status as "Parsing"
				status = StatusEnum.STATUS_PARSING;
				updateOntologyStatus(loadQueue, status, errorMessage);

				// load ontology
				loadOntology(ontologyBean);

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
		} catch (Exception e) {
			status = StatusEnum.STATUS_ERROR;
			errorMessage = getErrorMessage(e);

			// add OntologyVersionId to the error list
			errorOntologies.add(getOntologyDisplay(ontologyVersion));
			e.printStackTrace();
			log.error(e);
		}

		updateOntologyStatus(loadQueue, status, errorMessage);

		// index ontology
		if (status == StatusEnum.STATUS_READY) {
			errorMessage = indexOntology(errorMessage, ontologyVersion,
					ontologyBean);
			updateOntologyStatus(loadQueue, status, errorMessage);
		}
	}

	private String indexOntology(String errorMessage,
			VNcboOntology ontologyVersion, OntologyBean ontologyBean) {
		try {
			indexService.indexOntology(ontologyBean.getOntologyId(), false,
					false);
		} catch (Exception e) {
			int halfError = ERROR_MESSAGE_LENGTH / 2 - 2;
			String newErrorMessage = getErrorMessage(e);

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

			errorOntologies.add(getOntologyDisplay(ontologyVersion));
			e.printStackTrace();
			log.error(e);
		}

		return errorMessage;
	}

	private String getErrorMessage(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString();
		int stackTraceLen = stackTrace.length();
		int messageLen = (stackTraceLen < ERROR_MESSAGE_LENGTH) ? stackTraceLen
				: ERROR_MESSAGE_LENGTH;

		return stackTrace.substring(0, messageLen);
	}

	private void updateOntologyStatus(NcboOntologyLoadQueue loadQueue,
			StatusEnum status, String errorMessage) {
		NcboOntologyVersion ncboOntologyVersion = loadQueue
				.getNcboOntologyVersion();

		NcboLStatus ncboStatus = new NcboLStatus();
		ncboStatus.setId(status.getStatus());

		// update ontologyVersion table
		ncboOntologyVersion.setNcboLStatus(ncboStatus);
		ncboOntologyVersionDAO.saveOntologyVersion(ncboOntologyVersion);

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
	 * @throws URISyntaxException
	 */
	private void loadOntology(OntologyBean ontologyBean) throws Exception {
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

				getLoadManager(ontologyBean).loadOntology(file.toURI(),
						ontologyBean);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("..................loadOntology END");
		}
	}

	private OntologyLoadManager getLoadManager(OntologyBean ontologyBean)
			throws Exception {
		String formatHandler = ontologyFormatHandlerMap.get(ontologyBean
				.getFormat());
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
	 * @param ncboOntologyVersionDAO
	 *            the ncboOntologyVersionDAO to set
	 */
	public void setNcboOntologyVersionDAO(
			CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
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
}
