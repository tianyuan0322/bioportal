package org.ncbo.stanford.domain.custom.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Expression;
import org.ncbo.stanford.domain.generated.NcboUsageLog;
import org.ncbo.stanford.domain.generated.NcboUsageLogDAO;

public class CustomNcboUsageLogDAO extends NcboUsageLogDAO {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(CustomNcboUsageLogDAO.class);

	/**
	 * 
	 */
	public CustomNcboUsageLogDAO() {
		super();
	}

	public NcboUsageLog findUsageLogByHashCode(Integer hashCode) {
		return (NcboUsageLog) getSession().createCriteria(
				"org.ncbo.stanford.domain.generated.NcboUsageLog").add(
				Expression.eq("hashCode", hashCode)).uniqueResult();
	}
}
