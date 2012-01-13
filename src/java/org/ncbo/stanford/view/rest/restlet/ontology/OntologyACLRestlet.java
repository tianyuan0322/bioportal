package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 * A simple service to return the entire ontology ACL from the system
 * 
 * @author Michael Dorf
 * 
 */
public class OntologyACLRestlet extends AbstractOntologyBaseRestlet {

	private static final Log log = LogFactory.getLog(OntologyACLRestlet.class);

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		List<Integer> aclList = null;

		try {
			aclList = ontologyService.getOntologyAcl();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					aclList);
		}
	}
}
