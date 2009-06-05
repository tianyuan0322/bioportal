package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.service.ontology.OntologyViewService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyViewRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(OntologyViewRestlet.class);
	private OntologyViewService ontologyViewService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		// Handle GET calls here
		findOntology(request, response);
	}

	/**
	 * Handle PUT calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	protected void putRequest(Request request, Response response) {
		// Handle PUT calls here
		updateOntology(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	protected void deleteRequest(Request request, Response response) {
		// Handle DELETE calls here
		deleteOntologies(request, response);
	}

	/**
	 * Returns a specified OntologyBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void findOntology(Request request, Response response) {
		// find the OntologyViewBean from request
		OntologyViewBean ontologyBean = findOntologyViewBean(request, response);

		// generate response XML
		xmlSerializationService.generateXMLResponse(request, response,
				ontologyBean);
	}

	/**
	 * Update a specified OntologyBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void updateOntology(Request request, Response response) {
		// find the OntologyBean from request
		OntologyViewBean ontologyViewBean = findOntologyViewBean(request, response);

		// if "find" was successful, proceed to update
		if (!response.getStatus().isError()) {
			// 1. populate OntologyBean from Request object
			BeanHelper.populateOntologyViewBeanFromRequest(ontologyViewBean, request);

			// 2. now update the ontology
			try {
				ontologyViewService.cleanupOntologyViewCategory(ontologyViewBean);
				ontologyViewService.updateOntologyView(ontologyViewBean);
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}

		// generate response XML
		xmlSerializationService.generateXMLResponse(request, response,
				ontologyViewBean);
	}

	/**
	 * Delete several ontologies4
	 * 
	 * @param request
	 * @param response
	 */
	private void deleteOntologies(Request request, Response response) {
		try {
			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);
			List<Integer> ontologyVersionIds = getOntologyVersionIds(httpRequest);

			if (ontologyVersionIds != null) {
				ontologyViewService.deleteOntologyViews(ontologyVersionIds);
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

	/**
	 * Returns a specified OntologyBean and set the response status if there is
	 * an error. This is used for find, findAll, update, delete.
	 * 
	 * @param request
	 * @param response
	 */
	private OntologyViewBean findOntologyViewBean(Request request, Response response) {
		OntologyViewBean ontologyBean = null;
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));

		try {
			Integer intId = Integer.parseInt(ontologyVersionId);
			ontologyBean = ontologyViewService.findOntologyView(intId);

			response.setStatus(Status.SUCCESS_OK);

			// if ontologyBean is not found, set Error in the Status object
			if (ontologyBean == null || ontologyBean.getId() == null) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils
						.getMessage("msg.error.ontologyViewNotFound"));
			}
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
			nfe.printStackTrace();
			log.error(nfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		}

		return ontologyBean;
	}

	/**
	 * @param ontologyViewService
	 *            the ontologyViewService to set
	 */
	public void setOntologyViewService(OntologyViewService ontologyViewService) {
		this.ontologyViewService = ontologyViewService;
	}
}
