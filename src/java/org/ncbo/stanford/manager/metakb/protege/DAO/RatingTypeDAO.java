package org.ncbo.stanford.manager.metakb.protege.DAO;

import org.ncbo.stanford.bean.RatingTypeBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.PropertyMapSet;

/**
 * Data access object for accessing {@link RatingTypeBean} objects in the metadata KB.
 * Note that this is a read-only DAO, since the rating types are static (at least, 
 * defined in the KB rather than through java code).
 * 
 * @author Tony Loeser
 */
public class RatingTypeDAO extends AbstractDAO<RatingTypeBean> {

	private static String OWL_CLASS_NAME = "metadata:RatingType";
	
	@Override
	protected String getQualifiedClassName() {
		return OWL_CLASS_NAME;
	}

	
	// ============================================================
	// Property maps

	public static final String PROP_URI_NAME = PREFIX_OMV + "name";

	// Override
	protected void initializePropertyMaps(PropertyMapSet maps) {
		maps.addDatatypePropertyMap("name", String.class, SINGLE_VALUE, PROP_URI_NAME);
	}

	
	// ============================================================
	// Eliminate write methods
	
	// Override
	public Integer createObject(RatingTypeBean bean) throws MetadataException {
		throw new UnsupportedOperationException("RatingTypeDAO is a read-only DAO.");
	}

	// Override
	public void deleteObject(Integer id) {
		throw new UnsupportedOperationException("RatingTypeDAO is a read-only DAO.");
	}

	// Override
	public void updateObject(RatingTypeBean bean) {
		throw new UnsupportedOperationException("RatingTypeDAO is a read-only DAO.");
	}

	
	// ============================================================
	// Fix instance name generation
	
	// Override AbstractDAO
	// The problem is that AbstractDAO usually looks in the Metadata KB itself for the
	// instances.  In this case, the instances are static, in the Metadata Ontology 
	// itself.  
	protected String convertIdToName(Integer id) {
		return OWL_CLASS_NAME + '_' + id;
	}	
}
