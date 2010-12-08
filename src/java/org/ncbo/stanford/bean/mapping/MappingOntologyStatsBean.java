package org.ncbo.stanford.bean.mapping;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ontologyMappingStatistics")
public class MappingOntologyStatsBean {

	private Integer ontologyId;
	private Integer sourceMappings;
	private Integer targetMappings;
	private Integer totalMappings;

	/**
	 * Default no-arg constructor.
	 */
	public MappingOntologyStatsBean() {
		this.targetMappings = 0;
		this.sourceMappings = 0;
		this.totalMappings = 0;
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return ontologyId;
	}

	/**
	 * @param ontologyId the ontologyId to set
	 */
	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	/**
	 * @return the targetMappings
	 */
	public Integer getTargetMappings() {
		return targetMappings;
	}

	/**
	 * @param targetMappings the targetMappings to set
	 */
	public void setTargetMappings(Integer mappedTo) {
		this.targetMappings = mappedTo;
	}

	/**
	 * @return the sourceMappings
	 */
	public Integer getSourceMappings() {
		return sourceMappings;
	}

	/**
	 * @param sourceMappings the sourceMappings to set
	 */
	public void setSourceMappings(Integer mappedFrom) {
		this.sourceMappings = mappedFrom;
	}

	/**
	 * @return the totalMappings
	 */
	public Integer getTotalMappings() {
		return totalMappings;
	}

	/**
	 * @param totalMappings the totalMappings to set
	 */
	public void setTotalMappings(Integer totalMappingCount) {
		this.totalMappings = totalMappingCount;
	}

}
