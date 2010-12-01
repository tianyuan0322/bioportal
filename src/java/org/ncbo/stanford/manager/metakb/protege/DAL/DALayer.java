package org.ncbo.stanford.manager.metakb.protege.DAL;

import org.ncbo.stanford.manager.metakb.protege.base.AbstractDALayer;

public class DALayer extends AbstractDALayer {
	
	public DALayer(AbstractDALayer.MetadataKbProvider kbProvider) {
		super(kbProvider);
	}

	@Override
	protected void createDAOs() {
		// Add all of this layer's DAOs.
		addDAO(new ProjectDAO(this));
		addDAO(new RatingDAO(this));
		addDAO(new RatingTypeDAO(this));
		addDAO(new ReviewDAO(this));
	}

}
