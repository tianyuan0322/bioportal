package org.ncbo.stanford.bean;

import java.io.Serializable;

import org.ncbo.stanford.domain.generated.NcboAdminApplication;

public class ApplicationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String applicationId;
	private String name;
	private String description;

	/**
	 * Populates this instance with data from an entity bean
	 */
	public void populateFromEntity(NcboAdminApplication app) {
		if (app != null) {
			setApplicationId(app.getApplicationId());
			setName(app.getApplicationName());
			setDescription(app.getApplicationDescription());
		}
	}

	/**
	 * @return the applicationId
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * @param applicationId
	 *            the applicationId to set
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
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
}
