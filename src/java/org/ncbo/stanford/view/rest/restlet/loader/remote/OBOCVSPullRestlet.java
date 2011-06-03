package org.ncbo.stanford.view.rest.restlet.loader.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.loader.remote.OntologyPullService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class OBOCVSPullRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(OBOCVSPullRestlet.class);
	private OntologyPullService ontologyPullService;

	/**
	 * Handle PUT calls here
	 */
	@Override
	public void putRequest(Request request, Response response) {
		try {
			ontologyPullService.doOntologyPull();
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
	 * @param oboCVSPullService the oboCVSPullService to set
	 */
	public void setOntologyPullService(OntologyPullService ontologyPullService) {
		this.ontologyPullService = ontologyPullService;
	}
}
