package org.ncbo.stanford.domain.custom.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.ncbo.stanford.domain.generated.NcboUsageLog;
import org.ncbo.stanford.domain.generated.NcboUsageLogDAO;
import org.ncbo.stanford.util.helper.StringHelper;

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

	@SuppressWarnings("unchecked")
	public List<NcboUsageLog> findUsageLogByCriteria(String applicationId,
			String requestUrl, String resourceParameters, Date startDate,
			Date endDate) {
		Criteria criteria = getSession().createCriteria(NcboUsageLog.class);

		if (applicationId != null) {
			if (StringHelper.isNullOrNullString(applicationId)) {
				criteria.add(Expression.isNull("applicationId"));
			} else {
				criteria.add(Expression.eq("applicationId", applicationId));
			}
		}

		if (requestUrl != null) {
			if (StringHelper.isNullOrNullString(requestUrl)) {
				criteria.add(Expression.isNull("requestUrl"));
			} else {
				criteria.add(Expression.like("requestUrl",
						'%' + requestUrl + '%'));
			}
		}

		if (resourceParameters != null) {
			if (StringHelper.isNullOrNullString(resourceParameters)) {
				criteria.add(Expression.isNull("resourceParameters"));
			} else {
				criteria.add(Expression.like("resourceParameters",
						'%' + resourceParameters + '%'));
			}
		}

		if (startDate != null && endDate != null) {
			criteria
					.add(Expression.between("dateAccessed", startDate, endDate));
		} else if (startDate != null) {
			criteria.add(Expression.ge("dateAccessed", startDate));
		} else if (endDate != null) {
			criteria.add(Expression.le("dateAccessed", endDate));
		}

		return criteria.list();
	}

	public NcboUsageLog findUsageLogByHashCode(Integer hashCode) {
		return (NcboUsageLog) getSession().createCriteria(
				"org.ncbo.stanford.domain.generated.NcboUsageLog").add(
				Expression.eq("hashCode", hashCode)).uniqueResult();
	}
}
