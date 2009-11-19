package org.ncbo.stanford.view.rest.restlet.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.service.metrics.MetricsService;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyMetricsRestlet extends AbstractOntologyBaseRestlet {

	private MetricsService metricsService;
	private static final Log log = LogFactory.getLog(OntologyMetricsRestlet.class);

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		getOntologyMetrics(request, response);
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
				ontologyMetricsBean = metricsService.getOntologyMetrics(ontologyBean);

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
	 * @param metricsService the metricsService to set
	 */
	public void setMetricsService(MetricsService metricsService) {
		this.metricsService = metricsService;
	}

}
