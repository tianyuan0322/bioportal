package org.ncbo.stanford.bean.metadata;

import java.util.Date;

/**
 * Supertype of object beans that have dateCreated and dateModified properties
 * (in addition to the ID inherited from IdentifiedBean).
 * <p>
 * The created and modified dates are intended to correspond to when the bean
 * objects are saved to persistent store.
 * 
 * @author Tony Loeser
 */

public class TimedIdBean extends MetadataBean {

	// Timestamp when the object was first saved to persistent store
	private Date dateCreated = null;
	// Timestamp when the object was most recently modified in persistent store
	private Date dateModified = null;
	
	public TimedIdBean() {
		super();
	}
	
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
