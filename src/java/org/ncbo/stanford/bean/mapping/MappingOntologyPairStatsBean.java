package org.ncbo.stanford.bean.mapping;

public class MappingOntologyPairStatsBean {

	private Integer ontology1;
	private Integer ontology2;
	private Integer totalMappings;

	public Integer getOntology1() {
		return ontology1;
	}

	public void setOntology1(Integer ontology1) {
		this.ontology1 = ontology1;
	}

	public Integer getOntology2() {
		return ontology2;
	}

	public void setOntology2(Integer ontology2) {
		this.ontology2 = ontology2;
	}

	public Integer getTotalMappings() {
		return totalMappings;
	}

	public void setTotalMappings(Integer totalMappings) {
		this.totalMappings = totalMappings;
	}
}
