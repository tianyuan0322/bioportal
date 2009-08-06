package org.ncbo.stanford.view.rest.restlet.concept;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class ConceptParentsRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(ConceptParentsRestlet.class);

	private ConceptService conceptService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		findParents(request, response);
	}

	private void findParents(Request request, Response response) {
		List<ClassBean> parentConcepts = null;
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		String conceptId = getConceptId(request);

		String level = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_LEVEL);
		String offset = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_OFFSET);
		String limit = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_LIMIT);
		Integer levelInt = RequestUtils.parseIntegerParam(level);
		Integer offsetInt = RequestUtils.parseIntegerParam(offset);
		Integer limitInt = RequestUtils.parseIntegerParam(limit);

		try {
			parentConcepts = conceptService.findParents(
					new OntologyVersionIdBean(ontologyVersionId), conceptId,
					levelInt, offsetInt, limitInt);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					parentConcepts);
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
