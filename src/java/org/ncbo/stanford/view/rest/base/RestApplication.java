package org.ncbo.stanford.view.rest.base;

import javax.servlet.ServletContext;

import org.ncbo.stanford.util.security.filter.AuthenticationFilter;
import org.ncbo.stanford.util.security.filter.AuthorizationFilter;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Michael Dorf
 */
public class RestApplication extends Application {

	public RestApplication() {
	}

	@Override
	public Restlet createRoot() {
		Router router = new Router(getContext());
		ServletContext servletContext = (ServletContext) getContext()
				.getAttributes().get("org.restlet.ext.servlet.ServletContext");
		WebApplicationContext springAppContext = WebApplicationContextUtils
				.getWebApplicationContext(servletContext);

		RestManager manager = (RestManager) springAppContext
				.getBean("restManager");
		manager.init(router);
		AuthenticationFilter authentFilter = new AuthenticationFilter(
				getContext(), springAppContext);
		AuthorizationFilter authorizFilter = new AuthorizationFilter(
				getContext(), springAppContext);
		authentFilter.setNext(authorizFilter);
		authorizFilter.setNext(router);

		return authentFilter;
	}
}
