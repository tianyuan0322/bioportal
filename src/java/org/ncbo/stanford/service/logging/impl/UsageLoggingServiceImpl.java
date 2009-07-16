package org.ncbo.stanford.service.logging.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.logging.UsageLoggingBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUsageLogDAO;
import org.ncbo.stanford.domain.generated.NcboUsageLog;
import org.ncbo.stanford.service.logging.UsageLoggingService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UsageLoggingServiceImpl implements UsageLoggingService {

	 public static void main(String[] args) {
	 UsageLoggingBean bean = new UsageLoggingBean();
//	 bean.setApplicationId("4ea81d74-8960-4525-810b-fa1baab576ff");
			
//	 SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
//	 String today = f.format(Calendar.getInstance().getTime());
//			
//	 bean.setDateAccessed(DateHelper.getTodaysDate());
//	 bean.setHttpMethod("GET");
//	 bean.setRequestParameters("ontlogyids=1032");
//	 bean.setRequestUrl("/bioportal/search/melanoma");
	
	 System.out.println(bean.hashCode());
	 }

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(UsageLoggingServiceImpl.class);

	private CustomNcboUsageLogDAO ncboUsageLogDAO;

	public void logUsage(UsageLoggingBean usageLoggingBean) {
		NcboUsageLog usageLog = ncboUsageLogDAO
				.findUsageLogByHashCode(usageLoggingBean.hashCode());
		Integer newCount = 1;

		if (usageLog != null) {
			newCount = usageLog.getHitCount() + 1;
		} else {
			usageLog = new NcboUsageLog();
			usageLoggingBean.populateToEntity(usageLog);
		}

		usageLog.setHitCount(newCount);
		usageLoggingBean.setHitCount(newCount);

		ncboUsageLogDAO.save(usageLog);
	}

	/**
	 * @param ncboUsageLogDAO
	 *            the ncboUsageLogDAO to set
	 */
	public void setNcboUsageLogDAO(CustomNcboUsageLogDAO ncboUsageLogDAO) {
		this.ncboUsageLogDAO = ncboUsageLogDAO;
	}

	public List<UsageLoggingBean> extractUsage(UsageLoggingBean usageLoggingBean) {
		List<UsageLoggingBean> results = new ArrayList<UsageLoggingBean>(0);
		List<NcboUsageLog> entityMatches = ncboUsageLogDAO
				.findUsageLogByCriteria(usageLoggingBean.getApplicationId(),
						usageLoggingBean.getRequestUrl(), usageLoggingBean
								.getResourceParameters(), usageLoggingBean
								.getStartDateAccessed(), usageLoggingBean
								.getEndDateAccessed());

		for (NcboUsageLog entityMatch : entityMatches) {
			UsageLoggingBean result = new UsageLoggingBean();
			result.populateFromEntity(entityMatch);
			results.add(result);
		}

		return results;
	}
}
