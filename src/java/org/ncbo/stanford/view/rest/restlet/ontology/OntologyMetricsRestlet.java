package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.metrics.MetricsService;
import org.ncbo.stanford.util.RequestUtils;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyMetricsRestlet extends AbstractOntologyBaseRestlet {

	private MetricsService metricsService;
	private static final Log log = LogFactory
			.getLog(OntologyMetricsRestlet.class);

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

		try {
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);
			List<Integer> ontologyVersionIds = getOntologyVersionIds(httpRequest);

			if (ontologyVersionIds != null) {
				metricsService.extractOntologyMetrics(ontologyVersionIds);
				List<String> errorOntologies = metricsService
						.getErrorOntologies();

				if (!errorOntologies.isEmpty()) {
					throw new Exception(
							"Error Calculating Metrics for Ontologies: "
									+ errorOntologies);
				}
			}
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// TODO: Ideally this will contain information about which
			// ontologies succeeded or failed during processing.
			// generate response XML
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * @param metricsService
	 *            the metricsService to set
	 */
	public void setMetricsService(MetricsService metricsService) {
		this.metricsService = metricsService;
	}

}
