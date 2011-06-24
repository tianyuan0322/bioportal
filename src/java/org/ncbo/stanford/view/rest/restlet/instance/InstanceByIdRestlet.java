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
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 * @author s.reddy
 * 
 */
public class InstanceByIdRestlet extends AbstractBaseRestlet {
	private static final Log log = LogFactory.getLog(InstanceByIdRestlet.class);

	private ConceptService conceptService;

	public void getRequest(Request request, Response response) {
		findInstanceById(request, response);
	}

	private void findInstanceById(Request request, Response response) {

		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		String instanceId = httpRequest.getParameter(MessageUtils
				.getMessage("entity.instanceid"));

		Integer ontologyVerId = Integer.parseInt(ontologyVersionId);
		Object instanceBean = null;
		try {
			if (instanceId != null) {
				instanceBean = conceptService.findInstanceById(ontologyVerId,
						instanceId);
			} else {
				throw new InvalidInputException("invalid input");
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
