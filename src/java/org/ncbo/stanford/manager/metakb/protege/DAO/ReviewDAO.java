package org.ncbo.stanford.manager.metakb.protege.DAO;

import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * Data access object for storing {@link ReviewBean} objects to the Metadata KB.
 * 
 * @author Tony Loeser
 */
public class ReviewDAO extends AbstractDAO<ReviewBean> {
	
	private static String OWL_CLASS_NAME = "metadata:Review";

	public ReviewDAO(OWLModel metadataKb) {
		super(OWL_CLASS_NAME, metadataKb);
	}
	
	// ============================================================
	// Properties

	private static final String PROP_URI_TEXT = PREFIX_METADATA + "text";
	private static final String PROP_URI_USER_ID = PREFIX_METADATA + "userId";
	private static final String PROP_URI_PROJECT_ID = PREFIX_METADATA + "projectId";
	private static final String PROP_URI_ONTOLOGY_ID = PREFIX_METADATA + "ontologyId";
	private static final String PROP_URI_HAS_RATING = PREFIX_METADATA + "hasRating";

	// Override
	protected void initializePropertyMaps() {
		addDatatypePropertyMap("text", String.class, false, PROP_URI_TEXT);
		addDatatypePropertyMap("userId", Integer.class, false, PROP_URI_USER_ID);
		addDatatypePropertyMap("projectId", Integer.class, false, PROP_URI_PROJECT_ID);
		addDatatypePropertyMap("ontologyId", Integer.class, false, PROP_URI_ONTOLOGY_ID);
		addObjectPropertyMap("ratings", true, PROP_URI_HAS_RATING, new RatingDAO(metadataKb));
	}
}
