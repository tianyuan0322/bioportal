package org.ncbo.stanford.bean;

public class OntologyVersionIdBean extends AbstractIdBean {

	/**
	 * @param ontologyVersionId
	 */
	public OntologyVersionIdBean(String ontologyVersionId) {
		super(ontologyVersionId);
	}

	/**
	 * @return the ontologyVersionId
	 */
	public String getOntologyVersionId() {
		return getId();
	}
}