package org.ncbo.stanford.view.rest.restlet.concept;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.exception.InvalidParameterException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class ConceptSiblingsRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(ConceptSiblingsRestlet.class);

	private ConceptService conceptService;

	/**
	 * Handle GET calls here
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		findSiblings(request, response);
	}

	private void findSiblings(Request request, Response response) {
		List<ClassBean> siblings = null;
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		String conceptId = getConceptId(request);

		String level = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_LEVEL);
		String offset = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_OFFSET);
		Integer levelInt = RequestUtils.parseIntegerParam(level);
		Integer offsetInt = RequestUtils.parseIntegerParam(offset);

		try {
			if (StringHelper.isNullOrNullString(level)) {
				throw new InvalidParameterException(
						"No level parameter specified");
			}

			if (levelInt == null) {
				throw new InvalidParameterException(
						"Invalid level parameter specified");
			}

			siblings = conceptService.findSiblings(new OntologyVersionIdBean(
					ontologyVersionId), conceptId, levelInt, offsetInt);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					siblings);
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
