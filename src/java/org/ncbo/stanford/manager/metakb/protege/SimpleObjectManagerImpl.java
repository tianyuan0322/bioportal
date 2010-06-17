package org.ncbo.stanford.manager.metakb.protege;

import java.util.Collection;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.SimpleObjectManager;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;

/**
 * Basic form of object manager.  You can ask for a DAO.
 * 
 * @author Tony Loeser
 */
public abstract class SimpleObjectManagerImpl<BeanType extends AbstractIdBean> 
		extends BaseProtegeMetadataManager
		implements SimpleObjectManager<BeanType> {
	
	private final Class<? extends AbstractDAO<BeanType>> daoType;
	
	protected SimpleObjectManagerImpl(Class<? extends AbstractDAO<BeanType>> daoType) {
		this.daoType = daoType;
	}
	
	@Override
	public BeanType createObject() throws MetadataException {
		return getDAO(daoType).createObject();
	}

	@Override
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException {
		getDAO(daoType).deleteObject(id);
	}

	@Override
	public BeanType retrieveObject(Integer id) throws MetadataObjectNotFoundException {
		return getDAO(daoType).retreiveObject(id);
	}
	
	@Override
	public Collection<BeanType> retrieveAllObjects() {
		return getDAO(daoType).getAllObjects();
	}

	@Override
	public void updateObject(BeanType bean)
			throws MetadataObjectNotFoundException, MetadataException {
		getDAO(daoType).updateObject(bean);
	}
}
