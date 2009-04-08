package org.ncbo.stanford.view.rest.restlet.concept;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class ConceptChildrenRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(ConceptChildrenRestlet.class);

	private ConceptService conceptService;

	/**
	 * Handle GET calls here
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		findChildren(request, response);
	}

	private void findChildren(Request request, Response response) {
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		String conceptId = getConceptId(request);
		List<ClassBean> childConcepts = null;

		try {
			childConcepts = conceptService.findChildren(
					new OntologyVersionIdBean(ontologyVersionId), conceptId);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					childConcepts);
		}
	}

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
}
