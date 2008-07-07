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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.manager.OntologyLoadManager;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
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
	private static final int ERROR_MESSAGE_LENGTH = 1000;

	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>();
	private Map<String, OntologyLoadManager> ontologyLoadHandlerMap = new HashMap<String, OntologyLoadManager>();
	private List<Integer> errorIdList = new ArrayList<Integer>();

	/**
	 * Gets the list of ontologies that need to be loaded and process each using
	 * the appropriate loader API.
	 * 
	 * @throws Exception
	 */
	public void parseOntologies() {
		List<NcboOntologyLoadQueue> ontologiesToLoad = ncboOntologyLoadQueueDAO
				.getOntologiesToLoad();

		for (NcboOntologyLoadQueue loadQueue : ontologiesToLoad) {
			processRecord(loadQueue);
		}
	}

	/**
	 * Gets the list of ontologies that need to be loaded and process each using
	 * the appropriate loader API.
	 * 
	 * @throws Exception
	 */
	public void parseOntologies(String startId, String endId) {
		int start = Integer.parseInt(startId);
		int end = Integer.parseInt(endId);

		List<NcboOntologyLoadQueue> ontologiesToLoad = ncboOntologyLoadQueueDAO
				.getOntologiesToLoad();

		for (NcboOntologyLoadQueue loadQueue : ontologiesToLoad) {

			int currentId = loadQueue.getNcboOntologyVersion().getId();
			if (currentId >= start && currentId < end) {
				log.debug("parsing ontology : ID = "
						+ loadQueue.getNcboOntologyVersion().getId());
				processRecord(loadQueue);
			}
		}
	}

	/**
	 * Parses a single ontology
	 * 
	 * @param ontologyVersionId
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void parseOntology(String ontologyVersionId) {
		NcboOntologyVersion ontologyVersion = ncboOntologyVersionDAO
				.findById(Integer.parseInt(ontologyVersionId));

		// null check
		if (ontologyVersion == null) {
			log.error("Error - parseOntology(): Ontology Version ID "
					+ ontologyVersionId + " does not exist!");
			return;
		}

		NcboOntologyLoadQueue loadQueue = (NcboOntologyLoadQueue) ontologyVersion
				.getNcboOntologyLoadQueues().toArray()[0];

		// null check
		if (loadQueue == null) {
			log
					.error("Error - parseOntology(): There is no loadQueue for Ontology Version ID "
							+ ontologyVersionId);
			return;
		}

		processRecord(loadQueue);
	}

	/**
	 * Parse a single record from the ontology load queue
	 * 
	 * @param rec
	 */
	@Transactional(propagation = Propagation.NEVER)
	private void processRecord(NcboOntologyLoadQueue loadQueue) {
		String errorMessage = null;
		// populate bean
		NcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(loadQueue.getNcboOntologyVersion().getId());
		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.populateFromEntity(ontology);

		// cleanup lexgrid (no need for protege)
		// TODO - call lexgrid clean up API... something like this
		// getLoadManager(ontologyBean).cleanup();

		// cleanup bioportal
		Integer status = new Integer(StatusEnum.STATUS_WAITING.getStatus());
		updateOntologyStatus(loadQueue, status, errorMessage);

		// parse
		try {
			// set the status as "Parsing"
			status = new Integer(StatusEnum.STATUS_PARSING.getStatus());
			updateOntologyStatus(loadQueue, status, errorMessage);

			// load ontology
			loadOntology(ontologyBean);

			status = new Integer(StatusEnum.STATUS_READY.getStatus());
		} catch (Exception e) {
			status = new Integer(StatusEnum.STATUS_ERROR.getStatus());

			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();
			int stackTraceLen = stackTrace.length();
			int messageLen = (stackTraceLen < ERROR_MESSAGE_LENGTH) ? stackTraceLen
					: ERROR_MESSAGE_LENGTH;
			errorMessage = stackTrace.substring(0, messageLen);

			// add OntologyVersionId to the error list
			errorIdList.add(ontologyBean.getId());
			e.printStackTrace();
			log.error(e);
		}

		updateOntologyStatus(loadQueue, status, errorMessage);
	}

	private void updateOntologyStatus(NcboOntologyLoadQueue loadQueue,
			Integer status, String errorMessage) {
		NcboOntologyVersion ncboOntologyVersion = loadQueue
				.getNcboOntologyVersion();

		NcboLStatus ncboStatus = new NcboLStatus();
		ncboStatus.setId(status);

		// update ontologyVersion table
		ncboOntologyVersion.setNcboLStatus(ncboStatus);
		ncboOntologyVersionDAO.saveOntologyVersion(ncboOntologyVersion);

		// update loadQueue table

		if (!StringHelper.isNullOrNullString(errorMessage)) {
			loadQueue.setErrorMessage(errorMessage);
		}

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
		log.debug("loadOntology BEGIN..............");

		List<String> filenames = ontologyBean.getFilenames();

		// for UMLS, zip file, untar it, pass dir to LexGrid
		// pass zip to LexGrid
		// looping through the files

		for (String filename : filenames) {
			log.debug("......loading filename " + filename);
			String filePath = AbstractFilePathHandler.getOntologyFilePath(
					ontologyBean, filename);
			File file = new File(filePath);

			getLoadManager(ontologyBean).loadOntology(file.toURI(),
					ontologyBean);
		}

		log.debug("..................loadOntology END");
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
	 * @return the ncboOntologyLoadQueueDAO
	 */
	public CustomNcboOntologyLoadQueueDAO getNcboOntologyLoadQueueDAO() {
		return ncboOntologyLoadQueueDAO;
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
	 * @return the ncboOntologyVersionDAO
	 */
	public CustomNcboOntologyVersionDAO getNcboOntologyVersionDAO() {
		return ncboOntologyVersionDAO;
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
	 * @return the ontologyLoadHandlerMap
	 */
	public Map<String, OntologyLoadManager> getOntologyLoadHandlerMap() {
		return ontologyLoadHandlerMap;
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
	 * @return the ontologyFormatHandlerMap
	 */
	public Map<String, String> getOntologyFormatHandlerMap() {
		return ontologyFormatHandlerMap;
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
	 * @return the errorIdList
	 */
	public List<Integer> getErrorIdList() {
		return errorIdList;
	}

	/**
	 * @param errorIdList
	 *            the errorIdList to set
	 */
	public void setErrorIdList(List<Integer> errorIdList) {
		this.errorIdList = errorIdList;
	}
}
