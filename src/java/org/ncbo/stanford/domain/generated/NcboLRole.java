package org.ncbo.stanford.domain.generated;

import java.util.Set;

/**
 * NcboLRole entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboLRole extends AbstractNcboLRole implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboLRole() {
	}

	/** minimal constructor */
	public NcboLRole(Integer id, String name) {
		super(id, name);
	}

	/** full constructor */
	public NcboLRole(Integer id, String name, String description,
			Set ncboUserRoles) {
		super(id, name, description, ncboUserRoles);
	}

}
