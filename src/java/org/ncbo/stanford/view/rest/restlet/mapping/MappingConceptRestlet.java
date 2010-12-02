package org.ncbo.stanford.view.rest.restlet.mapping;

import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.mapping.MappingParametersBean;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class MappingConceptRestlet extends AbstractMappingRestlet {

	private static final Log log = LogFactory
			.getLog(MappingConceptRestlet.class);

	private OntologyService ontologyService;
	private ConceptService conceptService;
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

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void postRequest(Request request, Response response) {
		createMapping(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void deleteRequest(Request request, Response response) {
		deleteMapping(request, response);
	}

	private void listMapping(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Process base parameters
		String ontologyIdStr = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		String conceptId = getConceptId(request);
		String sourceOntStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_SOURCE_ONT);
		String targetOntStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_TARGET_ONT);
		String sourceConceptId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_SOURCE);
		String targetConceptId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_TARGET);
		String unidirectionalStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_UNIDIRECTIONAL);
		String isSourceStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_SOURCE_ONT_ID);
		String isTargetStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_TARGET_ONT_ID);
		String pageSizeStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGESIZE);
		String pageNumStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGENUM);

		// Post-process parameters
		Integer ontologyId = RequestUtils.parseIntegerParam(ontologyIdStr);
		Integer sourceOntId = RequestUtils.parseIntegerParam(sourceOntStr);
		Integer targetOntId = RequestUtils.parseIntegerParam(targetOntStr);
		Boolean unidirectional = RequestUtils
				.parseBooleanParam(unidirectionalStr);
		Boolean isSource = RequestUtils.parseBooleanParam(isSourceStr);
		Boolean isTarget = RequestUtils.parseBooleanParam(isTargetStr);
		Integer pageSize = RequestUtils.parseIntegerParam(pageSizeStr);
		Integer pageNum = RequestUtils.parseIntegerParam(pageNumStr);

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
		MappingParametersBean parameters = getMappingParameters(request,
				response);

		OntologyBean ont = null;
		OntologyBean sourceOnt = null;
		OntologyBean targetOnt = null;
		ClassBean concept = null;
		ClassBean sourceConcept = null;
		ClassBean targetConcept = null;
		Page<OneToOneMappingBean> mappings = null;
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

			if ((ont == null && sourceOnt == null && targetOnt == null)
					|| (ont == null && (sourceOnt == null || targetOnt == null))) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}

			// Test for valid concept
			if (conceptId != null) {
				concept = conceptService.findConcept(ont.getId(), conceptId, 0,
						true, true);
			} else if (conceptId == null && sourceConceptId != null
					&& targetConceptId != null) {
				sourceConcept = conceptService.findConcept(sourceOnt.getId(),
						sourceConceptId, 0, true, true);
				targetConcept = conceptService.findConcept(targetOnt.getId(),
						targetConceptId, 0, true, true);
			}

			if ((concept == null && sourceConcept == null && targetConcept == null)
					|| (concept == null && (sourceConcept == null || targetConcept == null))) {
				throw new ConceptNotFoundException(MessageUtils
						.getMessage("msg.error.conceptNotFound"));
			}

			// Process mappings
			if (ont != null) {
				if (isSource) {
					mappings = mappingService.getMappingsFromConcept(ont,
							concept, pageSize, pageNum, parameters);
				} else if (isTarget) {
					mappings = mappingService.getMappingsToConcept(ont,
							concept, pageSize, pageNum, parameters);
				} else {
					mappings = mappingService.getMappingsForConcept(ont,
							concept, pageSize, pageNum, parameters);
				}
			} else if (sourceOnt != null && targetOnt != null) {
				mappings = mappingService.getMappingsBetweenConcepts(sourceOnt,
						targetOnt, sourceConcept, targetConcept,
						unidirectional, pageSize, pageNum, parameters);
			} else {
				throw new InvalidInputException(
						OntologyNotFoundException.DEFAULT_MESSAGE);
			}

		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (ConceptNotFoundException cnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, cnfe.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					mappings);
		}
	}

	private void createMapping(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Process base parameters
		String sourceOntStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_SOURCE_ONT);
		String targetOntStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_TARGET_ONT);
		String sourceConceptId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_SOURCE);
		String targetConceptId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_TARGET);
		String sourceOntVersionIdStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_SOURCE_VERSION_ONT_ID);
		String targetOntVersionIdStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_TARGET_VERSION_ONT_ID);
		String comment = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_COMMENT);
		String submittedByStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_SUBMITTED_BY);
		String mappingType = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_TYPE);
		String mappingSourceStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_SOURCE);
		String mappingSourceName = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_SOURCE_NAME);
		String mappingSourceContactInfo = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_SOURCE_CONTACT_INFO);
		String mappingSourceSiteStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_SOURCE_SITE);
		String mappingSourceAlgorithm = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_SOURCE_ALGORITHM);
		String relationshipStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_RELATION);

		// Post-process parameters
		Integer sourceOntId = RequestUtils.parseIntegerParam(sourceOntStr);
		Integer targetOntId = RequestUtils.parseIntegerParam(targetOntStr);
		Integer sourceOntVersionId = RequestUtils
				.parseIntegerParam(sourceOntVersionIdStr);
		Integer targetOntVersionId = RequestUtils
				.parseIntegerParam(targetOntVersionIdStr);
		Integer submittedBy = RequestUtils.parseIntegerParam(submittedByStr);
		URI source = new URIImpl(sourceConceptId);
		URI target = new URIImpl(targetConceptId);
		URI relation = new URIImpl(relationshipStr);

		URI mappingSourceSite = null;
		if (mappingSourceSiteStr != null)
			mappingSourceSite = new URIImpl(mappingSourceSiteStr);

		MappingSourceEnum mappingSource = null;
		if (mappingSourceStr != null)
			mappingSource = MappingSourceEnum.valueOf(mappingSourceStr);

		OneToOneMappingBean mapping = null;
		OntologyBean sourceOnt = null;
		OntologyBean targetOnt = null;
		ClassBean sourceConcept = null;
		ClassBean targetConcept = null;
		try {
			// Test for valid ontologies
			if (sourceOntId != null && targetOntId != null) {
				sourceOnt = ontologyService
						.findLatestOntologyOrViewVersion(sourceOntId);
				targetOnt = ontologyService
						.findLatestOntologyOrViewVersion(targetOntId);
			}

			if (sourceOnt == null || targetOnt == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("You must provide a valid ontology id"));
			}

			// Test for valid concepts
			if (sourceConceptId != null && targetConceptId != null) {
				sourceConcept = conceptService.findConcept(sourceOnt.getId(),
						sourceConceptId, 0, true, true);
				targetConcept = conceptService.findConcept(targetOnt.getId(),
						targetConceptId, 0, true, true);
			}

			if (sourceConcept == null || targetConcept == null) {
				throw new ConceptNotFoundException(MessageUtils
						.getMessage("msg.error.conceptNotFound"));
			}

			// Test to make sure required parameters exist
			ArrayList<String> missingParams = new ArrayList<String>();

			if (source == null)
				missingParams.add("source");
			if (target == null)
				missingParams.add("target");
			if (relation == null)
				missingParams.add("relation");
			if (mappingType == null)
				missingParams.add("mappingType");
			if (submittedBy == null)
				missingParams.add("submittedBy");

			if (!missingParams.isEmpty()) {
				throw new InvalidInputException(
						"The following required parameters were not provided: "
								+ StringUtils.join(missingParams, ", "));
			}

			// Default values
			if (sourceOntVersionId == null) {
				sourceOntVersionId = sourceOnt.getId();
			}
			
			if (targetOntVersionId == null) {
				targetOntVersionId = targetOnt.getId();
			}
			
			mapping = mappingService.createMapping(source, target, relation,
					sourceOntId, targetOntId, sourceOntVersionId,
					targetOntVersionId, submittedBy, comment, mappingSource,
					mappingSourceName, mappingSourceContactInfo,
					mappingSourceSite, mappingSourceAlgorithm, mappingType);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					mapping);
		}
	}

	private void deleteMapping(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Process base parameters
		String mappingId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_ID);

		try {
			// The parameter isn't getting auto-decoded so do it here
			mappingId = URLDecoder.decode(mappingId, "UTF-8");
			
			mappingService.deleteMapping(new URIImpl(mappingId));
		} catch (MappingMissingException e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
			log.error(e);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
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

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
}
