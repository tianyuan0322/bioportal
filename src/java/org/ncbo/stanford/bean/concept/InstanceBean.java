package org.ncbo.stanford.bean.concept;


public class InstanceBean extends AbstractConceptBean {

	// property to set instance types
	private InstanceTypesList instanceType;



	public InstanceTypesList getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(InstanceTypesList instanceType) {
		this.instanceType = instanceType;
	}

	public String toString() {
		return "Type: InstanceBean " + super.toString();
	}

	public String toString(String indent) {
		return "Type: InstanceBean " + super.toString(indent);
	}

}
