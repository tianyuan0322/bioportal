package org.ncbo.stanford.service.metadata.impl;

import java.util.Collection;

import org.ncbo.stanford.bean.metadata.MetadataBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metakb.SimpleObjectManager;
import org.ncbo.stanford.service.metadata.BeanCRUDService;

/**
 * Implementation of {@link BeanCRUDService}.
 * 
 * @author Tony Loeser
 */
public abstract class BeanCRUDServiceImpl<BeanType extends MetadataBean>
		implements BeanCRUDService<BeanType> {

	protected abstract SimpleObjectManager<BeanType> getObjectManager();
	
	public abstract BeanType newBean();

	@Override
	public void saveObject(BeanType bean)
			throws MetadataObjectNotFoundException, MetadataException {
		getObjectManager().saveObject(bean);
	}

	@Override
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException {
		getObjectManager().deleteObject(id);
	}

	@Override
	public BeanType retrieveObject(Integer id) throws MetadataObjectNotFoundException {
		return getObjectManager().retrieveObject(id);
	}

	@Override
	public Collection<BeanType> retrieveAllObjects() {
		return getObjectManager().retrieveAllObjects();
	}
}
