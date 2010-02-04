package org.ncbo.stanford.bean;

public class OntologyIdBean extends AbstractIdBean {

	/**
	 * @param ontologyId
	 */
	public OntologyIdBean(Integer ontologyId) {
		super(ontologyId);
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return getId();
	}
}
