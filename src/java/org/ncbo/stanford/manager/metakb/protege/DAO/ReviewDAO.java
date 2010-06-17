package org.ncbo.stanford.manager.metakb.protege.DAO;

import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.PropertyMapSet;

/**
 * Data access object for storing {@link ReviewBean} objects to the Metadata KB.
 * 
 * @author Tony Loeser
 */
public class ReviewDAO extends AbstractDAO<ReviewBean> {
	
	private static String OWL_CLASS_NAME = "metadata:Review";

	@Override
	protected String getQualifiedClassName() {
		return OWL_CLASS_NAME;
	}

	
	// ============================================================
	// Properties

	private static final String PROP_URI_TEXT = PREFIX_METADATA + "text";
	private static final String PROP_URI_USER_ID = PREFIX_METADATA + "userId";
	private static final String PROP_URI_PROJECT_ID = PREFIX_METADATA + "projectId";
	private static final String PROP_URI_ONTOLOGY_ID = PREFIX_METADATA + "ontologyId";
	private static final String PROP_URI_HAS_RATING = PREFIX_METADATA + "hasRating";

	@Override
	protected void initializePropertyMaps(PropertyMapSet maps) {
		maps.addDatatypePropertyMap("text", String.class, SINGLE_VALUE, PROP_URI_TEXT);
		maps.addDatatypePropertyMap("userId", Integer.class, SINGLE_VALUE, PROP_URI_USER_ID);
		maps.addDatatypePropertyMap("projectId", Integer.class, SINGLE_VALUE, PROP_URI_PROJECT_ID);
		maps.addDatatypePropertyMap("ontologyId", Integer.class, SINGLE_VALUE, PROP_URI_ONTOLOGY_ID);
		maps.addObjectPropertyMap("ratings", COLLECTION, PROP_URI_HAS_RATING,
								  getSiblingDAO(RatingDAO.class));
	}
}
