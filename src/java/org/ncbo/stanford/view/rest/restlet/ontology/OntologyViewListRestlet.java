package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.MessageUtils;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class OntologyViewListRestlet extends AbstractOntologyBaseRestlet {

	private static final Log log = LogFactory.getLog(OntologyViewListRestlet.class);

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
		
//		// find the OntologyBean from request
//		OntologyBean ontologyBean = findOntologyBean(request, response);
//		
//		// if "find" was successful, proceed to update
//		if (!response.getStatus().isError()) {
			try {
				OntologyBean ontologyBean = ontologyService.findLatestOntologyOrViewVersion(Integer.parseInt(ontologyId));

				for (Integer vOntId : ontologyBean.getVirtualViewIds()) {
					List<OntologyBean> viewVersions = ontologyService.findAllOntologyOrViewVersionsByVirtualId(vOntId);
					views.add(viewVersions);
				}
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			} finally {
				// generate response XML
				xmlSerializationService.generateXMLResponse(request, response,
						views);
			}
//		}
	}

}
