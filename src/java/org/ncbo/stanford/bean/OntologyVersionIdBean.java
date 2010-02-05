package org.ncbo.stanford.bean;

public class OntologyVersionIdBean extends AbstractIdBean {

	/**
	 * @param ontologyVersionId
	 */
	public OntologyVersionIdBean(Integer ontologyVersionId) {
		super(ontologyVersionId);
	}

	/**
	 * @return the ontologyVersionId
	 */
	public Integer getOntologyVersionId() {
		return getId();
	}
}
