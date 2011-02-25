package org.ncbo.stanford.manager.metakb.protege.DAL;

import org.ncbo.stanford.bean.metadata.RatingBean;
import org.ncbo.stanford.bean.metadata.ReviewBean;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDALayer;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDAO;
import org.ncbo.stanford.manager.metakb.protege.base.prop.DatatypePropertyMap;
import org.ncbo.stanford.manager.metakb.protege.base.prop.ObjectPropertyMap;

/**
 * Data access object for storing {@link ReviewBean} objects to the Metadata KB.
 * 
 * @author Tony Loeser
 */
public class ReviewDAO extends AbstractDAO<ReviewBean> {
	
	private static String OWL_CLASS_NAME = PREFIX_METADATA + "Review";

	public ReviewDAO(AbstractDALayer daLayer) {
		super(daLayer, OWL_CLASS_NAME);
	}

	
	// ============================================================
	// Properties

	private static final String PROP_URI_TEXT = PREFIX_METADATA + "text";
	private static final String PROP_URI_USER_ID = PREFIX_METADATA + "userId";
	private static final String PROP_URI_PROJECT_ID = PREFIX_METADATA + "projectId";
	private static final String PROP_URI_ONTOLOGY_ID = PREFIX_METADATA + "ontologyId";
	private static final String PROP_URI_HAS_RATING = PREFIX_METADATA + "hasRating";

	@Override
	protected void initializePropertyMaps() {
		addMap(new DatatypePropertyMap("text", String.class, SINGLE_VALUE, PROP_URI_TEXT));
		addMap(new DatatypePropertyMap("userId", Integer.class, SINGLE_VALUE, PROP_URI_USER_ID));
		addMap(new DatatypePropertyMap("projectId", Integer.class, SINGLE_VALUE, PROP_URI_PROJECT_ID));
		addMap(new DatatypePropertyMap("ontologyId", Integer.class, SINGLE_VALUE, PROP_URI_ONTOLOGY_ID));
		addMap(new ObjectPropertyMap("ratings", RatingBean.class, COLLECTION, PROP_URI_HAS_RATING));
	}
}
