package org.ncbo.stanford.bean.notes;

import java.util.List;

public class ProposalNewTermBean extends AbstractProposalBean {
	private String definition;
	private String id;
	private List<String> parent;
	private String preferredName;
	private List<String> synonym;

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
	 * @return the synonym
	 */
	public List<String> getSynonym() {
		return synonym;
	}

	/**
	 * @param synonym
	 *            the synonym to set
	 */
	public void setSynonym(List<String> synonym) {
		this.synonym = synonym;
	}

}
