package org.ncbo.stanford.bean.concept;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
		return toString("");
	}

	public String toString(String indent) {
		String spacer = "   ";
		String str = "Id: " + id + " Label: " + label + "\n";
		str += indent + "Relations:\n";

		for (Object key : relations.keySet()) {
			Object value = relations.get(key);

			str += indent + "KEY: ";

			if (key instanceof AbstractConceptBean) {
				str += ((AbstractConceptBean) key).toString(spacer);
			} else {
				str += key;
			}

			str += "  VALUE: ";

			if (value instanceof AbstractConceptBean) {
				str += ((AbstractConceptBean) value).toString(spacer);
			} else if (value instanceof List) {
				str += toString((List) value, indent + spacer);
			} else {
				str += value;
			}
			str += "\n";
		}

		return str;
	}

	public String toString(List list, String indent) {
		StringBuffer buf = new StringBuffer();
		buf.append("[");

		Iterator i = list.iterator();
		boolean hasNext = i.hasNext();
		while (hasNext) {
			Object o = i.next();
			if (o instanceof AbstractConceptBean) {
				AbstractConceptBean acb = (AbstractConceptBean) o;
				buf.append(acb.toString(indent));
			} else {
				buf.append(String.valueOf(o));
			}

			hasNext = i.hasNext();
			if (hasNext)
				buf.append(", ");
		}

		buf.append(indent).append("]");
		return buf.toString();

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

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof AbstractConceptBean))
			return false;
		AbstractConceptBean t = (AbstractConceptBean) o;
		if (id.equals(t.id) && label.equals(t.label)
				&& relations.equals(t.relations))
			return true;
		return false;
	}

}
