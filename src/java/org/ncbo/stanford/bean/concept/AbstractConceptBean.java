package org.ncbo.stanford.bean.concept;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * Abstract class that represents the base bean for any ontology concept (class,
 * property or instance)
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractConceptBean {
	protected String ontologyVersionId;
	protected String id;
	protected String fullId;
	protected String label;
	protected Byte isTopLevel;
	protected Byte isBrowsable = new Byte(ApplicationConstants.TRUE);
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
	 * @return the full id
	 */
	public String getFullId() {
		return fullId;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setFullId(String fullId) {
		this.fullId = fullId;
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

	@SuppressWarnings("unchecked")
	public String toString(String indent) {
		String spacer = "   ";
		String str = "Id: " + id + " FullId: " + fullId + " Label: " + label
				+ " OntologyVersionId: " + ontologyVersionId + "\n";
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

	@SuppressWarnings("unchecked")
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

	public Object getRelation(Object key) {
		return relations.get(key);
	}

	public Object removeRelation(Object key) {
		return relations.remove(key);
	}

	/**
	 * @return the ontologyVersionId
	 */
	public String getOntologyVersionId() {
		return ontologyVersionId;
	}

	/**
	 * @param ontologyVersionId
	 *            the ontologyVersionId to set
	 */
	public void setOntologyVersionId(String ontologyVersionId) {
		this.ontologyVersionId = ontologyVersionId;
	}

	/**
	 * @return the isTopLevel
	 */
	public Byte getIsTopLevel() {
		return isTopLevel;
	}

	/**
	 * @param isTopLevel
	 *            the isTopLevel to set
	 */
	public void setIsTopLevel(Byte isTopLevel) {
		this.isTopLevel = isTopLevel;
	}

	/**
	 * @return the isBrowsable
	 */
	public Byte getIsBrowsable() {
		return isBrowsable;
	}

	/**
	 * @param isBrowsable
	 *            the isBrowsable to set
	 */
	public void setIsBrowsable(Byte isBrowsable) {
		this.isBrowsable = isBrowsable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fullId == null) ? 0 : fullId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((isBrowsable == null) ? 0 : isBrowsable.hashCode());
		result = prime * result
				+ ((isTopLevel == null) ? 0 : isTopLevel.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime
				* result
				+ ((ontologyVersionId == null) ? 0 : ontologyVersionId
						.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractConceptBean other = (AbstractConceptBean) obj;
		if (fullId == null) {
			if (other.fullId != null)
				return false;
		} else if (!fullId.equals(other.fullId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isBrowsable == null) {
			if (other.isBrowsable != null)
				return false;
		} else if (!isBrowsable.equals(other.isBrowsable))
			return false;
		if (isTopLevel == null) {
			if (other.isTopLevel != null)
				return false;
		} else if (!isTopLevel.equals(other.isTopLevel))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (ontologyVersionId == null) {
			if (other.ontologyVersionId != null)
				return false;
		} else if (!ontologyVersionId.equals(other.ontologyVersionId))
			return false;
		return true;
	}
}
