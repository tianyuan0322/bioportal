package org.ncbo.stanford.domain.generated;

/**
 * AbstractNcboLNotificationType entity provides the base persistence definition
 * of the NcboLNotificationType entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboLNotificationType implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private String type;

	// Constructors

	/** default constructor */
	public AbstractNcboLNotificationType() {
	}

	/** full constructor */
	public AbstractNcboLNotificationType(String type) {
		this.type = type;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}