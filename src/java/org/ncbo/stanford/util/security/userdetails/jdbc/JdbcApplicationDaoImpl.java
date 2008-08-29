/**
 * 
 */
package org.ncbo.stanford.util.security.userdetails.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.ncbo.stanford.bean.ApplicationBean;
import org.ncbo.stanford.exception.ApplicationNotFoundException;
import org.ncbo.stanford.util.security.userdetails.ApplicationDetailsService;
import org.springframework.context.ApplicationContextException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;

/**
 * Mimics Acegi's JdbcDaoImpl implementation to extract an application from the
 * database
 * 
 * @author Michael Dorf
 * 
 */
public class JdbcApplicationDaoImpl extends JdbcDaoSupport implements
		ApplicationDetailsService {

	public static final String DEF_APPLICATION_BY_APPLICATIONID_QUERY = "SELECT application_id, application_name FROM admin_application WHERE application_id = ?";

	private String applicationByApplicationIdQuery;
	protected MappingSqlQuery applicationByApplicationIdMapping;

	public JdbcApplicationDaoImpl() {
		applicationByApplicationIdQuery = DEF_APPLICATION_BY_APPLICATIONID_QUERY;
	}

	/**
	 * Loads an application using its application id
	 * 
	 * @param applicationId
	 */
	public ApplicationBean loadApplicationByApplicationId(String applicationId) {
		List applications = applicationByApplicationIdMapping
				.execute(applicationId);

		if (applications.size() == 0) {
			throw new ApplicationNotFoundException("Application: "
					+ applicationId + " not found");
		}

		return (ApplicationBean) applications.get(0);
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
