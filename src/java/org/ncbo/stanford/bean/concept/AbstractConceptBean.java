package org.ncbo.stanford.bean.concept;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class that represents the base bean for any ontology concept (class,
 * property or instance)
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractConceptBean {
	protected String id;
	protected String label;
	protected HashMap<Object, Object> relations = new HashMap<Object, Object>();

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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	public String toString() {
		String str = "Id: " + id + " Label: " + label;
		str += "\nRelations:";

		for (Object key : relations.keySet()) {
			Object value = relations.get(key);

			str += "\nKEY: ";

			if (key instanceof AbstractConceptBean) {
				str += "  "+((AbstractConceptBean) key).toString();
			} else if (key instanceof String) {
				str += ((String) key).toString();
			} else {
				str += key.toString();
			}

			str += "\nVALUE: ";

			if (value instanceof AbstractConceptBean) {
				str += "  "+((AbstractConceptBean) value).toString();
			} else if (value instanceof String) {
				str += ((String) value).toString();
			} else {
				str += value.toString();
			}
		}

		return str;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public Object addRelation(Object arg0, Object arg1) {
		return relations.put(arg0, arg1);
	}

	/**
	 * @param m
	 * @see java.util.HashMap#putAll(java.util.Map)
	 */
	public void addRelations(Map<? extends Object, ? extends Object> m) {
		relations.putAll(m);
	}

	public HashMap<Object, Object> getRelations() {
		return relations;
	}
	
	
	
}
