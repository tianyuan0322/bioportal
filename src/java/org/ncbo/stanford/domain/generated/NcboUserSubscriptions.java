package org.ncbo.stanford.domain.generated;

/**
 * NcboUserSubscriptions entity. @author MyEclipse Persistence Tools
 */
public class NcboUserSubscriptions extends AbstractNcboUserSubscriptions
		implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboUserSubscriptions() {
	}

	/** full constructor */
	public NcboUserSubscriptions(NcboLNotificationType ncboLNotificationType,
			Integer userId, String ontologyId) {
		super(ncboLNotificationType, userId, ontologyId);
	}

}
