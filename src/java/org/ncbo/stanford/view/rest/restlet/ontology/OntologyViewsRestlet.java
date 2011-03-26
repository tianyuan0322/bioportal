package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class OntologyViewsRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OntologyViewsRestlet.class);
	private OntologyService ontologyService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		listOntologyViews(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void listOntologyViews(Request request, Response response) {
		List<OntologyBean> ontologyList = null;
		String lastSegment = request.getResourceRef().getLastSegment();

		try {
			if (lastSegment.equals(RequestParamConstants.PARAM_ACTIVE)) {
				ontologyList = ontologyService
						.findLatestActiveOntologyViewVersions();
			} else {
				ontologyList = ontologyService.findLatestOntologyViewVersions();
			}
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML with XSL
			String xslFile = MessageUtils
					.getMessage("xsl.ontology.findall");
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyList, xslFile);
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