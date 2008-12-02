package org.ncbo.stanford.view.rest.restlet.path;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class PathRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(PathRestlet.class);

	private ConceptService conceptService;
	private XMLSerializationService xmlSerializationService;

	/**
	 * Handle GET calls here
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		findPathFromRoot(request, response);
	}

	/**
	 * Return to the response an individual ontology
	 * 
	 * @param ref
	 * @param resp
	 */
	private void findPathFromRoot(Request request, Response resp) {
		ClassBean concept = null;
		String source = (String) request.getAttributes().get("source");
		String target = (String) request.getAttributes().get("target");
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String light_string = (String) httpRequest.getParameter("light");
		boolean light = true;
		if (light_string != null && light_string.equalsIgnoreCase("false"))
			light = false;

		String ontologyVersion = (String) request.getAttributes().get(
				"ontologyVersionId");

		try {
			Integer ontId = Integer.parseInt(ontologyVersion);
			if (target
					.equalsIgnoreCase(RequestParamConstants.PARAM_ROOT_CONCEPT)) {
				concept = conceptService.findPathFromRoot(ontId, source, light);
			} else {
				// This is for when you are finding path from source to target--
				// Not Implemented Yet
			}

			if (concept == null) {
				resp.setStatus(Status.CLIENT_ERROR_NOT_FOUND,
						"Concept not found");
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}

		xmlSerializationService.generateXMLResponse(request, resp, concept);
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
