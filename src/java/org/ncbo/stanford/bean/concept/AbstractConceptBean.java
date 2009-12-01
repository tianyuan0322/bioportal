package org.ncbo.stanford.bean.concept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.enumeration.ConceptTypeEnum;

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
	protected ConceptTypeEnum type;
	protected List<String> synonyms;
	protected List<String> definitions;
	protected List<String> authors;
	protected Byte isObsolete = null;

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
	 * @return the synonyms
	 */
	public List<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * @param synonyms
	 *            the synonyms to set
	 */
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	/**
	 * @return the definitions
	 */
	public List<String> getDefinitions() {
		return definitions;
	}

	/**
	 * @return the authors
	 */
	public List<String> getAuthors() {
		return authors;
	}

	/**
	 * @param definitions
	 *            the definitions to set
	 */
	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}

	/**
	 * @param authors
	 *            the authors to set
	 */
	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addSynonym(String arg0) {
		if (synonyms == null) {
			synonyms = new ArrayList<String>(1);
		}

		return synonyms.add(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addDefinition(String arg0) {
		if (definitions == null) {
			definitions = new ArrayList<String>(1);
		}

		return definitions.add(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addAuthor(String arg0) {
		if (authors == null) {
			authors = new ArrayList<String>(1);
		}

		return authors.add(arg0);
	}

	/**
	 * @return the isObsolete
	 */
	public Byte getIsObsolete() {
		return isObsolete;
	}

	/**
	 * @param isObsolete
	 *            the isObsolete to set
	 */
	public void setIsObsolete(Byte isObsolete) {
		this.isObsolete = isObsolete;
	}

	/**
	 * @return the type
	 */
	public ConceptTypeEnum getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ConceptTypeEnum type) {
		this.type = type;
	}
}
