package org.ncbo.stanford.view.rest.base;

import javax.servlet.ServletContext;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.noelios.restlet.ext.servlet.ServletContextAdapter;

/**
 * @author Michael Dorf
 */
public class RestApplication extends Application {

	public RestApplication(Context context) {
	}

	@Override
	public Restlet createRoot() {
		Router router = new Router(getContext());

		ServletContextAdapter adapter = (ServletContextAdapter) getContext();
		ServletContext servletContext = adapter.getServletContext();
		WebApplicationContext springApplicationContext = WebApplicationContextUtils
				.getWebApplicationContext(servletContext);

		RestManager manager = (RestManager) springApplicationContext
				.getBean("restManager");
		manager.init(router);

		return router;
	}

}
