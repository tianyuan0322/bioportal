/**
 * 
 */
package org.ncbo.stanford.bean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Nick Griffith
 * 
 */
public class ConceptBean {

	private String id;
	private String displayLabel;
	private int ontologyId;
	private HashMap<String, String> properties;
	private List<ConceptBean> children;
	private List<ConceptBean> parents;

	
	/**
	 * Gets the first parent in the parents list.
	 * 
	 * @return the parent
	 */
	public ConceptBean getFirstParent() {
		ConceptBean parent = null;

		if (parents != null && parents.size() > 0) {
			parent = parents.get(0);
		}

		return parent;
	}

	/**
	 * @return the parent
	 */
	public List<ConceptBean> getParents() {
		return parents;
	}

	/**
	 * @param parents
	 *            the set of all parents
	 */
	public void setParents(List<ConceptBean> parents) {
		this.parents = parents;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the displayLabel
	 */
	public String getDisplayLabel() {
		return displayLabel;
	}

	/**
	 * @param displayLabel
	 *            the displayLabel to set
	 */
	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	/**
	 * @return the ontologyId
	 */
	public int getOntologyId() {
		return ontologyId;
	}

	/**
	 * @param ontologyId
	 *            the ontologyId to set
	 */
	public void setOntologyId(int ontologyId) {
		this.ontologyId = ontologyId;
	}

	/**
	 * @return the properties
	 */
	public HashMap<String, String> getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * @return the children
	 */
	public List<ConceptBean> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<ConceptBean> children) {
		this.children = children;
	}
}
