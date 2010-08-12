package org.ncbo.stanford.bean.notes;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ProposalNewTermBean extends AbstractProposalBean {
	private String temporaryTermId;
	private String preferredName;
	private String definition;
	private List<String> synonyms;
	private List<String> parent;

	/**
	 * @return the definition
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * @param definition
	 *            the definition to set
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}

	/**
	 * @return the temporaryTermId
	 */
	public String getTemporaryTermId() {
		return temporaryTermId;
	}

	/**
	 * @param temporaryTermId
	 *            the temporaryTermId to set
	 */
	public void setTemporaryTermId(String id) {
		this.temporaryTermId = id;
	}

	/**
	 * @return the parent
	 */
	public List<String> getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(List<String> parent) {
		this.parent = parent;
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
	 * This method generates an HTML representation of the properties and is
	 * used in the sending of notifications.
	 */
	public String toHTML() {
		return "<p>Definition: " + this.definition + "</p>" + "<p>Parent: "
				+ StringUtils.join(this.parent, ", ") + "</p>"
				+ "<p>Reason for Change: " + this.reasonForChange + "</p>";
	}
}
