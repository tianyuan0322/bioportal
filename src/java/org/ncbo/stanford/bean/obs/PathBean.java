package org.ncbo.stanford.bean.obs;

import java.util.ArrayList;
import java.util.List;

public class PathBean {

	private List<ConceptBean> conceptBeans = new ArrayList<ConceptBean>(0);

	/**
	 * @return the conceptBeans
	 */
	public List<ConceptBean> getConceptBeans() {
		return conceptBeans;
	}

	/**
	 * @param paths
	 *            the paths to set
	 */
	public void setConceptBeans(List<ConceptBean> conceptBeans) {
		this.conceptBeans = conceptBeans;
	}

	public String toString() {
		return "[" + super.toString() + " conceptBeans: " + conceptBeans + "]";
	}
}
