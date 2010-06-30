package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboUserSubscriptions entity provides the base persistence definition
 * of the NcboUserSubscriptions entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboUserSubscriptions implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private NcboLNotificationType ncboLNotificationType;
	private Integer userId;
	private String ontologyId;

	// Constructors

	/** default constructor */
	public AbstractNcboUserSubscriptions() {
	}

	/** full constructor */
	public AbstractNcboUserSubscriptions(
			NcboLNotificationType ncboLNotificationType, Integer userId,
			String ontologyId) {
		this.ncboLNotificationType = ncboLNotificationType;
		this.userId = userId;
		this.ontologyId = ontologyId;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NcboLNotificationType getNcboLNotificationType() {
		return this.ncboLNotificationType;
	}

	public void setNcboLNotificationType(
			NcboLNotificationType ncboLNotificationType) {
		this.ncboLNotificationType = ncboLNotificationType;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getOntologyId() {
		return this.ontologyId;
	}

	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}

}