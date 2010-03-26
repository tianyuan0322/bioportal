package org.ncbo.stanford.bean.obs;

import java.util.ArrayList;
import java.util.List;

public class ConceptBean {

	protected String id;
	protected String localConceptId;
	private String localOntologyId;
	private Byte isTopLevel;
	private String fullId;
	private String preferredName;
	private List<String> synonyms = new ArrayList<String>(0);
	private List<String> definitions = new ArrayList<String>(0);
	private List<SemanticTypeBean> semanticTypes = new ArrayList<SemanticTypeBean>(
			0);

	public String toString() {
		return "[id: " + id + " localConceptId: " + localConceptId
				+ ", localOntologyId: " + localOntologyId + ", fullId: "
				+ fullId + ", isTopLevel: " + isTopLevel + ", preferredName: "
				+ preferredName + ", synonyms: " + synonyms + ", definitions: "
				+ definitions + ", semanticTypes: " + semanticTypes.toString()
				+ "]";
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
	 * @return the localConceptId
	 */
	public String getLocalConceptId() {
		return localConceptId;
	}

	/**
	 * @param localConceptId
	 *            the localConceptId to set
	 */
	public void setLocalConceptId(String localConceptId) {
		this.localConceptId = localConceptId;
	}

	/**
	 * @return the localOntologyId
	 */
	public String getLocalOntologyId() {
		return localOntologyId;
	}

	/**
	 * @param localOntologyId
	 *            the localOntologyId to set
	 */
	public void setLocalOntologyId(String localOntologyId) {
		this.localOntologyId = localOntologyId;
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
	 * @return the fullId
	 */
	public String getFullId() {
		return fullId;
	}

	/**
	 * @param fullId
	 *            the fullId to set
	 */
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}

	/**
	 * @return the preferredName
	 */
	public String getPreferredName() {
		return preferredName;
	}

	/**
	 * @param preferredName
	 *            the preferredName to set
	 */
	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
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
	 * @param definitions
	 *            the definitions to set
	 */
	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}

	/**
	 * @return the semanticTypes
	 */
	public List<SemanticTypeBean> getSemanticTypes() {
		return semanticTypes;
	}

	/**
	 * @param semanticTypes
	 *            the semanticTypes to set
	 */
	public void setSemanticTypes(List<SemanticTypeBean> semanticTypes) {
		this.semanticTypes = semanticTypes;
	}
}
