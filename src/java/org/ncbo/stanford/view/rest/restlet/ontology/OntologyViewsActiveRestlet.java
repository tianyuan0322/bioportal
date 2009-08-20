package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyViewsActiveRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OntologyViewsActiveRestlet.class);
	private OntologyService ontologyService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		listOntologiesActive(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param request
	 * @param response
	 */
	private void listOntologiesActive(Request request, Response response) {
		List<OntologyBean> ontologyViewList = null;

		try {
			ontologyViewList = ontologyService.findLatestActiveOntologyViewVersions();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML with XSL
			String xslFile = MessageUtils.getMessage("xsl.ontology.view.findall");
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyViewList, xslFile);
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
