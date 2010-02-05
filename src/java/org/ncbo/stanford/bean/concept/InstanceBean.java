package org.ncbo.stanford.bean.concept;

import java.util.Collection;

public class InstanceBean extends AbstractConceptBean {

	// property to set instance types
	private Collection instanceTypes;

	public Collection getInstanceTypes() {
		return instanceTypes;
	}

	public void setInstanceTypes(Collection instanceTypes) {
		this.instanceTypes = instanceTypes;
	}

	public String toString() {
		return "Type: InstanceBean " + super.toString();
	}

	public String toString(String indent) {
		return "Type: InstanceBean " + super.toString(indent);
	}

}
