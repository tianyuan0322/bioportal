package org.ncbo.stanford.view.rest.restlet.loader.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.loader.remote.OBOCVSPullService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OBOCVSPullRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(OBOCVSPullRestlet.class);
	private OBOCVSPullService oboCVSPullService;

	/**
	 * Handle PUT calls here
	 */
	@Override
	public void putRequest(Request request, Response response) {
		try {
			oboCVSPullService.doCVSPull();
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
	public void setOboCVSPullService(OBOCVSPullService oboCVSPullService) {
		this.oboCVSPullService = oboCVSPullService;
	}
}
