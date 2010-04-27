package org.ncbo.stanford.manager.metakb;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

public interface SimpleObjectManager<BeanType extends AbstractIdBean> {

	public BeanType createInstance() throws MetadataException;
	
	public BeanType retrieveInstance(Integer id) throws MetadataObjectNotFoundException, Exception;
	
	public void updateInstance(BeanType bean) throws MetadataObjectNotFoundException, MetadataException;
	
	public void deleteInstance(Integer id) throws MetadataObjectNotFoundException;
}
