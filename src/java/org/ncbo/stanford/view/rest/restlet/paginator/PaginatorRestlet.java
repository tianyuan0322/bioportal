package org.ncbo.stanford.view.rest.restlet.paginator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;

public class PaginatorRestlet extends Restlet {

	private static final Log log = LogFactory.getLog(PaginatorRestlet.class);

	@Override
	public void handle(Request request, Response response) {
		if (request.getMethod().equals(Method.GET)) {
			getRequest(request, response);
		}
	}

	/**
	 * Handle GET calls here
	 */
	private void getRequest(Request request, Response response) {

	}
}
