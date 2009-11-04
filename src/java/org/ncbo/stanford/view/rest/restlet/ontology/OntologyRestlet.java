package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyRestlet extends AbstractOntologyBaseRestlet {

	private static final Log log = LogFactory.getLog(OntologyRestlet.class);

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		// Handle GET calls here
		findOntologyOrView(request, response);
	}

	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void putRequest(Request request, Response response) {
		// Handle PUT calls here
		updateOntologyOrView(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void deleteRequest(Request request, Response response) {
		// Handle DELETE calls here
		deleteOntologiesOrViews(request, response);
	}

	/**
	 * Returns a specified OntologyBean to the response
	 * 
	 * @param request
	 * @param response
	 */
	private void findOntologyOrView(Request request, Response response) {
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// generate response XML
		xmlSerializationService.generateXMLResponse(request, response,
				ontologyBean);
	}

	/**
	 * Update a specified OntologyBean to the response
	 * 
	 * @param request
	 * @param response
	 */
	private void updateOntologyOrView(Request request, Response response) {
		// find the OntologyBean from request
		OntologyBean ontologyBean = findOntologyBean(request, response);

		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {
			// 1. populate OntologyBean from Request object
			BeanHelper.populateOntologyBeanFromRequest(ontologyBean, request);

			// 2. now update the ontology
			try {
				ontologyService.updateOntologyOrView(ontologyBean);
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}

		// generate response XML
		xmlSerializationService.generateXMLResponse(request, response,
				ontologyBean);
	}

	/**
	 * Delete several ontologies4
	 * 
	 * @param request
	 * @param response
	 */
	private void deleteOntologiesOrViews(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String removeMetadata = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_REMOVE_METADATA);
		String removeOntologyFiles = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_REMOVE_ONTOLOGY_FILES);

		try {
			List<Integer> ontologyVersionIds = getOntologyVersionIds(httpRequest);
			boolean removeMetadataBool = RequestUtils
					.parseBooleanParam(removeMetadata);
			boolean removeOntologyFilesBool = RequestUtils
					.parseBooleanParam(removeOntologyFiles);

			if (ontologyVersionIds != null) {
				ontologyService.deleteOntologiesOrViews(ontologyVersionIds,
						removeMetadataBool, removeOntologyFilesBool);
			} else {
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
						"No valid parameters supplied");
			}
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		}
	}
}
