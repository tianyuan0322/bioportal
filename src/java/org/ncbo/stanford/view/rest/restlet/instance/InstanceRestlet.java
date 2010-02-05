/**
 * 
 */
package org.ncbo.stanford.view.rest.restlet.instance;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
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

		String instanceId = httpRequest.getParameter(MessageUtils
				.getMessage("entity.instanceid"));

		Integer ontologyVerId = Integer.parseInt(ontologyVersionId);
		InstanceBean instanceBean = null;
		try {

			instanceBean = conceptService.findInstanceById(ontologyVerId,
					instanceId);

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
