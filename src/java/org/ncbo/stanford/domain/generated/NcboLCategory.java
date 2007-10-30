package org.ncbo.stanford.domain.generated;

import java.util.Set;

/**
 * NcboLCategory entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboLCategory extends AbstractNcboLCategory implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboLCategory() {
	}

	/** minimal constructor */
	public NcboLCategory(Integer id, String name) {
		super(id, name);
	}

	/** full constructor */
	public NcboLCategory(Integer id, NcboLCategory ncboLCategory, String name,
			Set ncboOntologyCategories, Set ncboLCategories) {
		super(id, ncboLCategory, name, ncboOntologyCategories, ncboLCategories);
	}

}
