package org.ncbo.stanford.domain.generated;

import java.util.Set;

/**
 * NcboLUsageEventType entity. @author MyEclipse Persistence Tools
 */
public class NcboLUsageEventType extends AbstractNcboLUsageEventType implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboLUsageEventType() {
	}

	/** minimal constructor */
	public NcboLUsageEventType(Integer id, String eventName) {
		super(id, eventName);
	}

	/** full constructor */
	public NcboLUsageEventType(Integer id, String eventName,
			String eventDescription, Set ncboUsageLogs) {
		super(id, eventName, eventDescription, ncboUsageLogs);
	}

}
