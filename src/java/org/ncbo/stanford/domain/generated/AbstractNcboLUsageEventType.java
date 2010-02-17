package org.ncbo.stanford.domain.generated;

import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboLUsageEventType entity provides the base persistence definition
 * of the NcboLUsageEventType entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboLUsageEventType implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private String eventName;
	private String eventDescription;
	private Set ncboUsageLogs = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboLUsageEventType() {
	}

	/** minimal constructor */
	public AbstractNcboLUsageEventType(Integer id, String eventName) {
		this.id = id;
		this.eventName = eventName;
	}

	/** full constructor */
	public AbstractNcboLUsageEventType(Integer id, String eventName,
			String eventDescription, Set ncboUsageLogs) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.ncboUsageLogs = ncboUsageLogs;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEventName() {
		return this.eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventDescription() {
		return this.eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public Set getNcboUsageLogs() {
		return this.ncboUsageLogs;
	}

	public void setNcboUsageLogs(Set ncboUsageLogs) {
		this.ncboUsageLogs = ncboUsageLogs;
	}

}