package org.ncbo.stanford.view.rest.restlet.mapping;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.mapping.MappingBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class MappingStatsRecentRestlet extends AbstractMappingRestlet {

	private static final Log log = LogFactory
			.getLog(MappingStatsRecentRestlet.class);

	private MappingService mappingService;

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
		String limitStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_LIMIT);

		// Post-process parameters
		Integer limit = RequestUtils.parseIntegerParam(limitStr);

		// Default values
		if (limit == null || limit < 1 || limit > 100) {
			limit = 5;
		}

		List<MappingBean> mappings = null;
		try {
			mappings = mappingService.getRecentMappings(limit);
		} catch (InvalidInputException iie) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, iie
					.getMessage());
			iie.printStackTrace();
			log.error(iie);
		} catch (Exception e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
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
