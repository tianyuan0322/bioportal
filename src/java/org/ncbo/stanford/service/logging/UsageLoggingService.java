package org.ncbo.stanford.service.logging;

import java.util.List;

import org.ncbo.stanford.bean.logging.UsageLoggingBean;

public interface UsageLoggingService {

	public void logUsage(UsageLoggingBean usageLoggingBean);

	public List<UsageLoggingBean> extractUsage(UsageLoggingBean usageLoggingBean);
}
