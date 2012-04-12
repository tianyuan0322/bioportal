package org.ncbo.stanford.bean.concept;

import java.util.List;

public class ConceptOntologyPairResponseBean {

	private List<ConceptOntologyPairBean> concepts;
	private List<String> conceptOntologyPairErrors;

	public ConceptOntologyPairResponseBean() {
	}

	public List<ConceptOntologyPairBean> getConcepts() {
		return concepts;
	}

	public void setConcepts(List<ConceptOntologyPairBean> concepts) {
		this.concepts = concepts;
	}

	public List<String> getConceptOntologyPairErrors() {
		return conceptOntologyPairErrors;
	}

	public void setConceptOntologyPairErrors(
			List<String> conceptOntologyPairErrors) {
		this.conceptOntologyPairErrors = conceptOntologyPairErrors;
	};
}
