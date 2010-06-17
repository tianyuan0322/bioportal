package org.ncbo.stanford.manager.metakb.protege.DAO;

import org.ncbo.stanford.bean.RatingBean;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.PropertyMapSet;

/**
 * Data access object for storing {@link RatingBean} objects to the metadata KB.
 * 
 * @author Tony Loeser
 */
public class RatingDAO extends AbstractDAO<RatingBean> {
	
	private static String OWL_CLASS_NAME = "metadata:Rating";
	
	@Override
	protected String getQualifiedClassName() {
		return OWL_CLASS_NAME;
	}


	// ============================================================
	// Properties

	private static final String PROP_URI_HAS_RATING_TYPE = PREFIX_METADATA + "hasRatingType";
	private static final String PROP_URI_HAS_VALUE = PREFIX_METADATA + "hasValue";

	@Override
	protected void initializePropertyMaps(PropertyMapSet maps) {
		maps.addObjectPropertyMap("type", SINGLE_VALUE, PROP_URI_HAS_RATING_TYPE,
								  getSiblingDAO(RatingTypeDAO.class));
		maps.addDatatypePropertyMap("value", Integer.class, SINGLE_VALUE, PROP_URI_HAS_VALUE);
	}
}
