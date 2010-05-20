package org.ncbo.stanford.manager.metakb.protege.DAO;

import org.ncbo.stanford.bean.RatingBean;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * Data access object for storing {@link RatingBean} objects to the metadta KB.
 * 
 * @author Tony Loeser
 */
public class RatingDAO extends AbstractDAO<RatingBean> {
	
	private static String OWL_CLASS_NAME = "metadata:Rating";

	public RatingDAO(OWLModel metadataKb) {
		super(OWL_CLASS_NAME, metadataKb);
	}
	
	// ============================================================
	// Properties

	private static final String PROP_URI_HAS_RATING_TYPE = PREFIX_METADATA + "hasRatingType";
	private static final String PROP_URI_HAS_VALUE = PREFIX_METADATA + "hasValue";

	// Override
	protected void initializePropertyMaps() {
		addObjectPropertyMap("type", false, PROP_URI_HAS_RATING_TYPE, new RatingTypeDAO(metadataKb));
		addDatatypePropertyMap("value", Integer.class, false, PROP_URI_HAS_VALUE);
	}
}
