package org.ncbo.stanford.util.security.userdetails;

import org.ncbo.stanford.bean.ApplicationBean;
import org.ncbo.stanford.exception.ApplicationNotFoundException;
import org.springframework.dao.DataAccessException;

/**
 * Provides an interface for application details service used by the
 * authentication service
 * 
 * @author Michael Dorf
 * 
 */
public interface ApplicationDetailsService {

	/**
	 * Retrieve application from the database using its application id
	 * 
	 * @param applicationId
	 * @return
	 * @throws ApplicationNotFoundException
	 * @throws DataAccessException
	 */
	ApplicationBean loadApplicationByApplicationId(String applicationId)
			throws ApplicationNotFoundException, DataAccessException;
}
