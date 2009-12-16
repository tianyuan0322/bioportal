package org.ncbo.stanford.util.security.providers.dao;

import org.ncbo.stanford.bean.ApplicationBean;
import org.ncbo.stanford.util.security.userdetails.ApplicationDetailsService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.DaoAuthenticationProvider;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.PlaintextPasswordEncoder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.util.Assert;

/**
 * This class is a custom implementation of Acegi's
 * {@link DaoAuthenticationProvider} to allow for additional authentication of
 * an application. This is an {@link AuthenticationProvider} implementation that
 * retrieves application details form {@link ApplicationDetailsService} and user
 * details from an {@link UserDetailsService}.
 * 
 * @author Michael Dorf
 */
public class RESTfulDaoAuthenticationProvider extends
		AbstractRESTfulUserDetailsAuthenticationProvider {

	// ~ Instance fields
	// ================================================================================================

	private PasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();
	private SaltSource saltSource;
	private UserDetailsService userDetailsService;
	private ApplicationDetailsService applicationDetailsService;
	private boolean includeDetailsObject = true;

	// ~ Methods
	// ========================================================================================================

	protected void additionalAuthenticationChecks(UserDetails userDetails,
			ApplicationBean application,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		Object salt = null;

		if (this.saltSource != null) {
			salt = this.saltSource.getSalt(userDetails);
		}

		if (authentication.getCredentials() == null) {
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), includeDetailsObject ? userDetails
					: null);
		}

		if (application.getApplicationId() == null) {
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), includeDetailsObject ? userDetails
					: null);
		}

		String presentedPassword = authentication.getCredentials() == null ? ""
				: authentication.getCredentials().toString();

		if (!passwordEncoder.isPasswordValid(userDetails.getPassword(),
				presentedPassword, salt)) {
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), includeDetailsObject ? userDetails
					: null);
		}
	}

	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsService,
				"A UserDetailsService must be set");
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public SaltSource getSaltSource() {
		return saltSource;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	/**
	 * Retrieve user form database
	 */
	protected final UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails loadedUser;

		try {
			loadedUser = this.getUserDetailsService().loadUserByUsername(
					username);
		} catch (DataAccessException repositoryProblem) {
			throw new AuthenticationServiceException(repositoryProblem
					.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new AuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}

		return loadedUser;
	}

	/**
	 * Retrieve application form database
	 */
	protected final ApplicationBean retrieveApplication(String applicationId,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		ApplicationBean loadedApplication;

		try {
			loadedApplication = this.getApplicationDetailsService()
					.loadApplicationByApplicationId(applicationId);
		} catch (DataAccessException repositoryProblem) {
			throw new AuthenticationServiceException(repositoryProblem
					.getMessage(), repositoryProblem);
		}

		if (loadedApplication == null) {
			throw new AuthenticationServiceException(
					"ApplicationDetailsService returned null, which is an interface contract violation");
		}

		return loadedApplication;
	}

	/**
	 * Sets the PasswordEncoder instance to be used to encode and validate
	 * passwords. If not set, {@link PlaintextPasswordEncoder} will be used by
	 * default.
	 * 
	 * @param passwordEncoder
	 *            The passwordEncoder to use
	 */
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * The source of salts to use when decoding passwords. <code>null</code>
	 * is a valid value, meaning the <code>DaoAuthenticationProvider</code>
	 * will present <code>null</code> to the relevant
	 * <code>PasswordEncoder</code>.
	 * 
	 * @param saltSource
	 *            to use when attempting to decode passwords via the
	 *            <code>PasswordEncoder</code>
	 */
	public void setSaltSource(SaltSource saltSource) {
		this.saltSource = saltSource;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public boolean isIncludeDetailsObject() {
		return includeDetailsObject;
	}

	public void setIncludeDetailsObject(boolean includeDetailsObject) {
		this.includeDetailsObject = includeDetailsObject;
	}

	/**
	 * @return the applicationDetailsService
	 */
	public ApplicationDetailsService getApplicationDetailsService() {
		return applicationDetailsService;
	}

	/**
	 * @param applicationDetailsService
	 *            the applicationDetailsService to set
	 */
	public void setApplicationDetailsService(
			ApplicationDetailsService applicationDetailsService) {
		this.applicationDetailsService = applicationDetailsService;
	}
}
