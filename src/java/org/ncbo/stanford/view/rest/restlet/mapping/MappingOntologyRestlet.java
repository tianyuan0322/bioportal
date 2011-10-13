package org.ncbo.stanford.view.rest.restlet.mapping;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.mapping.MappingBean;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class MappingOntologyRestlet extends AbstractMappingRestlet {

	private static final Log log = LogFactory
			.getLog(MappingOntologyRestlet.class);

	private OntologyService ontologyService;
	private MappingService mappingService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void getRequest(Request request, Response response) {
		listMapping(request, response);
	}

	private void listMapping(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Process base parameters
		String ontologyIdStr = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		String pageSizeStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGESIZE);
		String pageNumStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGENUM);
		String sourceOntStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_SOURCE_ONT);
		String targetOntStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_TARGET_ONT);
		String unidirectionalStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_UNIDIRECTIONAL);
		String isSourceStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_IS_SOURCE);
		String isTargetStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_IS_TARGET);
		String rankedStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_RANKED);

		// Post-process parameters
		Integer ontologyId = RequestUtils.parseIntegerParam(ontologyIdStr);
		Integer pageSize = RequestUtils.parseIntegerParam(pageSizeStr);
		Integer pageNum = RequestUtils.parseIntegerParam(pageNumStr);
		Integer sourceOntId = RequestUtils.parseIntegerParam(sourceOntStr);
		Integer targetOntId = RequestUtils.parseIntegerParam(targetOntStr);
		Boolean unidirectional = RequestUtils
				.parseBooleanParam(unidirectionalStr);
		Boolean isSource = RequestUtils.parseBooleanParam(isSourceStr);
		Boolean isTarget = RequestUtils.parseBooleanParam(isTargetStr);
		Boolean ranked = RequestUtils.parseBooleanParam(rankedStr);

		// Default values
		if (pageSize == null
				|| pageSize > ApplicationConstants.MAPPINGS_MAX_PAGE_SIZE
				|| pageSize < 1) {
			pageSize = ApplicationConstants.DEFAULT_MAPPINGS_PAGE_SIZE;
		}

		if (pageNum == null || pageNum < 1) {
			pageNum = ApplicationConstants.DEFAULT_MAPPINGS_PAGE_NUM;
		}

		// Process non-base parameters
		SPARQLFilterGenerator parameters = getMappingParameters(request,
				response);

		OntologyBean ont = null;
		OntologyBean sourceOnt = null;
		OntologyBean targetOnt = null;
		Page<MappingBean> mappings = null;
		try {
			// Test for valid ontology
			if (ontologyId != null) {
				ont = ontologyService
						.findLatestOntologyOrViewVersion(ontologyId);
			} else if (sourceOntId != null && targetOntId != null) {
				sourceOnt = ontologyService
						.findLatestOntologyOrViewVersion(sourceOntId);
				targetOnt = ontologyService
						.findLatestOntologyOrViewVersion(targetOntId);
			}

			if (ont == null && sourceOnt == null && targetOnt == null) {
				throw new InvalidInputException(
						MessageUtils
								.getMessage("msg.error.ontologyversionidinvalid"));
			}

			if (ont != null) {
				if (isSource) {
					mappings = mappingService.getMappingsFromOntology(ont,
							pageSize, pageNum, parameters);
				} else if (isTarget) {
					mappings = mappingService.getMappingsToOntology(ont,
							pageSize, pageNum, parameters);
				} else {
					mappings = mappingService.getMappingsForOntology(ont,
							pageSize, pageNum, parameters);
				}
			} else if (sourceOnt != null && targetOnt != null) {
				if (ranked) {
					mappings = mappingService
							.getRankedMappingsBetweenOntologies(sourceOnt,
									targetOnt, pageSize, pageNum, parameters);
				} else {
					mappings = mappingService.getMappingsBetweenOntologies(
							sourceOnt, targetOnt, pageSize, pageNum,
							unidirectional, parameters);
				}
			} else {
				throw new InvalidInputException(
						OntologyNotFoundException.DEFAULT_MESSAGE);
			}

		} catch (OntologyNotFoundException onfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (ConceptNotFoundException cnfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, cnfe.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					mappings);
		}
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @param mappingService
	 *            the mappingService to set
	 */
	public void setMappingService(MappingService mappingService) {
		this.mappingService = mappingService;
	}
}
