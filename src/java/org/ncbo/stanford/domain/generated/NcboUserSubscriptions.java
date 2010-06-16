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

	/** minimal constructor */
	public NcboUserSubscriptions(Integer userId) {
		super(userId);
	}

	/** full constructor */
	public NcboUserSubscriptions(Integer userId, String ontologyId) {
		super(userId, ontologyId);
	}

}