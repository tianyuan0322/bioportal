package org.ncbo.stanford.bean.mapping;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ontologyPairMappingStatistics")
public class MappingOntologyPairStatsBean {

	private Integer sourceOntologyId;
	private Integer targetOntologyId;
	private Integer mappingCount;

	public Integer getSourceOntologyId() {
		return sourceOntologyId;
	}

	public void setSourceOntologyId(Integer sourceOntologyId) {
		this.sourceOntologyId = sourceOntologyId;
	}

	public Integer getTargetOntologyId() {
		return targetOntologyId;
	}

	public void setTargetOntologyId(Integer targetOntologyId) {
		this.targetOntologyId = targetOntologyId;
	}

	public Integer getMappingCount() {
		return mappingCount;
	}

	public void setMappingCount(Integer totalMappings) {
		this.mappingCount = totalMappings;
	}
}
