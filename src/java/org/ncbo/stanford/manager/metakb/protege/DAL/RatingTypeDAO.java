package org.ncbo.stanford.manager.metakb.protege.DAL;

import org.ncbo.stanford.bean.metadata.RatingTypeBean;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDALayer;
import org.ncbo.stanford.manager.metakb.protege.base.DAO.ReadOnlyDAO;
import org.ncbo.stanford.manager.metakb.protege.base.prop.DatatypePropertyMap;

/**
 * Data access object for accessing {@link RatingTypeBean} objects in the metadata KB.
 * Note that this is a read-only DAO, since the rating types are static (at least, 
 * defined in the KB rather than through java code).
 * 
 * @author Tony Loeser
 */
public class RatingTypeDAO extends ReadOnlyDAO<RatingTypeBean> {

	private static String OWL_CLASS_NAME = PREFIX_METADATA + "RatingType";
	
	public RatingTypeDAO(AbstractDALayer daLayer) {
		super(daLayer, OWL_CLASS_NAME);
	}

	
	// ============================================================
	// Property maps

	public static final String PROP_URI_NAME = PREFIX_OMV + "name";

	// Override
	protected void initializePropertyMaps() {
		addMap(new DatatypePropertyMap("name", String.class, SINGLE_VALUE,
									   PROP_URI_NAME));
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
