package org.ncbo.stanford.view.rest.restlet.mapping;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.mapping.MappingParametersBean;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.openrdf.model.impl.URIImpl;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class MappingRestlet extends AbstractMappingRestlet {

	private static final Log log = LogFactory.getLog(MappingRestlet.class);

	private MappingService mappingService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void getRequest(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Process base parameters
		String mappingId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_ID);

		if (mappingId != null) {
			listMapping(request, response, mappingId);
		} else {
			listMappings(request, response);
		}

	}

	private void listMapping(Request request, Response response,
			String mappingId) {
		OneToOneMappingBean mapping = null;
		try {
			mapping = mappingService.getMapping(new URIImpl(mappingId));
		} catch (MappingMissingException e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					mapping);
		}
	}

	private void listMappings(Request request, Response response) {
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
		if (pageSizeInt == null
				|| pageSizeInt > ApplicationConstants.MAPPINGS_MAX_PAGE_SIZE
				|| pageSizeInt < 1) {
			pageSizeInt = ApplicationConstants.DEFAULT_MAPPINGS_PAGE_SIZE;
		}

		if (pageNumInt == null || pageNumInt < 1) {
			pageNumInt = ApplicationConstants.DEFAULT_MAPPINGS_PAGE_NUM;
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

	/**
	 * @param mappingService
	 *            the mappingService to set
	 */
	public void setMappingService(MappingService mappingService) {
		this.mappingService = mappingService;
	}
}
