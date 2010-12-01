package org.ncbo.stanford.manager.metakb.protege.impl;

import java.util.Collection;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.SimpleObjectManager;

/**
 * Basic form of object manager.  You can ask for a DAO.
 * 
 * @author Tony Loeser
 */
public abstract class SimpleObjectManagerImpl<BeanType extends MetadataBean> 
		extends BaseProtegeMetadataManager
		implements SimpleObjectManager<BeanType> {
	
	private final Class<BeanType> beanType;
	
	protected SimpleObjectManagerImpl(Class<BeanType> beanType) {
		this.beanType = beanType;
	}
		
	@Override
	public void saveObject(BeanType bean)
			throws MetadataObjectNotFoundException, MetadataException {
		getDALayer().saveObject(bean);
	}

	@Override
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException {
		getDALayer().deleteObject(beanType, id);
	}

	@Override
	public BeanType retrieveObject(Integer id) throws MetadataObjectNotFoundException {
		return getDALayer().retrieveObject(beanType, id);
	}
	
	@Override
	public Collection<BeanType> retrieveAllObjects() {
		return getDALayer().getAllObjects(beanType);
	}
}
