package org.ncbo.stanford.view.rest.restlet.search;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;

public class MetaDataSearchRestlet extends AbstractBaseRestlet {

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

		String query = (String) request.getAttributes().get(
				RequestParamConstants.PARAM_QUERY);
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String includeViews = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_INCLUDEVIEWS);
		Boolean includeViewsBool = RequestUtils.parseBooleanParam(includeViews);
		query = Reference.decode(query);

		try {
			ontologies = ontologyService.searchOntologyMetadata(query,
					includeViewsBool);
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					ontologies);
		}
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}
