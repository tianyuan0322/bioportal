package org.ncbo.stanford.bean.obs;

import java.util.ArrayList;
import java.util.List;

public class ConceptBean extends AbstractConceptBean {

	private String localOntologyId;
	private Byte isTopLevel;

	private String preferredName;
	private List<String> synonyms = new ArrayList<String>(0);
	private List<SemanticTypeBean> semanticTypes = new ArrayList<SemanticTypeBean>(
			0);

	public String toString() {
		return "[" + super.toString() + ", localOntologyId: "
				+ this.getLocalOntologyId() + ", isTopLevel: "
				+ this.getIsTopLevel() + ", preferredName: "
				+ this.getPreferredName() + ", synonyms: " + this.getSynonyms()
				+ ", semanticTypes: " + this.getSemanticTypes().toString()
				+ "]";
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

	public boolean isTopLevel() {
		if (this.getIsTopLevel().equals(true)) {
			return true;
		}

		return false;
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
}
