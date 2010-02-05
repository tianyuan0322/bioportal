package org.ncbo.stanford.bean.concept;

import java.util.ArrayList;
import java.util.List;

public class ClassBean extends AbstractConceptBean {

	public static final String SUB_CLASS_PROPERTY = "SubClass";
	public static final String SUPER_CLASS_PROPERTY = "SuperClass";
	public static final String CHILD_COUNT_PROPERTY = "ChildCount";

	// properties to set instances and its count.
	List<InstanceBean> instances = new ArrayList<InstanceBean>();
	private Integer instanceCount;

	public List<InstanceBean> getInstances() {
		return instances;
	}

	public void setInstances(List<InstanceBean> instances) {
		this.instances = instances;
	}

	public Integer getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(Integer instanceCount) {
		this.instanceCount = instanceCount;
	}

	public String toString() {
		return "Type: ClassBean " + super.toString();
	}

	public String toString(String indent) {
		return "Type: ClassBean " + super.toString(indent);
	}
}
