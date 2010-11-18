package org.ncbo.stanford.view.rest.restlet.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.mapping.MappingParametersBean;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import com.ibm.icu.util.Calendar;

public class MappingRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(MappingRestlet.class);

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
		String pageSize = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGESIZE);
		String pageNum = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PAGENUM);

		// Post-process parameters
		Integer pageSizeInt = RequestUtils.parseIntegerParam(pageSize);
		Integer pageNumInt = RequestUtils.parseIntegerParam(pageNum);
		
		// Default values
		if (pageSizeInt == null || pageSizeInt > 50000 || pageSizeInt < 1) {
			pageSizeInt = ApplicationConstants.DEFAULT_PAGE_SIZE;
		}
		
		if (pageNumInt == null || pageNumInt < 1) {
			pageNumInt = ApplicationConstants.DEFAULT_PAGE_NUM;
		}

		// Process non-base parameters
		MappingParametersBean parameters = getMappingParameters(request,
				response);

		Page<OneToOneMappingBean> mappings = null;
		try {
			if (!parameters.isEmpty()) {
				mappings = mappingService.getMappingsForParameters(pageSizeInt,
						pageNumInt, parameters);
			} else {
				throw new InvalidInputException(
						"You must provide at least one parameter in order to retrieve mappings");
			}
		} catch (InvalidInputException iie) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, iie
					.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					mappings);
		}
	}

	private MappingParametersBean getMappingParameters(Request request,
			Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Mapping submitters
		List<Integer> submittedBy = RequestUtils
				.parseIntegerListParam(httpRequest
						.getParameter(RequestParamConstants.PARAM_SUBMITTERS));

		// Mapping types
		String mappingType = httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_TYPE);

		// Date parameters
		String startDateStr = httpRequest
				.getParameter(RequestParamConstants.PARAM_START_DATE);
		String endDateStr = httpRequest
				.getParameter(RequestParamConstants.PARAM_END_DATE);

		Date startDate = null;
		Date endDate = null;
		if (startDateStr != null) {
			startDate = DatatypeConverter.parseDateTime(startDateStr).getTime();
		}

		if (endDateStr != null) {
			endDate = DatatypeConverter.parseDateTime(endDateStr).getTime();
		} else if (startDateStr != null) {
			endDate = Calendar.getInstance().getTime();
		}

		// Relationship types
		List<String> relationshipTypesStr = RequestUtils
				.parseStringListParam(httpRequest
						.getParameter(RequestParamConstants.PARAM_RELATIONSHIPS));

		List<URI> relationshipTypes = new ArrayList<URI>();
		if (relationshipTypesStr != null) {
			for (String relationshipType : relationshipTypesStr) {
				relationshipTypes.add(new URIImpl(relationshipType));
			}
		}

		// Mapping sources
		List<String> mappingSourcesStr = RequestUtils
				.parseStringListParam(httpRequest
						.getParameter(RequestParamConstants.PARAM_MAPPING_SOURCES));

		List<MappingSourceEnum> mappingSources = new ArrayList<MappingSourceEnum>();
		if (mappingSourcesStr != null) {
			for (String mappingSource : mappingSourcesStr) {
				mappingSources.add(MappingSourceEnum.valueOf(mappingSource));
			}
		}

		// Create parameters bean
		MappingParametersBean parameters = new MappingParametersBean();
		if (submittedBy != null && !submittedBy.isEmpty())
			parameters.setSubmittedBy(submittedBy);
		if (mappingType != null)
			parameters.setMappingType(mappingType);
		if (startDate != null && endDate != null) {
			parameters.setStartDate(startDate);
			parameters.setEndDate(endDate);
		}
		if (relationshipTypes != null && !relationshipTypes.isEmpty())
			parameters.setRelationshipTypes(relationshipTypes);
		if (mappingSources != null && !mappingSources.isEmpty())
			parameters.setMappingSource(mappingSources);

		return parameters;
	}

	/**
	 * @param mappingService
	 *            the mappingService to set
	 */
	public void setMappingService(MappingService mappingService) {
		this.mappingService = mappingService;
	}
}
