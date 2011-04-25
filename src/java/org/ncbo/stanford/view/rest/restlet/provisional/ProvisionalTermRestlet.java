package org.ncbo.stanford.view.rest.restlet.provisional;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.exception.ProvisionalTermExistsException;
import org.ncbo.stanford.exception.ProvisionalTermMissingException;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.provisional.ProvisionalTermService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

import com.ibm.icu.util.Calendar;
import com.mysql.jdbc.StringUtils;

public class ProvisionalTermRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(ProvisionalTermRestlet.class);
	private ProvisionalTermService provisionalTermService;
	private OntologyService ontologyService;
	private UserService userService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		listProvisionalTerms(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void postRequest(Request request, Response response) {
		createProvisionalTerm(request, response);
	}

	/**
	 * Handle PUT calls here
	 */
	@Override
	public void putRequest(Request request, Response response) {
		updateProvisionalTerm(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void deleteRequest(Request request, Response response) {
		deleteProvisionalTerm(request, response);
	}

	// Private helpers

	private void listProvisionalTerms(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String termId = httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_ID);

		if (!StringUtils.isNullOrEmpty(termId)) {
			listProvisionalTerm(request, response, termId);
		} else {
			listAllProvisionalTerms(request, response);
		}

	}

	private void listAllProvisionalTerms(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		String ontologyIdsStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ONTOLOGY_IDS);
		String pageSizeStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGESIZE);
		String pageNumStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGENUM);

		SPARQLFilterGenerator parameters = getCommonParameters(request,
				response);

		// Post-process parameters
		List<Integer> ontologyIds = RequestUtils
				.parseIntegerListParam(ontologyIdsStr);
		Integer pageSize = RequestUtils.parseIntegerParam(pageSizeStr);
		Integer pageNum = RequestUtils.parseIntegerParam(pageNumStr);

		OntologyBean ont = null;
		Page<ClassBean> classBeans = null;
		try {
			for (Integer ontologyId : ontologyIds) {
				ont = ontologyService
						.findLatestOntologyOrViewVersion(ontologyId);

				// Check to make sure the ontology is valid
				if (ont == null) {
					throw new InvalidInputException(MessageUtils
							.getMessage("msg.error.ontologyversionidinvalid"));
				}
			}

			classBeans = provisionalTermService.getAllProvisionalTerms(
					pageSize, pageNum, parameters);

		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (ProvisionalTermMissingException e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
			e.printStackTrace();
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					classBeans);
		}
	}

	private void listProvisionalTerm(Request request, Response response,
			String termId) {
		ClassBean classBean = null;
		try {
			classBean = provisionalTermService.getProvisionalTerm(termId);
		} catch (ProvisionalTermMissingException e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					classBean);
		}
	}

	private void createProvisionalTerm(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		String ontologyIdsStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ONTOLOGY_IDS);
		String label = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_LABEL);
		String synonymsStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_SYNONYMS);
		String definition = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_DEFINITION);
		String provisionalSubclassOfStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_PROVISIONALSUBCLASSOF);
		String submittedByStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_SUBMITTEDBY);
		String status = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_STATUS);
		String permanentIdStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_PERMANENTID);
		String noteId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_ID);

		// Post-process parameters
		List<Integer> ontologyIds = RequestUtils
				.parseIntegerListParam(ontologyIdsStr);
		List<String> synonyms = RequestUtils.parseStringListParam(synonymsStr);
		URI provisionalSubclassOf = new URIImpl(provisionalSubclassOfStr);
		Integer submittedBy = RequestUtils.parseIntegerParam(submittedByStr);
		URI permanentId = new URIImpl(permanentIdStr);

		OntologyBean ont = null;
		ClassBean classBean = null;
		try {
			for (Integer ontologyId : ontologyIds) {
				ont = ontologyService
						.findLatestOntologyOrViewVersion(ontologyId);

				// Check to make sure the ontology is valid
				if (ont == null) {
					throw new InvalidInputException(MessageUtils
							.getMessage("msg.error.ontologyversionidinvalid"));
				}
			}
			
			// Check to make sure other required parameters are present
			if (ontologyIds.size() == 0)
				throw new InvalidInputException("A valid ontology id is required");
			
			if (submittedBy != null && userService.findUser(submittedBy) == null)
				throw new InvalidInputException("A valid user id is required");
			
			if (StringUtils.isNullOrEmpty(definition))
				throw new InvalidInputException("A term definition is required");
			
			if (StringUtils.isNullOrEmpty(label))
				throw new InvalidInputException("A preferred name is required");

			// Create the new term
			classBean = provisionalTermService.createProvisionalTerm(
					ontologyIds, label, synonyms, definition,
					provisionalSubclassOf, null, null, submittedBy, noteId,
					status, permanentId);
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
			e.printStackTrace();
		} catch (ProvisionalTermExistsException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					classBean);
		}
	}

	private void updateProvisionalTerm(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		String termId = httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_ID);
		String ontologyIdsStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ONTOLOGY_IDS);
		String label = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_LABEL);
		String synonymsStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_SYNONYMS);
		String definition = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_DEFINITION);
		String provisionalSubclassOfStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_PROVISIONALSUBCLASSOF);
		String submittedByStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_SUBMITTEDBY);
		String status = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_STATUS);
		String permanentIdStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_PERMANENTID);
		String noteId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_ID);

		// Post-process parameters
		List<Integer> ontologyIds = RequestUtils
				.parseIntegerListParam(ontologyIdsStr);
		List<String> synonyms = RequestUtils.parseStringListParam(synonymsStr);
		Integer submittedBy = RequestUtils.parseIntegerParam(submittedByStr);

		URI provisionalSubclassOf = null;
		if (!StringUtils.isNullOrEmpty(provisionalSubclassOfStr)) {
			provisionalSubclassOf = new URIImpl(provisionalSubclassOfStr);
		}

		URI permanentId = null;
		if (!StringUtils.isNullOrEmpty(permanentIdStr)) {
			permanentId = new URIImpl(permanentIdStr);
		}

		OntologyBean ont = null;
		ClassBean classBean = null;
		try {
			for (Integer ontologyId : ontologyIds) {
				ont = ontologyService
						.findLatestOntologyOrViewVersion(ontologyId);

				// Check to make sure the ontology is valid
				if (ont == null) {
					throw new InvalidInputException(MessageUtils
							.getMessage("msg.error.ontologyversionidinvalid"));
				}
			}

			classBean = provisionalTermService.updateProvisionalTerm(termId,
					ontologyIds, label, synonyms, definition,
					provisionalSubclassOf, null, null, submittedBy, noteId,
					status, permanentId);
		} catch (ProvisionalTermMissingException e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					classBean);
		}
	}

	private void deleteProvisionalTerm(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		String termId = httpRequest
				.getParameter(RequestParamConstants.PARAM_PROVISIONAL_TERM_ID);

		try {
			provisionalTermService.deleteProvisionalTerm(termId);
		} catch (ProvisionalTermMissingException e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
			e.printStackTrace();
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

	private SPARQLFilterGenerator getCommonParameters(Request request,
			Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Provisional term submitters
		List<Integer> submittedBy = RequestUtils
				.parseIntegerListParam(httpRequest
						.getParameter(RequestParamConstants.PARAM_SUBMITTERS));

		// Date parameters
		String createdStartDateStr = httpRequest
				.getParameter(RequestParamConstants.PARAM_CREATED_START_DATE);
		String createdEndDateStr = httpRequest
				.getParameter(RequestParamConstants.PARAM_CREATED_END_DATE);
		String updatedStartDateStr = httpRequest
				.getParameter(RequestParamConstants.PARAM_UPDATED_START_DATE);
		String updatedEndDateStr = httpRequest
				.getParameter(RequestParamConstants.PARAM_UPDATED_END_DATE);

		Date createdStartDate = null;
		Date createdEndDate = null;
		if (createdStartDateStr != null) {
			createdStartDate = DatatypeConverter.parseDateTime(
					createdStartDateStr).getTime();
		}
		if (createdEndDateStr != null) {
			createdEndDate = DatatypeConverter.parseDateTime(createdEndDateStr)
					.getTime();
		} else if (createdEndDateStr == null && createdStartDateStr != null) {
			createdEndDate = Calendar.getInstance().getTime();
		}

		Date updatedStartDate = null;
		Date updatedEndDate = null;
		if (updatedStartDateStr != null) {
			updatedStartDate = DatatypeConverter.parseDateTime(
					updatedStartDateStr).getTime();
		}
		if (updatedEndDateStr != null) {
			updatedEndDate = DatatypeConverter.parseDateTime(updatedEndDateStr)
					.getTime();
		} else if (updatedEndDate == null && updatedStartDateStr != null) {
			updatedEndDate = Calendar.getInstance().getTime();
		}

		// Create parameters bean
		SPARQLFilterGenerator parameters = new SPARQLFilterGenerator();
		if (submittedBy != null && !submittedBy.isEmpty())
			parameters.setSubmittedBy(submittedBy);
		if (createdStartDate != null && createdEndDate != null) {
			parameters.setCreatedStartDate(createdStartDate);
			parameters.setCreatedEndDate(createdEndDate);
		}
		if (updatedStartDate != null && updatedEndDate != null) {
			parameters.setUpdatedStartDate(updatedStartDate);
			parameters.setUpdatedEndDate(updatedEndDate);
		}

		return parameters;
	}

	/**
	 * @param provisionalTermService
	 *            the provisionalTermService to set
	 */
	public void setProvisionalTermService(
			ProvisionalTermService provisionalTermService) {
		this.provisionalTermService = provisionalTermService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @param userService the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
