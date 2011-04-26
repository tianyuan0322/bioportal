package org.ncbo.stanford.util.security.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.security.SecurityContextHolder;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;
import org.restlet.util.RouteList;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class AuthorizationFilter extends AbstractAuthFilter {
	private static final Log log = LogFactory.getLog(AuthorizationFilter.class);

	private OntologyService ontologyService;

	private List<String> ontologyVirualParams = Arrays.asList(
			RequestParamConstants.PARAM_ONTOLOGY_ID,
			RequestParamConstants.PARAM_ONTOLOGY_IDS);

	private List<String> ontologyVersionParams = Arrays.asList(
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_ID,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_IDS,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_OLD,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_NEW,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_ID_1,
			RequestParamConstants.PARAM_ONTOLOGY_VERSION_ID_2);

	@SuppressWarnings("unchecked")
	public AuthorizationFilter(Context context,
			WebApplicationContext springAppContext) {
		super(context, springAppContext);
		ontologyService = (OntologyService) springAppContext
				.getBean("ontologyService");
		addExceptionPaths((TreeSet<String>) springAppContext.getBean(
				"authorizationExceptionPaths", TreeSet.class));
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		int retVal = CONTINUE;

		if (isException(request)) {
			return retVal;
		}

		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		extractAttributes(request, response);
		Map<String, Object> params = request.getAttributes();
		RequestUtils.parseQueryString(httpRequest.getQueryString(), params);
		RESTfulSession session = (RESTfulSession) params
				.get(ApplicationConstants.USER_SESSION_NAME);
		SecurityContextHolder ctx = null;
		UserBean user = null;
		ErrorTypeEnum error = null;

		if (session == null
				|| (ctx = (SecurityContextHolder) session
						.getAttribute(ApplicationConstants.SECURITY_CONTEXT_KEY)) == null
				|| (user = ctx.getUserBean()) == null) {
			// This should never happen. NULL session means user failed to
			// authenticate but somehow made it to this filter. The security
			// context or user should not be null either.
			error = ErrorTypeEnum.INVALID_SESSION;
		} else {
			// Admin has access to all ontologies
			if (user.isAdmin()) {
				return retVal;
			}

			StringBuffer sb = new StringBuffer(0);
			Map<Integer, Integer> mapTranslated = new HashMap<Integer, Integer>(
					0);
			List<Integer> ontologyIds = findOntologyIdsInRequest(params,
					mapTranslated);

			for (Integer ontologyId : ontologyIds) {
				if (ontologyService.isInAcl(ontologyId)
						&& !user.isInAcl(ontologyId)) {
					sb.append(mapTranslated.get(ontologyId) + ", ");
				}
			}

			if (sb.length() > 0) {
				error = ErrorTypeEnum.ACCESS_DENIED;
				error
						.setErrorMessage("You don't have sufficient privileges to access the following ontologies/views: "
								+ sb.substring(0, sb.length() - 2));
			}
		}

		if (error != null) {
			String path = request.getResourceRef().getPath();
			retVal = STOP;
			RequestUtils.setHttpServletResponse(response,
					Status.CLIENT_ERROR_FORBIDDEN, MediaType.TEXT_XML,
					xmlSerializationService.getErrorAsXML(error, path));
		}

		return retVal;
	}

	private List<Integer> findOntologyIdsInRequest(Map<String, Object> params,
			Map<Integer, Integer> mapTranslated) {
		List<Integer> ontologyIds = new ArrayList<Integer>(0);

		if (!params.isEmpty()) {
			extractOntologyIdsFromVirtualIds(params, ontologyIds, mapTranslated);
			extractOntologyIdsFromVersionIds(params, ontologyIds, mapTranslated);
		}
		return ontologyIds;
	}

	/**
	 * mapTranslated is a map that keeps track of the translations between the
	 * original ids in the request and the resulting virtual ids
	 * 
	 * @param params
	 * @param ontologyIds
	 * @param mapTranslated
	 */
	private void extractOntologyIdsFromVirtualIds(Map<String, Object> params,
			List<Integer> ontologyIds, Map<Integer, Integer> mapTranslated) {
		for (String param : ontologyVirualParams) {
			if (params.containsKey(param)) {
				try {
					List<Integer> ontIdsParsed = RequestUtils
							.parseIntegerListParam((String) params.get(param));

					for (Integer ontId : ontIdsParsed) {
						// check if this virtual id is a view id and translate
						// it to ontology ids
						List<Integer> ontIds = ontologyService
								.getOntologyIdsByViewId(ontId);

						if (ontIds != null && !ontIds.isEmpty()) {
							// populate translated map to be able to track back
							for (Integer o : ontIds) {
								mapTranslated.put(o, ontId);
							}
							ontologyIds.addAll(ontIds);
						} else {
							mapTranslated.put(ontId, ontId);
							ontologyIds.add(ontId);
						}
					}
				} catch (ClassCastException cce) {
					// ignore value
				} catch (NumberFormatException nfe) {
					// ignore value
				}
			}
		}
	}

	/**
	 * mapTranslated is a map that keeps track of the translations between the
	 * original ids in the request and the resulting virtual ids
	 * 
	 * @param params
	 * @param ontologyIds
	 * @param mapTranslated
	 */
	private void extractOntologyIdsFromVersionIds(Map<String, Object> params,
			List<Integer> ontologyIds, Map<Integer, Integer> mapTranslated) {
		for (String param : ontologyVersionParams) {
			if (params.containsKey(param)) {
				try {
					List<Integer> versionIdsParsed = RequestUtils
							.parseIntegerListParam((String) params.get(param));

					for (Integer versionId : versionIdsParsed) {
						// try getting a virtual ontology id for this version id
						Integer ontId = ontologyService
								.getOntologyIdByVersionId(versionId);

						if (ontId == null) {
							// this might be a view version id, try finding the
							// corresponding virtual view id
							Integer viewId = ontologyService
									.getViewIdByViewVersionId(versionId);

							if (viewId != null) {
								List<Integer> ontIds = ontologyService
										.getOntologyIdsByViewId(viewId);

								if (ontIds != null && !ontIds.isEmpty()) {
									// populate translated map to be able to
									// track back
									for (Integer o : ontIds) {
										mapTranslated.put(o, versionId);
									}
									ontologyIds.addAll(ontIds);
								} else {
									// we found a view id that does not
									// correspond to any ontology id in the
									// system. Sign of bad data.
									log
											.warn("Virtual View Id: "
													+ viewId
													+ " does not appear to correspond to any ontology in the system.");
								}
							} else {
								// there's nothing in the system that matches
								// this id, ignore it
							}
						} else {
							mapTranslated.put(ontId, versionId);
							ontologyIds.add(ontId);
						}
					}
				} catch (ClassCastException cce) {
					// ignore value
				} catch (NumberFormatException nfe) {
					// ignore value
				}
			}
		}
	}

	private void extractAttributes(Request request, Response response) {
		Router next = (Router) getNext();
		RouteList routes = next.getRoutes();
		TemplateRoute best = routes.getBest(request, response, 0);

		if (best != null) {
			final String remainingPart = request.getResourceRef()
					.getRemainingPart(false, false);
			best.getTemplate().parse(remainingPart, request.getAttributes());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(ontologyService, "ontologyService must be specified");
	}
}
