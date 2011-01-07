package org.ncbo.stanford.view.rest.restlet.ontology;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyIdBean;
import org.ncbo.stanford.bean.OntologyVersionIdBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
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

	private static final Log log = LogFactory
			.getLog(AbstractOntologyBaseRestlet.class);
	protected OntologyService ontologyService;

	protected AbstractIdBean getOntologyVersionOrVirtualId(Request request)
			throws InvalidInputException {
		String idParam = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		Integer idInt = null;
		AbstractIdBean id = null;

		if (idParam != null) {
			idInt = RequestUtils.parseIntegerParam(idParam);

			if (idInt == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}

			id = new OntologyVersionIdBean(idInt);
		} else {
			idParam = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyid"));

			if (idParam != null) {
				idInt = RequestUtils.parseIntegerParam(idParam);

				if (idInt == null) {
					throw new InvalidInputException(MessageUtils
							.getMessage("msg.error.ontologyidinvalid"));
				}

				id = new OntologyIdBean(idInt);
			} else {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.idinvalid"));
			}
		}

		return id;
	}

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
		Integer ontologyVersionIdInt = RequestUtils
				.parseIntegerParam(ontologyVersionId);
		Integer ontologyIdInt = null;

		try {
			if (ontologyVersionIdInt == null) {
				String ontologyId = (String) request.getAttributes().get(
						MessageUtils.getMessage("entity.ontologyid"));
				ontologyIdInt = RequestUtils.parseIntegerParam(ontologyId);

				if (ontologyIdInt == null) {
					throw new InvalidInputException(MessageUtils
							.getMessage("msg.error.idinvalid"));
				} else {
					ontologyBean = ontologyService
							.findLatestOntologyOrViewVersion(ontologyIdInt);
				}
			} else {
				ontologyBean = ontologyService
						.findOntologyOrView(ontologyVersionIdInt);
			}

			if (ontologyBean == null || ontologyBean.getId() == null) {
				// TODO if we could test whether the virtual id is for an
				// ontology or for a view
				// we could return more appropriate message (i.e.
				// "msg.error.ontologyViewNotFound")
				throw new OntologyNotFoundException(MessageUtils
						.getMessage("msg.error.ontologyNotFound"));
			}
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
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
