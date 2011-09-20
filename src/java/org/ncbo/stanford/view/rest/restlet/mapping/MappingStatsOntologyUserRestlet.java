package org.ncbo.stanford.view.rest.restlet.mapping;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.mapping.MappingUserStatsBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class MappingStatsOntologyUserRestlet extends AbstractMappingRestlet {
	private static final Log log = LogFactory
			.getLog(MappingStatsOntologyUserRestlet.class);

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
		// Default parameters
		String ontologyIdStr = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));

		// Post-process parameters
		Integer ontologyId = RequestUtils.parseIntegerParam(ontologyIdStr);

		String targetontology = (String) request.getAttributes().get("targetontologyid");
		Integer targetontologyId = null;
		if (StringUtils.isNumeric(targetontology))
			targetontologyId = Integer.parseInt(targetontology);
			
		List<MappingUserStatsBean> userCounts = null;
		OntologyBean ont = null;
		OntologyBean ontTarget = null;
		try {
			if (ontologyId != null) {
				ont = ontologyService
						.findLatestOntologyOrViewVersion(ontologyId);
			} else {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}

			if (ont == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}
			if (targetontologyId != null) {
				ontTarget = ontologyService
						.findLatestOntologyOrViewVersion(targetontologyId);
			}
			userCounts = mappingService.getOntologyUserCount(ont
					.getOntologyId(), ontTarget != null ? ontTarget.getOntologyId() : null);
			
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
					userCounts);
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
