package org.ncbo.stanford.bean.mapping;

import java.util.List;

import org.ncbo.stanford.bean.concept.ConceptOntologyPairBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("mappingConceptSetResults")
public class MappingResultConceptSetBean {

	private List<MappingBean> mappings;
	private List<ConceptOntologyPairBean> errorConcepts;

	public MappingResultConceptSetBean() {
	}

	public List<MappingBean> getMappings() {
		return mappings;
	}

	public void setMappings(List<MappingBean> mappings) {
		this.mappings = mappings;
	}

	public List<ConceptOntologyPairBean> getErrorConcepts() {
		return errorConcepts;
	}

	public void setErrorConcepts(List<ConceptOntologyPairBean> errorConcepts) {
		this.errorConcepts = errorConcepts;
	}

}
