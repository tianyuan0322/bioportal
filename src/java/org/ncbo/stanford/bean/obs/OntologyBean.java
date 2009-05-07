package org.ncbo.stanford.bean.obs;

public class OntologyBean {
	private String localOntologyID;
	private String virtualOntologyID;
	private String ontologyName;
	private String ontologyVersion;

	/**
	 * @return the localOntologyID
	 */
	public String getLocalOntologyID() {
		return localOntologyID;
	}

	/**
	 * @param localOntologyID
	 *            the localOntologyID to set
	 */
	public void setLocalOntologyID(String localOntologyID) {
		this.localOntologyID = localOntologyID;
	}

	/**
	 * @return the virtualOntologyID
	 */
	public String getVirtualOntologyID() {
		return virtualOntologyID;
	}

	/**
	 * @param virtualOntologyID
	 *            the virtualOntologyID to set
	 */
	public void setVirtualOntologyID(String virtualOntologyID) {
		this.virtualOntologyID = virtualOntologyID;
	}

	/**
	 * @return the ontologyName
	 */
	public String getOntologyName() {
		return ontologyName;
	}

	/**
	 * @param ontologyName
	 *            the ontologyName to set
	 */
	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

	/**
	 * @return the ontologyVersion
	 */
	public String getOntologyVersion() {
		return ontologyVersion;
	}

	/**
	 * @param ontologyVersion
	 *            the ontologyVersion to set
	 */
	public void setOntologyVersion(String ontologyVersion) {
		this.ontologyVersion = ontologyVersion;
	}
}
