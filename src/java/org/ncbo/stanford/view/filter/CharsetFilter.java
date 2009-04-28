package org.ncbo.stanford.view.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ncbo.stanford.util.MessageUtils;

public class CharsetFilter implements Filter {
	private String encoding;

	public void init(FilterConfig config) throws ServletException {
		encoding = config.getInitParameter("requestEncoding");

		if (encoding == null) {
			encoding = MessageUtils.getMessage("default.encoding");
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain next) throws IOException, ServletException {
		// Respect the client-specified character encoding
		// (see HTTP specification section 3.4.1)
		if (null == request.getCharacterEncoding()) {
			request.setCharacterEncoding(encoding);
		}

		/**
		 * Set the default response content type and encoding
		 */
		response.setContentType("text/xml;charset=" + encoding);
		response.flushBuffer();

		next.doFilter(request, response);
	}

	public void destroy() {
	}
}
