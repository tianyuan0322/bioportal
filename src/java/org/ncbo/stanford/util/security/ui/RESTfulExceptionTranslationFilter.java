package org.ncbo.stanford.util.security.ui;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationTrustResolver;
import org.acegisecurity.AuthenticationTrustResolverImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.enumeration.ErrorType;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.RequestUtils;
import org.restlet.Restlet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Handles any <code>AccessDeniedException</code> and
 * <code>AuthenticationException</code> thrown within the filter chain.
 * <p>
 * This implementation modifies the default Acegi implementation and is designed
 * to handle RESTful calls.
 * </p>
 * 
 * @author Michael Dorf
 */
public class RESTfulExceptionTranslationFilter extends Restlet implements
		Filter, InitializingBean {
	// ~ Static fields/initializers
	// =====================================================================================

	private static final Log logger = LogFactory
			.getLog(RESTfulExceptionTranslationFilter.class);

	// ~ Instance fields
	// ================================================================================================

	private XMLSerializationService xmlSerializationService;
	private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	// ~ Methods
	// ========================================================================================================

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(xmlSerializationService,
				"xmlSerializationService must be specified");
		Assert.notNull(authenticationTrustResolver,
				"authenticationTrustResolver must be specified");
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest)) {
			throw new ServletException("HttpServletRequest required");
		}

		if (!(response instanceof HttpServletResponse)) {
			throw new ServletException("HttpServletResponse required");
		}

		String pathInfo = ((HttpServletRequest) request).getPathInfo();

		try {
			chain.doFilter(request, response);

			if (logger.isDebugEnabled()) {
				logger.debug("Chain processed normally");
			}
		} catch (AuthenticationException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Authentication exception occurred", ex);
			}

			RequestUtils.setHttpServletResponse((HttpServletResponse) response,
					HttpServletResponse.SC_FORBIDDEN, xmlSerializationService
							.getErrorAsXML(ErrorType.AUTHENTICATION_REQUIRED,
									pathInfo));
		} catch (AccessDeniedException ex) {
			if (authenticationTrustResolver.isAnonymous(SecurityContextHolder
					.getContext().getAuthentication())) {
				if (logger.isDebugEnabled()) {
					logger.debug("Access is denied (user is anonymous)", ex);
				}

				RequestUtils.setHttpServletResponse(
						(HttpServletResponse) response,
						HttpServletResponse.SC_FORBIDDEN,
						xmlSerializationService.getErrorAsXML(
								ErrorType.AUTHENTICATION_REQUIRED, pathInfo));
			} else {
				if (logger.isDebugEnabled()) {
					logger
							.debug("Access is denied (user is not anonymous)",
									ex);
				}

				RequestUtils.setHttpServletResponse(
						(HttpServletResponse) response,
						HttpServletResponse.SC_FORBIDDEN,
						xmlSerializationService.getErrorAsXML(
								ErrorType.ACCESS_DENIED, pathInfo));
			}
		} catch (ServletException ex) {
			if (ex.getRootCause() instanceof AuthenticationException
					|| ex.getRootCause() instanceof AccessDeniedException) {
				RequestUtils.setHttpServletResponse(
						(HttpServletResponse) response,
						HttpServletResponse.SC_FORBIDDEN,
						xmlSerializationService.getErrorAsXML(
								ErrorType.ACCESS_DENIED, pathInfo));
			} else {
				RequestUtils.setHttpServletResponse(
						(HttpServletResponse) response,
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						xmlSerializationService.getErrorAsXML(
								ErrorType.RUNTIME_ERROR, pathInfo));
			}
		} catch (IOException ex) {
			RequestUtils.setHttpServletResponse((HttpServletResponse) response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					xmlSerializationService.getErrorAsXML(
							ErrorType.RUNTIME_ERROR, pathInfo));
		}
	}

	public AuthenticationTrustResolver getAuthenticationTrustResolver() {
		return authenticationTrustResolver;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void setAuthenticationTrustResolver(
			AuthenticationTrustResolver authenticationTrustResolver) {
		this.authenticationTrustResolver = authenticationTrustResolver;
	}

	/**
	 * @return the xmlSerializationService
	 */
	public XMLSerializationService getXmlSerializationService() {
		return xmlSerializationService;
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
