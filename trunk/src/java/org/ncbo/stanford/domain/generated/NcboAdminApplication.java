package org.ncbo.stanford.domain.generated;

/**
 * NcboAdminApplication entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboAdminApplication extends AbstractNcboAdminApplication
		implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboAdminApplication() {
	}

	/** minimal constructor */
	public NcboAdminApplication(Integer id, String applicationId,
			String applicationName) {
		super(id, applicationId, applicationName);
	}

	/** full constructor */
	public NcboAdminApplication(Integer id, String applicationId,
			String applicationName, String applicationDescription) {
		super(id, applicationId, applicationName, applicationDescription);
	}

}
