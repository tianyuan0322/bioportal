package org.ncbo.stanford.bean.concept;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class is used as a simple data object for storing pairs of ontology and
 * concept ids for use in the batch service.
 * 
 * @author palexander
 * 
 */
@XStreamAlias("conceptOntologyPair")
public class ConceptOntologyPairBean {

	private Integer ontologyId;
	private String ontologyName;
	private String conceptId;
	private ClassBean concept;

	public ConceptOntologyPairBean() {
	}

	public ConceptOntologyPairBean(Integer ontologyId, String conceptId) {
		this.ontologyId = ontologyId;
		this.conceptId = conceptId;
	}

	public Integer getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public ClassBean getConcept() {
		return concept;
	}

	public void setConcept(ClassBean concept) {
		this.concept = concept;
	}

	public String getOntologyName() {
		return ontologyName;
	}

	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	};
}
