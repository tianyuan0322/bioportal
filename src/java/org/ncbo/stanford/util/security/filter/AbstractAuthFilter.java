package org.ncbo.stanford.util.security.filter;

import java.util.Set;
import java.util.TreeSet;

import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;
import org.restlet.util.RouteList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class AbstractAuthFilter extends Filter implements InitializingBean {

	protected XMLSerializationService xmlSerializationService;
	private Set<String> exceptionPaths;
	private Set<String> exceptionReferrers;
	protected Router router;

	@SuppressWarnings("unchecked")
	public AbstractAuthFilter(Context context,
			WebApplicationContext springAppContext, Router router) {
		super(context);
		this.router = router;
		xmlSerializationService = (XMLSerializationService) springAppContext
				.getBean("xmlSerializationService",
						XMLSerializationService.class);
		exceptionPaths = new TreeSet<String>((TreeSet<String>) springAppContext
				.getBean("authenticationExceptionPaths", TreeSet.class));
		exceptionReferrers = (TreeSet<String>) springAppContext.getBean(
				"authenticationExceptionReferrers", TreeSet.class);
	}

	protected void addExceptionPath(String path) {
		exceptionPaths.add(path);
	}

	protected void addExceptionPaths(Set<String> paths) {
		exceptionPaths.addAll(paths);
	}

	protected boolean isException(Request request, Response response) {
		Reference referrerRef = request.getReferrerRef();
		RouteList routes = router.getRoutes();
		TemplateRoute bestRoute = routes.getBest(request, response, 0);

		// if best route isn't found (null), you have a bigger fish to fry leave
		// for the caller to deal with it
		if (bestRoute != null) {
			Template template = bestRoute.getTemplate();
			String pattern = template.getPattern();
			// remove trailing slash
			pattern = pattern.substring(0, pattern.length()
					- (pattern.endsWith("/") ? 1 : 0));

			for (String path : exceptionPaths) {
				// remove trailing slash
				path = path.substring(0, path.length()
						- (path.endsWith("/") ? 1 : 0));

				if (!path.isEmpty() && pattern.equalsIgnoreCase(path)) {
					return true;
				}
			}

			if (referrerRef != null) {
				String referrer = referrerRef.toString();

				for (String path : exceptionReferrers) {
					if (!path.isEmpty() && referrer.startsWith(path)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(xmlSerializationService,
				"xmlSerializationService must be specified");
		Assert.notNull(exceptionPaths, "exceptionPaths must be specified");
		Assert.notNull(exceptionReferrers,
				"exceptionReferrers must be specified");
	}
}
