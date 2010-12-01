package org.ncbo.stanford.bean.metadata;


/**
 * Rating types are the axes along which ratings score ontologies.
 * See {@link RatingBean}.
 * 
 * @author Tony Loeser
 */
public class RatingTypeBean extends MetadataBean {

	String name = null;
	
	public RatingTypeBean(Integer id) {
		super(id);
	}

	// ============================================================
	// Standard getters/setters
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
