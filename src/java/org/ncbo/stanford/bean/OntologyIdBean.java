package org.ncbo.stanford.bean;

public class OntologyIdBean extends AbstractIdBean {

	/**
	 * @param ontologyId
	 */
	public OntologyIdBean(String ontologyId) {
		super(ontologyId);
	}

	/**
	 * @return the ontologyId
	 */
	public String getOntologyId() {
		return getId();
	}
}
