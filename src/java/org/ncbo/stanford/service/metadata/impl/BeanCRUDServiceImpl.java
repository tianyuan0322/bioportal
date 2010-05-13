package org.ncbo.stanford.service.metadata.impl;

import java.util.Collection;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.SimpleObjectManager;
import org.ncbo.stanford.service.metadata.BeanCRUDService;

/**
 * Implementation of {@link BeanCRUDService}.
 * 
 * @author Tony Loeser
 */
public abstract class BeanCRUDServiceImpl<BeanType extends AbstractIdBean>
		implements BeanCRUDService<BeanType> {

	protected abstract SimpleObjectManager<BeanType> getObjectManager();

	// Implement BeanCRUDService#create
	public BeanType createObject() throws MetadataException {
		return getObjectManager().createObject();
	}

	// Implement BeanCRUDService#delete(Integer)
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException {
		getObjectManager().deleteObject(id);
	}

	// Implement BeanCRUDService#retrieve
	public BeanType retrieveObject(Integer id) throws MetadataObjectNotFoundException {
		return getObjectManager().retrieveObject(id);
	}

	// Implement BeanCRUDService#retrieveAll
	public Collection<BeanType> retrieveAllObjects() {
		return getObjectManager().retrieveAllObjects();
	}

	// Implement BeanCRUDService#update
	public void updateObject(BeanType bean) 
			throws MetadataException, MetadataObjectNotFoundException {
		getObjectManager().updateObject(bean);
	}
}
