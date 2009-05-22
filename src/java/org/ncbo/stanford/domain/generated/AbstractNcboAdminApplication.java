package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboAdminApplication entity provides the base persistence definition
 * of the NcboAdminApplication entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboAdminApplication implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private String applicationId;
	private String applicationName;
	private String applicationDescription;

	// Constructors

	/** default constructor */
	public AbstractNcboAdminApplication() {
	}

	/** minimal constructor */
	public AbstractNcboAdminApplication(Integer id, String applicationId,
			String applicationName) {
		this.id = id;
		this.applicationId = applicationId;
		this.applicationName = applicationName;
	}

	/** full constructor */
	public AbstractNcboAdminApplication(Integer id, String applicationId,
			String applicationName, String applicationDescription) {
		this.id = id;
		this.applicationId = applicationId;
		this.applicationName = applicationName;
		this.applicationDescription = applicationDescription;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getApplicationId() {
		return this.applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationName() {
		return this.applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationDescription() {
		return this.applicationDescription;
	}

	public void setApplicationDescription(String applicationDescription) {
		this.applicationDescription = applicationDescription;
	}

}