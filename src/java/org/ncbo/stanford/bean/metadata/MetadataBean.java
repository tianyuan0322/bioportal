package org.ncbo.stanford.bean.metadata;

/**
 * Base class for java-side objects corresponding to Individuals in the metakb.
 * Provides an ID that corresponds to the id of the Individual in the kb.
 * 
 * The ID should only be set by the persistent store itself, not by any other 
 * client code.  We can't enforce this with the visibility of the related 
 * method(s) because the beans and manager code sit in different packages, and
 * it is the manager code that imports the beans.
 * 
 * @author Tony Loeser
 */
public class MetadataBean extends Object {

	private Integer id;
	
	/**
	 * Use this constructor to create a new object, before saving it into the
	 * persistent store.
	 */
	public MetadataBean() {
		id = null;
	}
	
	/**
	 * Constructor that specifies the id for the corresponding entity in the
	 * persistent store.  This method should only be used by the persistent
	 * store itself.
	 */
	public MetadataBean(Integer id) {
		this.id = id;
	}

	// =========================================================================
	// Standard accessors

	public Integer getId() {
		return id;
	}
	
	/**
	 * Standard setter for id.  Note that it is unsupported to set the value
	 * of the id once it has already been set.
	 */
	public void setId(Integer id) {
		if (this.id == null) {
			this.id = id;
		} else {
			String msg = "Attempt to re-set a MetadataObjectBean id from "+
			             this.id+" to "+id+".";
			throw new UnsupportedOperationException(msg);
		}
	}
}
