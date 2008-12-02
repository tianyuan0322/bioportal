package org.ncbo.stanford.view.rest.restlet.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.search.IndexService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class IndexRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(IndexRestlet.class);

	private IndexService indexService;
	private XMLSerializationService xmlSerializationService;

	/**
	 * Handle GET calls here
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		// no GET requests should be made to this restlet
		response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		xmlSerializationService.generateStatusXMLResponse(request, response);
	}

	/**
	 * Handle POST calls here
	 */
	@Override
	protected void postRequest(Request request, Response response) {
		indexAllOntologies(request, response);
	}

	/**
	 * Handle PUT calls here
	 */
	@Override
	protected void putRequest(Request request, Response response) {
		indexOntology(request, response);
	}

	/**
	 * Handle DELETE calls here
	 */
	@Override
	protected void deleteRequest(Request request, Response response) {
		removeOntology(request, response);
	}

	/**
	 * Index all ontologies
	 * 
	 * @param request
	 * @param response
	 */
	private void indexAllOntologies(Request request, Response response) {
		try {
			indexService.indexAllOntologies();
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
	 * Index ontology
	 * 
	 * @param request
	 * @param response
	 */
	private void indexOntology(Request request, Response response) {
		try {
			String ontologyIdStr = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyid"));
			Integer ontologyId = Integer.parseInt(ontologyIdStr);
			indexService.indexOntology(ontologyId);
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
	 * Removes ontology from index
	 * 
	 * @param request
	 * @param response
	 */
	private void removeOntology(Request request, Response response) {
		try {
			String ontologyIdStr = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyid"));
			Integer ontologyId = Integer.parseInt(ontologyIdStr);
			indexService.removeOntology(ontologyId);
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
	 * @param indexService
	 *            the indexService to set
	 */
	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
