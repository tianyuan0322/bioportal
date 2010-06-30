package org.ncbo.stanford.domain.generated;

import java.util.Set;

/**
 * NcboLNotificationType entity. @author MyEclipse Persistence Tools
 */
public class NcboLNotificationType extends AbstractNcboLNotificationType
		implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboLNotificationType() {
	}

	/** minimal constructor */
	public NcboLNotificationType(Integer id, String type) {
		super(id, type);
	}

	/** full constructor */
	public NcboLNotificationType(Integer id, String type,
			Set ncboUserSubscriptionses) {
		super(id, type, ncboUserSubscriptionses);
	}

}
