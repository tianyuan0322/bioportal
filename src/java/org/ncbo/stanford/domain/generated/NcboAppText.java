package org.ncbo.stanford.domain.generated;

import java.util.Date;

/**
 * NcboAppText entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboAppText extends AbstractNcboAppText implements
		java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboAppText() {
	}

	/** minimal constructor */
	public NcboAppText(String identifier,
			NcboLAppTextDatatype ncboLAppTextDatatype, Date dateCreated,
			Date dateUpdated) {
		super(identifier, ncboLAppTextDatatype, dateCreated, dateUpdated);
	}

	/** full constructor */
	public NcboAppText(String identifier,
			NcboLAppTextDatatype ncboLAppTextDatatype, String description,
			String textContent, String lastModifier, Date dateCreated,
			Date dateUpdated) {
		super(identifier, ncboLAppTextDatatype, description, textContent,
				lastModifier, dateCreated, dateUpdated);
	}

}
