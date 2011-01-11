package org.ncbo.stanford.view.rest.restlet.mapping;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.mapping.MappingConceptStatsBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class MappingStatsOntologyConceptsRestlet extends AbstractMappingRestlet {

	private static final Log log = LogFactory
			.getLog(MappingStatsOntologiesRestlet.class);

	private MappingService mappingService;
	private OntologyService ontologyService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void getRequest(Request request, Response response) {
		listMappings(request, response);
	}

	private void listMappings(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Process base parameters
		String ontologyIdStr = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		String ontologyVersionIdStr = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		String limitStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_LIMIT);

		// Post-process parameters
		Integer ontologyId = RequestUtils.parseIntegerParam(ontologyIdStr);
		Integer ontologyVersionId = RequestUtils
				.parseIntegerParam(ontologyVersionIdStr);
		Integer limit = RequestUtils.parseIntegerParam(limitStr);

		// Default values
		if (limit == null || limit < 1 || limit > 500) {
			limit = 500;
		}

		List<MappingConceptStatsBean> ontologyCounts = null;
		OntologyBean ont = null;
		try {
			if (ontologyId != null) {
				ont = ontologyService.findLatestOntologyOrViewVersion(ontologyId);
			} else if (ontologyVersionId != null) {
				ont = ontologyService.findOntologyOrView(ontologyVersionId);
			}

			if (ont == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}

			ontologyCounts = mappingService.getOntologyConceptsCount(ont
					.getOntologyId(), limit);
		} catch (InvalidInputException e) {
			log.debug(e);
			e.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			log.debug(e);
			e.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyCounts);
		}
	}

	/**
	 * @param mappingService
	 *            the mappingService to set
	 */
	public void setMappingService(MappingService mappingService) {
		this.mappingService = mappingService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

}
