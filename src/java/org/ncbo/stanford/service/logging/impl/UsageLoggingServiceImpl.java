package org.ncbo.stanford.service.logging.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.logging.UsageLoggingBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboLUsageEventTypeDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUsageLogDAO;
import org.ncbo.stanford.domain.generated.NcboUsageLog;
import org.ncbo.stanford.service.logging.UsageLoggingService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UsageLoggingServiceImpl implements UsageLoggingService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(UsageLoggingServiceImpl.class);

	private CustomNcboUsageLogDAO ncboUsageLogDAO;
	private CustomNcboLUsageEventTypeDAO ncboLUsageEventTypeDAO;

	public void logUsage(UsageLoggingBean usageLoggingBean) {
		NcboUsageLog usageLog = new NcboUsageLog();
		usageLoggingBean.populateToEntity(ncboLUsageEventTypeDAO, usageLog);
		ncboUsageLogDAO.save(usageLog);
	}

	public List<UsageLoggingBean> extractUsage(UsageLoggingBean usageLoggingBean) {
		List<UsageLoggingBean> results = new ArrayList<UsageLoggingBean>(0);
		List<NcboUsageLog> entityMatches = ncboUsageLogDAO
				.findUsageLogByCriteria(usageLoggingBean.getApplicationId(),
						usageLoggingBean.getRequestUrl(), usageLoggingBean
								.getStartDateAccessed(), usageLoggingBean
								.getEndDateAccessed());

		for (NcboUsageLog entityMatch : entityMatches) {
			UsageLoggingBean result = new UsageLoggingBean();
			result.populateFromEntity(entityMatch);
			results.add(result);
		}

		return results;
	}

	/**
	 * @param ncboUsageLogDAO
	 *            the ncboUsageLogDAO to set
	 */
	public void setNcboUsageLogDAO(CustomNcboUsageLogDAO ncboUsageLogDAO) {
		this.ncboUsageLogDAO = ncboUsageLogDAO;
	}

	/**
	 * @param ncboLUsageEventTypeDAO
	 *            the ncboLUsageEventTypeDAO to set
	 */
	public void setNcboLUsageEventTypeDAO(
			CustomNcboLUsageEventTypeDAO ncboLUsageEventTypeDAO) {
		this.ncboLUsageEventTypeDAO = ncboLUsageEventTypeDAO;
	}
}
