package org.ncbo.stanford.bean;

public class OntologyVersionIdBean {

	private String ontologyVersionId = null;
	private boolean isUmls;

	/**
	 * @param ontologyVersionId
	 */
	public OntologyVersionIdBean(String ontologyVersionId) {
		super();
		this.ontologyVersionId = ontologyVersionId;

		try {
			Integer.parseInt(ontologyVersionId);
		} catch (NumberFormatException e) {
			isUmls = true;
		}
	}

	/**
	 * @return the ontologyVersionId
	 */
	public String getOntologyVersionId() {
		return ontologyVersionId;
	}

	/**
	 * @return the isUmls
	 */
	public boolean isUmls() {
		return isUmls;
	}
}
