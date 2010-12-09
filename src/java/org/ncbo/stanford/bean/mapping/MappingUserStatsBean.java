package org.ncbo.stanford.bean.mapping;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("userStatisticsBean")
public class MappingUserStatsBean {

	private Integer userId;
	private Integer mappingCount;

	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return the mappingCount
	 */
	public Integer getMappingCount() {
		return mappingCount;
	}

	/**
	 * @param mappingCount
	 *            the mappingCount to set
	 */
	public void setMappingCount(Integer mappingCount) {
		this.mappingCount = mappingCount;
	}

}
