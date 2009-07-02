package org.ncbo.stanford.view.rest.restlet.search;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class MetaDataSearchRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(MetaDataSearchRestlet.class);
	private OntologyService ontologyService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		searchConcept(request, response);
	}

	/**
	 * Return to the response an individual ontology
	 * 
	 * @param request
	 * @param response
	 */
	private void searchConcept(Request request, Response response) {
		List<OntologyBean> ontologies = null;
		String query = (String) request.getAttributes().get("query");
		query = Reference.decode(query);

		try {
			ontologies = ontologyService.searchOntologyMetadata(query);

			if (ontologies.isEmpty()) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
						"Query not found");
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}

		xmlSerializationService.generateXMLResponse(request, response,
				ontologies);
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}
