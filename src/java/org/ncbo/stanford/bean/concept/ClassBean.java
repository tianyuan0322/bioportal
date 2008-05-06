package org.ncbo.stanford.bean.concept;

public class ClassBean extends AbstractConceptBean {

	boolean lightWeight; //True means that the concept bean is not completely populated.  
	               //When a path to root from a concept is populated, we may not be interested in all
	               // the details of every concept in the path. Getting details of every concept is a
	               // performance hit.
	public String toString() {
		return "Type: ClassBean  isLightWeight="+lightWeight+"  " + super.toString();
	}
	
	public String toString(String indent) {
		return "Type: ClassBean  isLightWeight="+lightWeight+"  "  + super.toString(indent);
	}
	/**
	 * @return the light
	 */
	public boolean isLightWeight() {
		return lightWeight;
	}
	/**
	 * @param light the light to set
	 */
	public void setLightWeight(boolean lightWeight) {
		this.lightWeight = lightWeight;
	}
}
