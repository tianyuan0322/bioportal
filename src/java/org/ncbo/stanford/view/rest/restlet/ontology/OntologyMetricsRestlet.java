package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.manager.metrics.OntologyMetricsManager;
import org.ncbo.stanford.service.metrics.MetricsService;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyMetricsRestlet extends AbstractOntologyBaseRestlet {

	private MetricsService metricsService;
	private static final Log log = LogFactory
			.getLog(OntologyMetricsRestlet.class);
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	private Map<String, OntologyMetricsManager> ontologyMetricsHandlerMap = new HashMap<String, OntologyMetricsManager>(
			0);

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		getOntologyMetrics(request, response);
	}

	/**
	 * Handle POST calls here
	 */
	@Override
	public void postRequest(Request request, Response response) {
		extractOntologyMetrics(request, response);
	}

	/**
	 * Return to the response an individual ontology
	 * 
	 * @param request
	 * @param response
	 */
	private void getOntologyMetrics(Request request, Response response) {
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		OntologyMetricsBean ontologyMetricsBean = null;
		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {
			try {
				ontologyMetricsBean = metricsService
						.getOntologyMetrics(ontologyBean);

			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			} finally {
				// generate response XML
				xmlSerializationService.generateXMLResponse(request, response,
						ontologyMetricsBean);
			}
		}
	}

	/**
	 * Extract metrics from the given ontology and store them into the metadata
	 * ontology.
	 * 
	 * @param request
	 * @param response
	 */
	private void extractOntologyMetrics(Request request, Response response) {
		// find the OntologyBean from the request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {
			try {
				OntologyMetricsBean metricsBean = getMetricsManager(
						ontologyBean).extractOntologyMetrics(ontologyBean);

				metricsService.updateOntologyMetrics(ontologyBean, metricsBean);

			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);

			} finally {
				// generate response XML
				xmlSerializationService.generateStatusXMLResponse(request,
						response);
			}
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
	 * @param metricsService
	 *            the metricsService to set
	 */
	public void setMetricsService(MetricsService metricsService) {
		this.metricsService = metricsService;
	}

	/**
	 * @param ontologyMetricsHandlerMap the ontologyMetricsHandlerMap to set
	 */
	public void setOntologyMetricsHandlerMap(
			Map<String, OntologyMetricsManager> ontologyMetricsHandlerMap) {
		this.ontologyMetricsHandlerMap = ontologyMetricsHandlerMap;
	}

	/**
	 * @param ontologyFormatHandlerMap the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

}
