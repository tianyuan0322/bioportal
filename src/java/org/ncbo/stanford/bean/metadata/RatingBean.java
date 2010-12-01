package org.ncbo.stanford.bean.metadata;

/**
 * Ratings are an aspect of reviews; a numerical score along a named axis
 * describing the quality of an ontology.  See {@link ReviewBean}.
 * 
 * @author Tony Loeser
 */
public class RatingBean extends TimedIdBean {

	private RatingTypeBean type = null;
	private Integer value = null;
	
	public RatingBean() {
		super();
	}
	
	public RatingBean(Integer id) {
		super(id);
	}
	
	// ============================================================
	// Standard getters/setters

	public RatingTypeBean getType() {
		return type;
	}
	public void setType(RatingTypeBean type) {
		this.type = type;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}	
}
