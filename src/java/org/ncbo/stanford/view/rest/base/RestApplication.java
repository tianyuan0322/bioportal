package org.ncbo.stanford.view.rest.base;

import javax.servlet.ServletContext;

import org.ncbo.stanford.util.security.AuthFilter;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Router;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.noelios.restlet.ext.servlet.ServletContextAdapter;

/**
 * @author Michael Dorf
 */
public class RestApplication extends Application {

	public RestApplication() {
	}

	@Override
	public Restlet createRoot() {
		Router router = new Router(getContext());

		ServletContextAdapter adapter = (ServletContextAdapter) getContext();
		ServletContext servletContext = adapter.getServletContext();
		WebApplicationContext springAppContext = WebApplicationContextUtils
				.getWebApplicationContext(servletContext);

		RestManager manager = (RestManager) springAppContext
				.getBean("restManager");
		manager.init(router);

		AuthFilter authFilter = new AuthFilter(getContext(), springAppContext);
		authFilter.setNext(router);
		
		return authFilter;
	}
}
