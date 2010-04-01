/**
 * 
 */
package org.ncbo.stanford.view.rest.restlet.instance;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * @author s.reddy
 * 
 */
public class InstanceRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(InstanceRestlet.class);

	private ConceptService conceptService;

	public void getRequest(Request request, Response response) {
		findInstance(request, response);
	}

	private void findInstance(Request request, Response response) {

		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyVersionid"));
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		String conceptId = httpRequest.getParameter(MessageUtils
				.getMessage("entity.conceptid"));

		Integer ontologyVerId = Integer.parseInt(ontologyVersionId);
		Object instanceBean = null;
		try {
			if (conceptId != null) {

				String pageSize = (String) httpRequest
						.getParameter(RequestParamConstants.PARAM_PAGESIZE);
				String pageNum = (String) httpRequest
						.getParameter(RequestParamConstants.PARAM_PAGENUM);

				Integer pageSizeInt = RequestUtils.parseIntegerParam(pageSize);
				Integer pageNumInt = RequestUtils.parseIntegerParam(pageNum);

				instanceBean = conceptService.findInstancesByConceptId(
						ontologyVerId, conceptId, pageSizeInt, pageNumInt);

			} else {
				throw new InvalidInputException(InvalidInputException.DEFAULT_MESSAGE);
			}

		} catch (InvalidInputException invalidInputEx) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, invalidInputEx
					.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
			log.error(e.getMessage());
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					instanceBean);
		}
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

}
