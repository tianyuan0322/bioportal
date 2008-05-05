package org.ncbo.stanford.bean.concept;

public class ClassBean extends AbstractConceptBean {

	boolean light; //True means that the concept bean is not completely populated.  
	               //When a path to root from a concept is populated, we may not be interested in all
	               // the details of every concept in the path. Getting details of every concept is a
	               // performance hit.
	public String toString() {
		return "Type: ClassBean " + super.toString();
	}
	/**
	 * @return the light
	 */
	public boolean isLight() {
		return light;
	}
	/**
	 * @param light the light to set
	 */
	public void setLight(boolean light) {
		this.light = light;
	}
}
