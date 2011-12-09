package org.ncbo.stanford.view.rest.restlet.ontology;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.NamespaceBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class OntologyNamespacesRestlet extends AbstractOntologyBaseRestlet {
	private static final Log log = LogFactory
			.getLog(OntologyNamespacesRestlet.class);

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		List<NamespaceBean> namespaces = null;
		OntologyBean ontologyBean = findOntologyBean(request, response);

		if (!response.getStatus().isError()) {
			try {
				namespaces = ontologyService.findNamespaces(ontologyBean);
			} catch (Exception e) {
				response
						.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}

		xmlSerializationService.generateXMLResponse(request, response,
				namespaces);
	}
}
