package org.ncbo.stanford.manager.metakb.protege;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metakb.SimpleObjectManager;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * Basic form of object manager.  You can ask for a DAO.
 * 
 * @author tony
 */
public abstract class SimpleObjectManagerImpl<BeanType extends AbstractIdBean> 
		extends AbstractOntologyManagerProtege
		implements SimpleObjectManager<BeanType> {
	
	protected abstract AbstractDAO<BeanType> getAbstractDAO();
	
	protected OWLModel getMetadataKb() {
		try {
			return getMetadataOWLModel();
		} catch (Exception e) {
			throw new BPRuntimeException("Could not initialize metadata Kb", e);
		}
	}
	
	// Implement interface
	public BeanType createInstance(BeanType bean) throws MetadataException {
		return getAbstractDAO().createInstance();
	}

	// Implement interface
	public void deleteInstance(Integer id) throws MetadataObjectNotFoundException {
		getAbstractDAO().deleteInstance(id);
	}

	// Implement interface
	public BeanType retrieveInstance(Integer id)
			throws MetadataObjectNotFoundException, Exception {
		return getAbstractDAO().retrieveInstance(id);
	}

	// Implement interface
	public void updateInstance(BeanType bean)
			throws MetadataObjectNotFoundException, MetadataException {
		getAbstractDAO().updateInstance(bean);
	}
}
