package org.ncbo.stanford.view.rest.restlet.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * An abstract restlet that contains common functionality as well as default
 * handling for GET, POST, PUT, and DELETE methods
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractOntologyBaseRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AbstractOntologyBaseRestlet.class);
	protected OntologyService ontologyService;


	/**
	 * Returns a specified OntologyBean and set the response status if there is
	 * an error. This is used for find, findAll, update, delete.
	 * 
	 * @param request
	 * @param response
	 */
	protected OntologyBean findOntologyBean(Request request, Response response) {
		OntologyBean ontologyBean = null;
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));

		try {
			Integer intId = Integer.parseInt(ontologyVersionId);
			ontologyBean = ontologyService.findOntologyOrView(intId);

			response.setStatus(Status.SUCCESS_OK);

			// if ontologyBean is not found, set Error in the Status object
			if (ontologyBean == null || ontologyBean.getId() == null) {
				//TODO if we could test whether the virtual id is for an ontology or for a view
				//     we could return more appropriate message (i.e. "msg.error.ontologyViewNotFound")
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, MessageUtils
						.getMessage("msg.error.ontologyNotFound"));
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
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}
