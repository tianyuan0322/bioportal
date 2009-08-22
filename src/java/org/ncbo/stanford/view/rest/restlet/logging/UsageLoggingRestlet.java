package org.ncbo.stanford.view.rest.restlet.logging;

import java.util.List;

import org.ncbo.stanford.bean.logging.UsageLoggingBean;
import org.ncbo.stanford.service.logging.UsageLoggingService;
import org.ncbo.stanford.util.helper.BeanHelper;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class UsageLoggingRestlet extends AbstractBaseRestlet {

	private UsageLoggingService usageLoggingService;
	private static final String NO_PARAMETERS_EXCEPTION = "This service requires appropriate query parameters to operate";

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		extractUsage(request, response);
	}

	@Override
	protected boolean logRequests() {
		return false;
	}

	private void extractUsage(Request request, Response response) {
		List<UsageLoggingBean> usageData = null;
		UsageLoggingBean usageBean = BeanHelper
				.populateUsageLoggingBeanFromRequestForDataExtraction(request);

		if (usageBean.isEmpty()) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST,
					NO_PARAMETERS_EXCEPTION);
		} else {
			usageData = usageLoggingService.extractUsage(usageBean);
		}

		// generate response XML
		xmlSerializationService.generateXMLResponse(request, response,
				usageData);
	}

	/**
	 * @param usageLoggingService
	 *            the usageLoggingService to set
	 */
	public void setUsageLoggingService(UsageLoggingService usageLoggingService) {
		this.usageLoggingService = usageLoggingService;
	}
}
