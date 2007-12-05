/**
 * 
 */
package org.ncbo.stanford.bean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Nick Griffith
 *
 */
public class ConceptBean {
	
	private String id;
	private String displayLabel;
	private int ontologyId;
	private HashMap<String, String> properties;
	private ArrayList<ConceptBean> children;
	private ConceptBean parent;
	
	
	
	
	/**
	 * @return the children
	 */
	public ArrayList<ConceptBean> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(ArrayList<ConceptBean> children) {
		this.children = children;
	}
	/**
	 * @return the parent
	 */
	public ConceptBean getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(ConceptBean parent) {
		this.parent = parent;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
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
	 * @param displayLabel the displayLabel to set
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
	 * @param ontologyId the ontologyId to set
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
	 * @param properties the properties to set
	 */
	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}
	
	
	
	
}
