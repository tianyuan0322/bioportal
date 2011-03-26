package org.ncbo.stanford.view.rest.restlet.concept;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.ontology.AbstractOntologyBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class ConceptLeavesRestlet extends AbstractOntologyBaseRestlet {

	private static final Log log = LogFactory
			.getLog(ConceptLeavesRestlet.class);

	private ConceptService conceptService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		findLeaves(request, response);
	}

	private void findLeaves(Request request, Response response) {
		List<ClassBean> leafConcepts = null;

		try {
			AbstractIdBean id = getOntologyVersionOrVirtualId(request);
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);
			String conceptId = getConceptId(request);
			String offset = (String) httpRequest
					.getParameter(RequestParamConstants.PARAM_OFFSET);
			String limit = (String) httpRequest
					.getParameter(RequestParamConstants.PARAM_LIMIT);
			String delim = RequestUtils.getAttributeOrRequestParam(
					RequestParamConstants.PARAM_DELIMITER, request);
			Integer offsetInt = RequestUtils.parseIntegerParam(offset);
			Integer limitInt = RequestUtils.parseIntegerParam(limit);

			leafConcepts = conceptService.findLeaves(id, conceptId, offsetInt,
					limitInt, delim);
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					leafConcepts);
		}
	}

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
}
