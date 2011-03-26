package org.ncbo.stanford.view.rest.restlet.logging;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.logging.UsageLoggingBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.logging.UsageLoggingService;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class UsageLoggingRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(UsageLoggingRestlet.class);

	private UsageLoggingService usageLoggingService;
	private static final String NO_PARAMETERS_EXCEPTION = "This service requires appropriate input to operate properly";

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		extractUsage(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void postRequest(Request request, Response response) {
		logUsage(request, response);
	}

	private void extractUsage(Request request, Response response) {
		List<UsageLoggingBean> usageData = null;

		try {
			UsageLoggingBean usageBean = BeanHelper
					.populateUsageLoggingBeanFromRequestForDataExtraction(request);

			if (usageBean.isEmpty()) {
				throw new InvalidInputException(NO_PARAMETERS_EXCEPTION);
			} else {
				usageData = usageLoggingService.extractUsage(usageBean);
			}
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					usageData);
		}
	}

	private void logUsage(Request request, Response response) {
		try {
			UsageLoggingBean usageBean = BeanHelper
					.populateUsageLoggingBeanFromRequestForLogging(request);

			if (usageBean.isEmpty()) {
				throw new InvalidInputException(NO_PARAMETERS_EXCEPTION);
			} else {
				usageLoggingService.logUsage(usageBean);
			}
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * @param usageLoggingService
	 *            the usageLoggingService to set
	 */
	public void setUsageLoggingService(UsageLoggingService usageLoggingService) {
		this.usageLoggingService = usageLoggingService;
	}
}
