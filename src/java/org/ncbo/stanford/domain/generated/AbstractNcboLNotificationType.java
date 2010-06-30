package org.ncbo.stanford.domain.generated;

import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboLNotificationType entity provides the base persistence definition
 * of the NcboLNotificationType entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboLNotificationType implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private String type;
	private Set ncboUserSubscriptionses = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboLNotificationType() {
	}

	/** minimal constructor */
	public AbstractNcboLNotificationType(Integer id, String type) {
		this.id = id;
		this.type = type;
	}

	/** full constructor */
	public AbstractNcboLNotificationType(Integer id, String type,
			Set ncboUserSubscriptionses) {
		this.id = id;
		this.type = type;
		this.ncboUserSubscriptionses = ncboUserSubscriptionses;
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

	public Set getNcboUserSubscriptionses() {
		return this.ncboUserSubscriptionses;
	}

	public void setNcboUserSubscriptionses(Set ncboUserSubscriptionses) {
		this.ncboUserSubscriptionses = ncboUserSubscriptionses;
	}

}