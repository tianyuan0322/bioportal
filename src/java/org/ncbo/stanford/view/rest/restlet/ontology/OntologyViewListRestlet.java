package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class OntologyViewListRestlet extends AbstractOntologyBaseRestlet {

	private static final Log log = LogFactory
			.getLog(OntologyViewListRestlet.class);

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		listOntologyViews(request, response);
	}

	/**
	 * Return to the response an individual ontology
	 * 
	 * @param request
	 * @param response
	 */
	private void listOntologyViews(Request request, Response response) {
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		List<List<OntologyBean>> views = new ArrayList<List<OntologyBean>>();
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String excludeDeprecated = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_EXCLUDE_DEPRECATED);
		boolean excludeDeprecatedBool = RequestUtils
				.parseBooleanParam(excludeDeprecated);
		Integer ontologyIdInt = RequestUtils.parseIntegerParam(ontologyId);

		// // find the OntologyBean from request
		// OntologyBean ontologyBean = findOntologyBean(request, response);
		//		
		// // if "find" was successful, proceed to update
		// if (!response.getStatus().isError()) {
		try {
			if (ontologyIdInt == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyidinvalid"));
			}

			OntologyBean ontologyBean = ontologyService
					.findLatestOntologyOrViewVersion(ontologyIdInt);

			if (ontologyBean == null) {
				throw new OntologyNotFoundException(MessageUtils
						.getMessage("msg.error.ontologyNotFound"));
			}

			for (Integer vOntId : ontologyBean.getVirtualViewIds()) {
				List<OntologyBean> viewVersions = ontologyService
						.findAllOntologyOrViewVersionsByVirtualId(vOntId,
								excludeDeprecatedBool);
				views.add(viewVersions);
			}
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (OntologyNotFoundException e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					views);
		}
		// }
	}

}
