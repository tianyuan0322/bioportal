/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ncbo.stanford.util.security.ui;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.AcegiSecurityException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationTrustResolver;
import org.acegisecurity.AuthenticationTrustResolverImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.AccessDeniedHandler;
import org.acegisecurity.ui.AccessDeniedHandlerImpl;
import org.acegisecurity.ui.AuthenticationEntryPoint;
import org.acegisecurity.ui.savedrequest.SavedRequest;
import org.acegisecurity.util.PortResolver;
import org.acegisecurity.util.PortResolverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.RequestUtils;
import org.restlet.Restlet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * Handles any <code>AccessDeniedException</code> and
 * <code>AuthenticationException</code> thrown within the filter chain.
 * <p>
 * This implementation modifies the default Acegi implementation and is designed to handle RESTful calls.
 * </p>
 * <p>
 * If an {@link AccessDeniedException} is detected, the filter will determine
 * whether or not the user is an anonymous user. If they are an anonymous user,
 * the <code>authenticationEntryPoint</code> will be launched. If they are not
 * an anonymous user, the filter will delegate to the
 * {@link org.acegisecurity.ui.AccessDeniedHandler}. By default the filter will
 * use {@link org.acegisecurity.ui.AccessDeniedHandlerImpl}.
 * </p>
 * <p>
 * To use this filter, it is necessary to specify the following properties:
 * </p>
 * <ul>
 * <li><code>portResolver</code> is used to determine the "real" port that a
 * request was received on.</li>
 * </ul>
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

	private AccessDeniedHandler accessDeniedHandler;

	private AuthenticationEntryPoint authenticationEntryPoint;

	private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	private PortResolver portResolver = new PortResolverImpl();

	private boolean createSessionAllowed = true;

	// ~ Methods
	// ========================================================================================================

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(authenticationEntryPoint,
				"authenticationEntryPoint must be specified");
		Assert.notNull(portResolver, "portResolver must be specified");
		Assert.notNull(authenticationTrustResolver,
				"authenticationTrustResolver must be specified");
	}

	/**
	 * Introspects the <code>Applicationcontext</code> for the single instance
	 * of {@link AccessDeniedHandler}. If found invoke
	 * setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) method by
	 * providing the found instance of accessDeniedHandler as a method
	 * parameter. If more than one instance of <code>AccessDeniedHandler</code>
	 * is found, the method throws <code>IllegalStateException</code>.
	 * 
	 * @param applicationContext
	 *            to locate the instance
	 */
	private void autoDetectAnyAccessDeniedHandlerAndUseIt(
			ApplicationContext applicationContext) {
		Map map = applicationContext.getBeansOfType(AccessDeniedHandler.class);
		if (map.size() > 1) {
			throw new IllegalArgumentException(
					"More than one AccessDeniedHandler beans detected please refer to the one using "
							+ " [ accessDeniedBeanRef  ] " + "attribute");
		} else if (map.size() == 1) {
			AccessDeniedHandler handler = (AccessDeniedHandlerImpl) map
					.values().iterator().next();
			setAccessDeniedHandler(handler);
		} else {
			// create and use the default one specified as an instance variable.
			accessDeniedHandler = new AccessDeniedHandlerImpl();
		}

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

		try {
			chain.doFilter(request, response);

			if (logger.isDebugEnabled()) {
				logger.debug("Chain processed normally");
			}
		} catch (AuthenticationException ex) {
			handleException(request, response, chain, ex);
		} catch (AccessDeniedException ex) {
			handleException(request, response, chain, ex);
		} catch (ServletException ex) {
			if (ex.getRootCause() instanceof AuthenticationException
					|| ex.getRootCause() instanceof AccessDeniedException) {
				handleException(request, response, chain,
						(AcegiSecurityException) ex.getRootCause());
			} else {
				throw ex;
			}
		} catch (IOException ex) {
			throw ex;
		}
	}

	public AuthenticationEntryPoint getAuthenticationEntryPoint() {
		return authenticationEntryPoint;
	}

	public AuthenticationTrustResolver getAuthenticationTrustResolver() {
		return authenticationTrustResolver;
	}

	public PortResolver getPortResolver() {
		return portResolver;
	}

	private void handleException(ServletRequest request,
			ServletResponse response, FilterChain chain,
			AcegiSecurityException exception) throws IOException,
			ServletException {
		if (exception instanceof AuthenticationException) {
			if (logger.isDebugEnabled()) {
				logger
						.debug(
								"Authentication exception occurred; redirecting to authentication entry point",
								exception);
			}

			RequestUtils.setHttpServletResponse(
					(HttpServletResponse) response,
					HttpServletResponse.SC_FORBIDDEN,
					"hello world - authentication exception");

			// sendStartAuthentication(request, response, chain,
			// (AuthenticationException) exception);
		} else if (exception instanceof AccessDeniedException) {
			if (authenticationTrustResolver.isAnonymous(SecurityContextHolder
					.getContext().getAuthentication())) {
				if (logger.isDebugEnabled()) {
					logger
							.debug(
									"Access is denied (user is anonymous); redirecting to authentication entry point",
									exception);
				}

				RequestUtils.setHttpServletResponse(
						(HttpServletResponse) response,
						HttpServletResponse.SC_FORBIDDEN,
						"hello world - access denied exception");

				// sendStartAuthentication(request, response, chain, new
				// InsufficientAuthenticationException(
				// "Full authentication is required to access this resource"));
			} else {
				if (logger.isDebugEnabled()) {
					logger
							.debug(
									"Access is denied (user is not anonymous); delegating to AccessDeniedHandler",
									exception);
				}

				RequestUtils.setHttpServletResponse(
						(HttpServletResponse) response,
						HttpServletResponse.SC_FORBIDDEN,
						"hello world - access denied exception");
				
				
//				accessDeniedHandler.handle(request, response,
//						(AccessDeniedException) exception);
			}
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * If <code>true</code>, indicates that
	 * <code>SecurityEnforcementFilter</code> is permitted to store the target
	 * URL and exception information in the <code>HttpSession</code> (the
	 * default). In situations where you do not wish to unnecessarily create
	 * <code>HttpSession</code>s - because the user agent will know the
	 * failed URL, such as with BASIC or Digest authentication - you may wish to
	 * set this property to <code>false</code>. Remember to also set the
	 * {@link org.acegisecurity.context.HttpSessionContextIntegrationFilter#allowSessionCreation}
	 * to <code>false</code> if you set this property to <code>false</code>.
	 * 
	 * @return <code>true</code> if the <code>HttpSession</code> will be
	 *         used to store information about the failed request,
	 *         <code>false</code> if the <code>HttpSession</code> will not
	 *         be used
	 */
	public boolean isCreateSessionAllowed() {
		return createSessionAllowed;
	}

	protected void sendStartAuthentication(ServletRequest request,
			ServletResponse response, FilterChain chain,
			AuthenticationException reason) throws ServletException,
			IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		SavedRequest savedRequest = new SavedRequest(httpRequest, portResolver);

		if (logger.isDebugEnabled()) {
			logger
					.debug("Authentication entry point being called; SavedRequest added to Session: "
							+ savedRequest);
		}

		if (createSessionAllowed) {
			// Store the HTTP request itself. Used by AbstractProcessingFilter
			// for redirection after successful authentication (SEC-29)
			httpRequest.getSession().setAttribute(
					AbstractProcessingFilter.ACEGI_SAVED_REQUEST_KEY,
					savedRequest);
		}

		// SEC-112: Clear the SecurityContextHolder's Authentication, as the
		// existing Authentication is no longer considered valid
		SecurityContextHolder.getContext().setAuthentication(null);

		authenticationEntryPoint.commence(httpRequest,
				(HttpServletResponse) response, reason);
	}

	public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
		Assert.notNull(accessDeniedHandler, "AccessDeniedHandler required");
		this.accessDeniedHandler = accessDeniedHandler;
	}

	public void setAuthenticationEntryPoint(
			AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	public void setAuthenticationTrustResolver(
			AuthenticationTrustResolver authenticationTrustResolver) {
		this.authenticationTrustResolver = authenticationTrustResolver;
	}

	public void setCreateSessionAllowed(boolean createSessionAllowed) {
		this.createSessionAllowed = createSessionAllowed;
	}

	public void setPortResolver(PortResolver portResolver) {
		this.portResolver = portResolver;
	}
}
