package org.ncbo.stanford.service.ontology;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.manager.load.OntologyLoadManager;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.service.metrics.MetricsService;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;

public abstract class AbstractOntologyService {

	private static final Log log = LogFactory
			.getLog(AbstractOntologyService.class);

	private static final int LONG_ERROR_MESSAGE_LENGTH = 1000;
	private static final int SHORT_ERROR_MESSAGE_LENGTH = 100;

	protected Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>();
	protected Map<String, OntologyLoadManager> ontologyLoadHandlerMap = new HashMap<String, OntologyLoadManager>();
	protected CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	protected CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	protected IndexSearchService indexService;
	protected MetricsService metricsService;
	protected OntologyMetadataManager ontologyMetadataManager;

	protected boolean deleteOntologyFile(OntologyBean ontologyBean) {
		String dirPath = AbstractFilePathHandler
				.getFullOntologyDirPath(ontologyBean);

		return AbstractFilePathHandler.deleteDirectory(new File(dirPath));
	}

	protected List<String> uploadOntologyFile(OntologyBean ontologyBean,
			FilePathHandler filePathHandler) throws Exception {

		List<String> fileNames = new ArrayList<String>(1);

		if (filePathHandler != null) {
			try {
				fileNames = filePathHandler
						.processOntologyFileUpload(ontologyBean);
			} catch (Exception e) {
				// log to error
				log
						.error("Error in OntologyService:uploadOntologyFile()! - remote file (fileItem) "
								+ e.getMessage());
				e.printStackTrace();
				throw e;
			}
		}
		return fileNames;
	}

	protected OntologyLoadManager getLoadManager(OntologyBean ontologyBean)
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

	protected String addErrorOntology(List<String> errorOntologies,
			String ontologyVersionId, OntologyBean ontologyVersion,
			String errorMessage) {
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

	protected String getOntologyDisplay(String ontologyVersionId,
			OntologyBean ontologyVersion) {
		String ontologyDisplay = "(Id: " + ontologyVersionId + ")";

		if (ontologyVersion != null) {
			ontologyDisplay = ontologyVersion.getDisplayLabel() + " "
					+ ontologyDisplay;
		}

		return ontologyDisplay;
	}

	protected String getLongErrorMessage(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString();
		int stackTraceLen = stackTrace.length();
		int messageLen = (stackTraceLen < LONG_ERROR_MESSAGE_LENGTH) ? stackTraceLen
				: LONG_ERROR_MESSAGE_LENGTH;

		return stackTrace.substring(0, messageLen);
	}

	protected String appendError(String errorMessage, Exception e) {
		int halfError = LONG_ERROR_MESSAGE_LENGTH / 2 - 2;
		String newErrorMessage = getLongErrorMessage(e);

		if (errorMessage != null) {
			errorMessage = (errorMessage.length() > halfError) ? errorMessage
					.substring(0, halfError) : errorMessage;
			newErrorMessage = "\n\n"
					+ ((newErrorMessage.length() > halfError) ? newErrorMessage
							.substring(0, halfError) : newErrorMessage);
		} else {
			errorMessage = "";
		}

		errorMessage += newErrorMessage;

		return errorMessage;
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
	 * @param ontologyLoadHandlerMap
	 *            the ontologyLoadHandlerMap to set
	 */
	public void setOntologyLoadHandlerMap(
			Map<String, OntologyLoadManager> ontologyLoadHandlerMap) {
		this.ontologyLoadHandlerMap = ontologyLoadHandlerMap;
	}

	/**
	 * @param indexService
	 *            the indexService to set
	 */
	public void setIndexService(IndexSearchService indexService) {
		this.indexService = indexService;
	}

	/**
	 * @param ncboOntologyFileDAO
	 *            the ncboOntologyFileDAO to set
	 */
	public void setNcboOntologyFileDAO(
			CustomNcboOntologyFileDAO ncboOntologyFileDAO) {
		this.ncboOntologyFileDAO = ncboOntologyFileDAO;
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
	 * @param metricsService
	 *            the metricsService to set
	 */
	public void setMetricsService(MetricsService metricsService) {
		this.metricsService = metricsService;
	}

	/**
	 * @param ontologyMetadataManager
	 *            the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}
}
