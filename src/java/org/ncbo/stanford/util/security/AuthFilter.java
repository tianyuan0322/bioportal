package org.ncbo.stanford.util.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Context;
import org.restlet.Filter;
import org.restlet.Route;
import org.restlet.Router;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.util.RouteList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class AuthFilter extends Filter implements InitializingBean {
	private static final Log logger = LogFactory.getLog(AuthFilter.class);

	private XMLSerializationService xmlSerializationService;
	private UserService userService;

	private List<String> ontologyVirualParams = new ArrayList<String>(Arrays.asList(
			RequestParamConstants.PARAM_ONTOLOGY_ID,
			RequestParamConstants.PARAM_ONTOLOGY_IDS));
	
	private List<String> ontologyVersionParams = new ArrayList<String>(Arrays.asList(
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_ID,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_IDS,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_OLD,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_NEW,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_ID_1,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_ID_2));
	
	
	

	public AuthFilter(Context context, WebApplicationContext springAppContext) {
		super(context);
		xmlSerializationService = (XMLSerializationService) springAppContext
				.getBean("xmlSerializationService");
		userService = (UserService) springAppContext.getBean("userService");
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		int retVal = CONTINUE;
		ErrorTypeEnum error = null;

		String apiKey = httpRequest
				.getParameter(RequestParamConstants.PARAM_APIKEY);
		UserBean user = authenticateUserByApiKey(apiKey, error);

		if (error != null) {
			String path = request.getResourceRef().getPath();
//			retVal = STOP;
//			RequestUtils.setHttpServletResponse(response,
//					Status.CLIENT_ERROR_FORBIDDEN, MediaType.TEXT_XML,
//					xmlSerializationService.getErrorAsXML(error, path));
		}

		extractAttributes(request, response);
		Map<String, Object> attrib = request.getAttributes();

		
		
		return retVal;
	}

	private UserBean authenticateUserByApiKey(String apiKey, ErrorTypeEnum error) {
		UserBean user = null;

		if (StringHelper.isNullOrNullString(apiKey)) {
			error = ErrorTypeEnum.APIKEY_REQUIRED;
		} else if (!StringHelper.isValidUUID(apiKey)) {
			error = ErrorTypeEnum.INVALID_APIKEY;
		} else if ((user = userService.findUserByApiKey(apiKey)) == null) {
			error = ErrorTypeEnum.INVALID_CREDENTIALS;
		}

		return user;
	}

	private void extractAttributes(Request request, Response response) {
		Router next = (Router) getNext();
		RouteList routes = next.getRoutes();
		Route best = routes.getBest(request, response, 0);

		final String remainingPart = request.getResourceRef().getRemainingPart(
				false, true);
		best.getTemplate().parse(remainingPart, request.getAttributes());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(xmlSerializationService,
				"xmlSerializationService must be specified");
		Assert.notNull(userService, "userService must be specified");
	}
}
