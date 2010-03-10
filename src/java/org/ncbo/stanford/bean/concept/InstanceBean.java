package org.ncbo.stanford.bean.concept;


public class InstanceBean extends AbstractConceptBean {

	// property to set instance types
	private InstanceTypesList instanceTypes;


	public InstanceTypesList getInstanceTypes() {
		return instanceTypes;
	}

	public void setInstanceTypes(InstanceTypesList instanceTypes) {
		this.instanceTypes = instanceTypes;
	}

	public String toString() {
		return "Type: InstanceBean " + super.toString();
	}

	public String toString(String indent) {
		return "Type: InstanceBean " + super.toString(indent);
	}

}
