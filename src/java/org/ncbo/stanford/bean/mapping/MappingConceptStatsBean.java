package org.ncbo.stanford.bean.mapping;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("conceptMappingStatistics")
public class MappingConceptStatsBean {

	private String fullId;
	private Integer count;

	/**
	 * Default no-arg constuctor.
	 */
	public MappingConceptStatsBean() {
	}

	/**
	 * @return the fullId
	 */
	public String getFullId() {
		return fullId;
	}

	/**
	 * @param fullId
	 *            the fullId to set
	 */
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}
}
