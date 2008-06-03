package org.ncbo.stanford.service.loader.scheduler.impl;

import java.io.File;
import java.net.URISyntaxException;
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
import org.ncbo.stanford.manager.OntologyLoadManager;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
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

	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>();
	private Map<String, OntologyLoadManager> ontologyLoadHandlerMap = new HashMap<String, OntologyLoadManager>();
	private String ontologyFilePath;

	/**
	 * Gets the list of ontologies that need to be loaded and process each using
	 * the appropriate loader API.
	 * 
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void parseOntologies() {
		List<NcboOntologyLoadQueue> ontologiesToLoad = ncboOntologyLoadQueueDAO
				.getOntologiesToLoad();

		for (NcboOntologyLoadQueue rec : ontologiesToLoad) {
			processRecord(rec);
		}
	}

	/**
	 * Parses a single ontology
	 * 
	 * @param ontologyVersionId
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void parseOntology(Integer ontologyVersionId) {
		NcboLStatus status = new NcboLStatus();

		NcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);
		NcboOntologyVersion ver = ncboOntologyVersionDAO
				.findById(ontologyVersionId);
		NcboOntologyLoadQueue queueRec = (NcboOntologyLoadQueue) ver
				.getNcboOntologyLoadQueues().toArray()[0];

		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.populateFromEntity(ontology);

		status.setId(StatusEnum.STATUS_PARSING.getStatus());
		ver.setNcboLStatus(status);

		status.setId(StatusEnum.STATUS_PARSING.getStatus());
		ver.setNcboLStatus(status);
		ncboOntologyVersionDAO.saveOntologyVersion(ver);

		if (queueRec != null) {
			queueRec.setNcboLStatus(status);
			ncboOntologyLoadQueueDAO.saveNcboOntologyLoadQueue(queueRec);
		}

		try {
			loadOntology(ontologyBean);

			status = new NcboLStatus();
			status.setId(StatusEnum.STATUS_READY.getStatus());
		} catch (Exception e) {
			status = new NcboLStatus();
			status.setId(StatusEnum.STATUS_ERROR.getStatus());
			log.error(e);
			e.printStackTrace();
		}

		ver.setNcboLStatus(status);
		ncboOntologyVersionDAO.saveOntologyVersion(ver);

		if (queueRec != null) {
			queueRec.setNcboLStatus(status);
			ncboOntologyLoadQueueDAO.saveNcboOntologyLoadQueue(queueRec);
		}
	}

	/**
	 * Parse a single record from the ontology load queue
	 * 
	 * @param rec
	 */
	private void processRecord(NcboOntologyLoadQueue rec) {
		NcboLStatus status = new NcboLStatus();
		NcboOntologyVersion ver = rec.getNcboOntologyVersion();

		NcboOntology ontology = ncboOntologyVersionDAO.findOntologyVersion(ver
				.getId());
		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.populateFromEntity(ontology);

		
		
		
		
		
		
		
		
		
		// TODO: remove this
//		String formatHandler = ontologyFormatHandlerMap.get(ontologyBean
//				.getFormat());
//		OntologyLoadManager loadManager = ontologyLoadHandlerMap
//				.get(formatHandler);

		// TODO: remove this if statement
		// if (loadManager instanceof OntologyLoadManagerProtegeImpl) {

		try {

			// TODO: remove this if block
//			if (rec.getNcboOntologyVersion().getId() == 3145
//					|| rec.getNcboOntologyVersion().getId() == 4886
//					|| rec.getNcboOntologyVersion().getId() == 13578) {
//				return;
//			}

			status.setId(StatusEnum.STATUS_PARSING.getStatus());
			ver.setNcboLStatus(status);
			ncboOntologyVersionDAO.saveOntologyVersion(ver);

			rec.setNcboLStatus(status);
			ncboOntologyLoadQueueDAO.saveNcboOntologyLoadQueue(rec);

			loadOntology(ontologyBean);

			status = new NcboLStatus();
			status.setId(StatusEnum.STATUS_READY.getStatus());

		} catch (Exception e) {
			status = new NcboLStatus();
			status.setId(StatusEnum.STATUS_ERROR.getStatus());
			log.error(e);
			e.printStackTrace();
		}

		ver.setNcboLStatus(status);
		ncboOntologyVersionDAO.saveOntologyVersion(ver);

		rec.setNcboLStatus(status);
		ncboOntologyLoadQueueDAO.saveNcboOntologyLoadQueue(rec);

		// }

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
		String formatHandler = ontologyFormatHandlerMap.get(ontologyBean
				.getFormat());
		OntologyLoadManager loadManager = ontologyLoadHandlerMap
				.get(formatHandler);
		List<String> filenames = ontologyBean.getFilenames();

		// for UMLS, zip file, untar it, pass dir to LexGrid
		// pass zip to LexGrid
		// looping through the files

		for (String filename : filenames) {
			File file = new File(ontologyFilePath + ontologyBean.getFilePath()
					+ "/" + filename);
			loadManager.loadOntology(file.toURI(), ontologyBean);
		}
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
	 * @return the ontologyFilePath
	 */
	public String getOntologyFilePath() {
		return ontologyFilePath;
	}

	/**
	 * @param ontologyFilePath
	 *            the ontologyFilePath to set
	 */
	public void setOntologyFilePath(String ontologyFilePath) {
		this.ontologyFilePath = ontologyFilePath;
	}
}
