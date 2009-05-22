package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.service.ontology.OntologyViewService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyViewsActiveRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OntologyViewsActiveRestlet.class);
	private OntologyViewService ontologyViewService;

	/**
	 * Handle GET calls here
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		listOntologiesActive(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param request
	 * @param response
	 */
	private void listOntologiesActive(Request request, Response response) {
		List<OntologyViewBean> ontologyViewList = null;

		try {
			ontologyViewList = ontologyViewService.findLatestActiveOntologyViewVersions();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML with XSL
			String xslFile = MessageUtils.getMessage("xsl.ontology.findall");
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyViewList, xslFile);
		}
	}

	/**
	 * @param ontologyViewService
	 *            the ontologyViewService to set
	 */
	public void setOntologyViewService(OntologyViewService ontologyViewService) {
		this.ontologyViewService = ontologyViewService;
	}
}
