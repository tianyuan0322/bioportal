package org.ncbo.stanford.bean.concept;

public class ClassBean extends AbstractConceptBean {

	public static final String SUB_CLASS_PROPERTY = "SubClass";
	public static final String SUPER_CLASS_PROPERTY = "SuperClass";
	public static final String CHILD_COUNT_PROPERTY = "ChildCount";
	  
	public String toString() {
		return "Type: ClassBean " + super.toString();
	}

	public String toString(String indent) {
		return "Type: ClassBean " + super.toString(indent);
	}
}
