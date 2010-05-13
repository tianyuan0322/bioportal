package org.ncbo.stanford.service.metadata;

import java.util.Collection;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;

/**
 * A thin service layer that forwards all calls to subclasses of
 * {@link org.ncbo.stanford.manager.metakb.SimpleObjectManager}.
 * 
 * @author Tony Loeser
 * 
 * @param BeanType the type of object on which these CRUD operations act.
 */
public interface BeanCRUDService<BeanType extends AbstractIdBean> {

	/**
	 * Forwards to the equivalent manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.SimpleObjectManager#createObject()
	 */
	public BeanType createObject() throws MetadataException;
	
	/**
	 * Forwards to the equivalent manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.SimpleObjectManager#retrieveObject(Integer)
	 */
	public BeanType retrieveObject(Integer id) throws MetadataObjectNotFoundException;
	
	/**
	 * Forwards to the equivalent manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.SimpleObjectManager#retrieveAllObjects()
	 */
	public Collection<BeanType> retrieveAllObjects();
	
	/**
	 * Forwards to the equivalent manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.SimpleObjectManager#updateObject(AbstractIdBean)
	 */
	public void updateObject(BeanType bean) throws MetadataException, MetadataObjectNotFoundException;
	
	/**
	 * Forwards to the equivalent manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.SimpleObjectManager#deleteObject(Integer)
	 */
	public void deleteObject(Integer id) throws MetadataObjectNotFoundException;
}
