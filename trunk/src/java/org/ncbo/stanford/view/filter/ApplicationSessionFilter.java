/**
 * Package Declaration
 */

package org.ncbo.stanford.view.filter;

/**
 * Import Statements
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;

public class ApplicationSessionFilter extends OpenSessionInViewFilter {

	private static final Log log = LogFactory
			.getLog(ApplicationSessionFilter.class);

	public ApplicationSessionFilter() {
		super();
		log.debug("OpenSessionInViewFilter...  Initialised...");
	}

	/**
	 * we do a different flushmode than in the codebase here
	 */
	protected Session getSession(SessionFactory sessionFactory)
			throws DataAccessResourceFailureException {
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		session.setFlushMode(FlushMode.AUTO);
		log.debug("ApplicationSessionFilter.getSession() invoked...");

		return session;
	}

	/**
	 * we do an explicit flush here just in case we do not have an automated
	 * flush
	 */
	protected void closeSession(Session session, SessionFactory factory) {
		session.flush();
		super.closeSession(session, factory);
		log.debug("ApplicationSessionFilter.closeSession() invoked...");
	}
}
