package org.ncbo.stanford.view.rest.restlet.concept;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class ConceptChildrenVirtualRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(ConceptChildrenVirtualRestlet.class);

	private ConceptService conceptService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		findChildren(request, response);
	}

	private void findChildren(Request request, Response response) {
		List<ClassBean> childConcepts = null;
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
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
		Integer ontologyIdInt = RequestUtils.parseIntegerParam(ontologyId);

		try {
			if (ontologyIdInt == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyidinvalid"));
			}

			childConcepts = conceptService.findChildren(new OntologyIdBean(
					ontologyIdInt), conceptId, levelInt, offsetInt, limitInt);
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					childConcepts);
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
