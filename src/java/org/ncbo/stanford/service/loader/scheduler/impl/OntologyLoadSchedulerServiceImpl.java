package org.ncbo.stanford.service.loader.scheduler.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.manager.diff.OntologyDiffManager;
import org.ncbo.stanford.manager.load.OntologyLoadManager;
import org.ncbo.stanford.manager.metrics.OntologyMetricsManager;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
import org.ncbo.stanford.service.ontology.AbstractOntologyService;
import org.ncbo.stanford.util.CompressionUtils;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
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
public class OntologyLoadSchedulerServiceImpl extends AbstractOntologyService
		implements OntologyLoadSchedulerService {

	private static final Log log = LogFactory
			.getLog(OntologyLoadSchedulerServiceImpl.class);
	private static final String ONTOLOGY_QUEUE_DOES_NOT_EXIST_ERROR = "No load queue record exists for the ontology";

	private Map<String, OntologyDiffManager> ontologyDiffHandlerMap = new HashMap<String, OntologyDiffManager>(
			0);
	private Map<String, OntologyMetricsManager> ontologyMetricsHandlerMap = new HashMap<String, OntologyMetricsManager>(
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
		List<Integer> errorVersionIdList = new ArrayList<Integer>(
				ontologyVersionIdList);

		for (Integer ontologyVersionId : ontologyVersionIdList) {
			try {
				ob = ontologyMetadataManager
						.findOntologyOrViewVersionById(ontologyVersionId);

				if (ob == null) {
					continue;
				}

				errorVersionIdList.remove(ontologyVersionId);

				NcboOntologyLoadQueue loadQueue = ncboOntologyLoadQueueDAO
						.findByOntologyVersionId(ontologyVersionId);

				if (loadQueue == null) {
					String error = addErrorOntology(errorOntologies,
							ontologyVersionId.toString(), ob,
							ONTOLOGY_QUEUE_DOES_NOT_EXIST_ERROR);
					log.error(error);
				} else {
					processRecord(loadQueue, formatHandler, ob);
				}
			} catch (Exception e) {
				addErrorOntology(errorOntologies, ontologyVersionId.toString(),
						null, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}

		optimizeIndex();

		for (Integer errorVersionId : errorVersionIdList) {
			String error = addErrorOntology(errorOntologies, errorVersionId
					.toString(), null, ONTOLOGY_VERSION_DOES_NOT_EXIST_ERROR);
			log.error(error);
		}
	}

	/**
	 * Parse a single record from the ontology load queue
	 * 
	 * @param loadQueue
	 * @param formatHandler
	 */
	public void processRecord(NcboOntologyLoadQueue loadQueue,
			String formatHandler, OntologyBean ontologyBean) {
		String errorMessage = null;
		Integer ontologyVersionId = loadQueue.getOntologyVersionId();
		StatusEnum status = StatusEnum.STATUS_ERROR;

		// parse
		try {
			// in some cases the ontologyBean is passed as null, so we need to
			// retrieve it from backend
			if (ontologyBean == null) {
				ontologyBean = ontologyMetadataManager
						.findOntologyOrViewVersionById(ontologyVersionId);
			}

			if (ontologyBean == null) {
				throw new OntologyNotFoundException(
						OntologyNotFoundException.DEFAULT_MESSAGE
								+ " (Version Id: " + ontologyVersionId + ")");
			}

			status = StatusEnum.STATUS_WAITING;
			updateOntologyStatus(loadQueue, ontologyBean, formatHandler,
					status, errorMessage);

			List<String> filenames = ontologyBean.getFilenames();

			if (filenames.isEmpty()) {
				status = StatusEnum.STATUS_ERROR;
				updateOntologyStatus(
						loadQueue,
						ontologyBean,
						formatHandler,
						status,
						MessageUtils
								.getMessage("msg.error.noontologyfilessubmitted"));

				String error = addErrorOntology(
						errorOntologies,
						ontologyVersionId.toString(),
						ontologyBean,
						MessageUtils
								.getMessage("msg.error.noontologyfilessubmitted"));
				log.error(error);
			} else {
				// set the status as "Parsing"
				status = StatusEnum.STATUS_PARSING;
				updateOntologyStatus(loadQueue, ontologyBean, formatHandler,
						status, errorMessage);

				// load ontology
				loadOntology(ontologyBean, formatHandler);

				status = StatusEnum.STATUS_READY;
				updateOntologyStatus(loadQueue, ontologyBean, formatHandler,
						status, errorMessage);

				// calculate ontology metrics
				calculateMetrics(ontologyBean, formatHandler);

				// create a diff of this version and the previous one
				createDiff (ontologyBean);
			}

			// index ontology but only when the loader (formatHandler) is not
			// explicitly overridden
			if (isDefaultFormatHandler(ontologyBean, formatHandler)
					&& status == StatusEnum.STATUS_READY) {
				errorMessage = indexOntology(errorMessage, ontologyBean);
				updateOntologyStatus(loadQueue, ontologyBean, formatHandler,
						status, errorMessage);
			}
		} catch (Exception e) {
			status = StatusEnum.STATUS_ERROR;
			errorMessage = getLongErrorMessage(e);
			addErrorOntology(errorOntologies, ontologyVersionId.toString(),
					ontologyBean, errorMessage);
			e.printStackTrace();
			log.error(e);

			try {
				updateOntologyStatus(loadQueue, ontologyBean, formatHandler,
						status, errorMessage);
			} catch (Exception e1) {
				e.printStackTrace();
				log.error("Unable to update ontology status due to error: "
						+ e.getMessage());
			}
		}
	}

	private String indexOntology(String errorMessage, OntologyBean ontologyBean) {
		try {
			indexService.indexOntology(ontologyBean.getOntologyId(), false,
					false);
		} catch (Exception e) {
			errorMessage = appendError(errorMessage, e);
			addErrorOntology(errorOntologies, ontologyBean.getId().toString(),
					ontologyBean, errorMessage);
			e.printStackTrace();
			log.error(e);
		}

		return errorMessage;
	}

	private void updateOntologyStatus(NcboOntologyLoadQueue loadQueue,
			OntologyBean ontologyBean, String formatHandler, StatusEnum status,
			String errorMessage) throws Exception {
		if (isDefaultFormatHandler(ontologyBean, formatHandler)) {
			NcboLStatus ncboStatus = new NcboLStatus();
			Integer statusId = status.getStatus();
			ncboStatus.setId(statusId);
			// update ontology metadata
			ontologyBean.setStatusId(statusId);

			ontologyMetadataManager.saveOntologyOrView(ontologyBean);

			// update loadQueue table
			loadQueue.setErrorMessage(errorMessage);
			loadQueue.setDateProcessed(Calendar.getInstance().getTime());
			loadQueue.setNcboLStatus(ncboStatus);
			ncboOntologyLoadQueueDAO.saveNcboOntologyLoadQueue(loadQueue);
		}
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
			filename = filename.replace(ApplicationConstants.DIR, "");

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
				|| !ontologyLoadHandlerMap.containsKey(formatHandler)) {
			formatHandler = ontologyFormatHandlerMap.get(ontologyBean
					.getFormat());
		}

		OntologyLoadManager loadManager = ontologyLoadHandlerMap
				.get(formatHandler);

		if (loadManager == null) {
			String msg = "Cannot find formatHandler for "
					+ ontologyBean.getFormat();
			log.error(msg);
			throw new InvalidOntologyFormatException(msg);
		}

		return loadManager;
	}

	private boolean isDefaultFormatHandler(OntologyBean ontologyBean,
			String formatHandler) {
		return (formatHandler == null || formatHandler
				.equals(ontologyFormatHandlerMap.get(ontologyBean.getFormat())));
	}

	/**
	 * Calculate ontology metrics for the specified ontology. The minimum
	 * requirement is that the ontology is parsed and exists in the Bioportal
	 * repository.
	 * 
	 * @param ontologyBean
	 * @param formatHandler
	 * @throws Exception
	 */
	private void calculateMetrics(OntologyBean ontologyBean,
			String formatHandler) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("calculateMetrics BEGIN..............");
		}

		OntologyMetricsBean metricsBean = getMetricsManager(ontologyBean)
				.extractOntologyMetrics(ontologyBean);

		metricsService.updateOntologyMetrics(ontologyBean, metricsBean);

		if (log.isDebugEnabled()) {
			log.debug("..................calculateMetrics END");
		}
	}

	private OntologyMetricsManager getMetricsManager(OntologyBean ontologyBean)
			throws Exception {
		String formatHandler = ontologyFormatHandlerMap.get(ontologyBean
				.getFormat());
		OntologyMetricsManager metricsManager = ontologyMetricsHandlerMap
				.get(formatHandler);

		if (metricsManager == null) {
			log.error("Cannot find metricsManager for "
					+ ontologyBean.getFormat());
			throw new InvalidOntologyFormatException(
					"Cannot find formatHandler for " + ontologyBean.getFormat());
		}

		return metricsManager;
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

		getDiffManager(ontologyBean)
				.createDiffForLatestActiveOntologyVersionPair(
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
	 * @param ontologyMetricsHandlerMap
	 *            the ontologyMetricsHandlerMap to set
	 */
	public void setOntologyMetricsHandlerMap(
			Map<String, OntologyMetricsManager> ontologyMetricsHandlerMap) {
		this.ontologyMetricsHandlerMap = ontologyMetricsHandlerMap;
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
