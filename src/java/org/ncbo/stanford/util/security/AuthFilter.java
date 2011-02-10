package org.ncbo.stanford.util.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Context;
import org.restlet.Filter;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class AuthFilter extends Filter implements InitializingBean {
	private static final Log logger = LogFactory.getLog(AuthFilter.class);

	private XMLSerializationService xmlSerializationService;

	public AuthFilter(Context context, WebApplicationContext springAppContext) {
		super(context);
		xmlSerializationService = (XMLSerializationService) springAppContext
				.getBean("xmlSerializationService");
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		int retVal = CONTINUE;

		String apiKey = httpRequest
				.getParameter(RequestParamConstants.PARAM_APIKEY);
		String path = request.getResourceRef().getPath();

/* commenting this code for now to avoid breaking developers' installations
 		if (StringHelper.isNullOrNullString(apiKey)) {
			RequestUtils.setHttpServletResponse(response,
					Status.CLIENT_ERROR_FORBIDDEN, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(
							ErrorTypeEnum.APIKEY_REQUIRED, path));
			retVal = STOP;
		} else if (!StringHelper.isValidUUID(apiKey)) {
			RequestUtils.setHttpServletResponse(response,
					Status.CLIENT_ERROR_FORBIDDEN, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(
							ErrorTypeEnum.INVALID_APIKEY, path));
			retVal = STOP;
		}
*/
		return retVal;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(xmlSerializationService,
				"xmlSerializationService must be specified");
	}

}
