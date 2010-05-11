package org.ncbo.stanford.bean;

import java.util.Date;

import org.ncbo.stanford.bean.AbstractIdBean;

/**
 * Supertype of object beans that have dateCreated and dateModified properties
 * (in addition to the ID inherited from IdentifiedBean).
 * <p>
 * The created and modified dates are intended to correspond to when the bean
 * objects are saved to persistent store.
 * 
 * @author Tony Loeser
 */

public class TimedIdBean extends AbstractIdBean {

	Date dateCreated = null;  // Timestamp when the object was first saved to persistent store
	Date dateModified = null; // Timestamp when the object was most recently modified in persistent store
	
	public TimedIdBean(Integer id) {
		super(id);
	}
	
	// ============================================================
	// Standard getters/setters

	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public Date getDateModified() {
		return dateModified;
	}
	
	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
}
