package org.ncbo.stanford.manager.metakb.protege.base.DAO;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDALayer;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDAO;

/**
 * XXX Fill in.
 * <p>
 * Implementing classes must provide a public, zero-argument constructor that calls this classes 
 * constructor.
 * 
 * @author Tony Loeser
 *
 * @param <BeanType>
 */
public abstract class ReadOnlyDAO<BeanType extends MetadataBean>
		extends AbstractDAO<BeanType> {

	protected ReadOnlyDAO(AbstractDALayer daLayer, String qualifiedClassName) {
		super(daLayer, qualifiedClassName);
	}
	
	// ============================================================
	// Eliminate write methods
	
	@Override
	public void saveObject(BeanType bean)
			throws MetadataObjectNotFoundException, MetadataException {
		String msg = this.getClass().getSimpleName() + " is a read-only DAO.";
		throw new UnsupportedOperationException(msg);
	}

	@Override
	public void deleteObject(Integer id) {
		String msg = this.getClass().getSimpleName() + " is a read-only DAO.";
		throw new UnsupportedOperationException(msg);
	}

	
	// ============================================================
	// Fix instance name generation
	
	// The problem is that AbstractDAO usually looks in the Metadata KB
	// for the instances.  In this case, the instances are static, in the 
	// Metadata Ontology itself.  (The Metadata KB imports the Metadata 
	// Ontology, and they have different namespaces.)
	@Override
	protected String convertIdToName(Integer id) {
		return qualifiedClassName + '_' + id;
	}	

}
