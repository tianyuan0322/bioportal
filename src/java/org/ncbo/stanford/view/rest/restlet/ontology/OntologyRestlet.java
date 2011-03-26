package org.ncbo.stanford.view.rest.restlet.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class OntologyRestlet extends AbstractOntologyBaseRestlet {

	private static final Log log = LogFactory.getLog(OntologyRestlet.class);

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		// Handle GET calls here
		findOntologyOrView(request, response);
	}

	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void putRequest(Request request, Response response) {
		// Handle PUT calls here
		updateOntologyOrView(request, response);
	}

	/**
	 * Returns a specified OntologyBean to the response
	 * 
	 * @param request
	 * @param response
	 */
	private void findOntologyOrView(Request request, Response response) {
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// generate response XML
		xmlSerializationService.generateXMLResponse(request, response,
				ontologyBean);
	}

	/**
	 * Update a specified OntologyBean to the response
	 * 
	 * @param request
	 * @param response
	 */
	private void updateOntologyOrView(Request request, Response response) {
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {
			// 1. populate OntologyBean from Request object
			BeanHelper.populateOntologyBeanFromRequest(ontologyBean, request);

			// 2. now update the ontology
			try {
				ontologyService.updateOntologyOrView(ontologyBean);
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}

		// generate response XML
		xmlSerializationService.generateXMLResponse(request, response,
				ontologyBean);
	}
}
