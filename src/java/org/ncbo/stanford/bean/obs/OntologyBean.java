package org.ncbo.stanford.bean.obs;

public class OntologyBean {
	private Integer id;
	private Integer localOntologyId;
	private String name;
	private String version;
	private String description;
	private String status;
	private String virtualOntologyId;
	private String format;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the localOntologyId
	 */
	public Integer getLocalOntologyId() {
		return localOntologyId;
	}

	/**
	 * @param localOntologyId
	 *            the localOntologyId to set
	 */
	public void setLocalOntologyId(Integer localOntologyId) {
		this.localOntologyId = localOntologyId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the virtualOntologyId
	 */
	public String getVirtualOntologyId() {
		return virtualOntologyId;
	}

	/**
	 * @param virtualOntologyId
	 *            the virtualOntologyId to set
	 */
	public void setVirtualOntologyId(String virtualOntologyId) {
		this.virtualOntologyId = virtualOntologyId;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
}
