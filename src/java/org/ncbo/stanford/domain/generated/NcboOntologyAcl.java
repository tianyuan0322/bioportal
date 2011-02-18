package org.ncbo.stanford.domain.generated;

/**
 * NcboOntologyAcl entity. @author MyEclipse Persistence Tools
 */
public class NcboOntologyAcl extends AbstractNcboOntologyAcl implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboOntologyAcl() {
	}

	/** full constructor */
	public NcboOntologyAcl(NcboUser ncboUser, Integer ontologyId,
			Boolean isOwner) {
		super(ncboUser, ontologyId, isOwner);
	}

}
