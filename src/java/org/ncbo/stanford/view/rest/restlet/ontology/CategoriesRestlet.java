package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class CategoriesRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(CategoriesRestlet.class);
	private OntologyService ontologyService;
	private XMLSerializationService xmlSerializationService;

	@Override
	public void handle(Request request, Response response) {

		if (request.getMethod().equals(Method.GET)) {
			getRequest(request, response);
		}

	}

	/**
	 * Handle GET calls here
	 */
	private void getRequest(Request request, Response response) {
		listCategories(request, response);
	}

	/**
	 * Return to the response a listing of ontologies
	 * 
	 * @param response
	 */
	private void listCategories(Request request, Response response) {

		// List<Integer> categoryList = null;

		// TODO - determine this...
		List<CategoryBean> categoryList = null;

		try {
			categoryList = getOntologyService().findAllCategories();

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);

		} finally {

			// generate response XML with XSL
			getXmlSerializationService().generateXMLResponse(request, response,
					categoryList);
		}

	}

	/**
	 * @return the ontologyService
	 */
	public OntologyService getOntologyService() {
		return ontologyService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @return the xmlSerializationService
	 */
	public XMLSerializationService getXmlSerializationService() {
		return xmlSerializationService;
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