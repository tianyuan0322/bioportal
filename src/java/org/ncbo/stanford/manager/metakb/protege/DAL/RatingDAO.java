package org.ncbo.stanford.manager.metakb.protege.DAL;

import org.ncbo.stanford.bean.metadata.RatingBean;
import org.ncbo.stanford.bean.metadata.RatingTypeBean;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDALayer;
import org.ncbo.stanford.manager.metakb.protege.base.PropertyMapSet;
import org.ncbo.stanford.manager.metakb.protege.base.DAO.TimestampedDAO;
import org.ncbo.stanford.manager.metakb.protege.base.prop.DatatypePropertyMap;
import org.ncbo.stanford.manager.metakb.protege.base.prop.ObjectPropertyMap;

/**
 * Data access object for storing {@link RatingBean} objects to the metadata KB.
 * 
 * @author Tony Loeser
 */
public class RatingDAO extends TimestampedDAO<RatingBean> {
	
	private static String OWL_CLASS_NAME = PREFIX_METADATA + "Rating";
	
	public RatingDAO(AbstractDALayer daLayer) {
		super(daLayer, OWL_CLASS_NAME);
	}


	// ============================================================
	// Properties

	private static final String PROP_URI_HAS_RATING_TYPE = PREFIX_METADATA + "hasRatingType";
	private static final String PROP_URI_HAS_VALUE = PREFIX_METADATA + "hasValue";

	@Override
	protected void initializePropertyMaps(PropertyMapSet maps) {
		maps.add(new ObjectPropertyMap("type", RatingTypeBean.class, SINGLE_VALUE,
				     				   PROP_URI_HAS_RATING_TYPE));
		maps.add(new DatatypePropertyMap("value", Integer.class, SINGLE_VALUE,
										 PROP_URI_HAS_VALUE));
	}
}
