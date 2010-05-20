package org.ncbo.stanford.manager.metakb.protege.DAO;

import org.ncbo.stanford.bean.RatingTypeBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * Data access object for accessing {@link RatingTypeBean} objects in the metadata KB.
 * Note that this is a read-only DAO, since the rating types are static (at least, 
 * defined in the KB rather than through java code).
 * 
 * @author Tony Loeser
 */
public class RatingTypeDAO extends AbstractDAO<RatingTypeBean> {

	private static String OWL_CLASS_NAME = "metadata:RatingType";
	
	public RatingTypeDAO(OWLModel metadataKb) {
		super(OWL_CLASS_NAME, metadataKb);
	}
	
	// ============================================================
	// Properties

	public static final String PROP_URI_NAME = PREFIX_OMV + "name";

	// Override
	protected void initializePropertyMaps() {
		addDatatypePropertyMap("name", String.class, false, PROP_URI_NAME);
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
