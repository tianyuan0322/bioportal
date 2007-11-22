/**
 * 
 */
package org.ncbo.stanford.util.security.providers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.ncbo.stanford.bean.ApplicationBean;
import org.ncbo.stanford.util.security.ui.ApplicationAuthenticationDetails;
import org.springframework.context.ApplicationContextException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;

/**
 * @author Michael Dorf
 * 
 */
public class ApplicationAuthenticationProvider extends JdbcDaoSupport implements
		AuthenticationProvider {

	public static final String DEF_APPLICATION_BY_APPLICATIONID_QUERY = "SELECT application_id, application_name FROM admin_application WHERE application_id = ?";

	private String applicationByApplicationIdQuery;
	protected MappingSqlQuery applicationByApplicationIdMapping;

	public ApplicationAuthenticationProvider() {
		applicationByApplicationIdQuery = DEF_APPLICATION_BY_APPLICATIONID_QUERY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.acegisecurity.providers.AuthenticationProvider#authenticate(org.acegisecurity.Authentication)
	 */
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		ApplicationAuthenticationDetails details = (ApplicationAuthenticationDetails) ((UsernamePasswordAuthenticationToken) authentication)
				.getDetails();
		String applicationId = details.getApplicationId();

		List applications = applicationByApplicationIdMapping.execute(applicationId);		
		
        if (applications.size() == 0) {
            throw new BadCredentialsException("Application: " + applicationId + " not found");
        }

        return authentication;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.acegisecurity.providers.AuthenticationProvider#supports(java.lang.Class)
	 */
	public boolean supports(Class authentication) {
		return (UsernamePasswordAuthenticationToken.class
				.isAssignableFrom(authentication));
	}

	/**
	 * @return the applicationByApplicationIdQuery
	 */
	public String getApplicationByApplicationIdQuery() {
		return applicationByApplicationIdQuery;
	}

	/**
	 * @param applicationByApplicationIdQuery
	 *            the applicationByApplicationIdQuery to set
	 */
	public void setApplicationByApplicationIdQuery(
			String applicationByApplicationIdQuery) {
		this.applicationByApplicationIdQuery = applicationByApplicationIdQuery;
	}

	protected void initDao() throws ApplicationContextException {
		this.applicationByApplicationIdMapping = new ApplicationByApplicationIdMapping(
				getDataSource());
	}

	/**
	 * Query object to look up an application.
	 */
	protected class ApplicationByApplicationIdMapping extends MappingSqlQuery {
		protected ApplicationByApplicationIdMapping(DataSource ds) {
			super(ds, applicationByApplicationIdQuery);
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			String applicationId = rs.getString(1);
			String name = rs.getString(2);

			ApplicationBean application = new ApplicationBean();
			application.setApplicationId(applicationId);
			application.setName(name);

			return application;
		}
	}
}
