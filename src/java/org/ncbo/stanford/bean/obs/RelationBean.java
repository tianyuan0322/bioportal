package org.ncbo.stanford.bean.obs;

public class RelationBean {

	private String conceptId;
	private ConceptBean concept;
	private int level;

	/**
	 * @return the conceptId
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * @param conceptId
	 *            the conceptId to set
	 */
	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * @return the concept
	 */
	public ConceptBean getConcept() {
		return concept;
	}

	/**
	 * @param concept
	 *            the concept to set
	 */
	public void setConcept(ConceptBean concept) {
		this.concept = concept;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	public String toString() {
		return "[conceptId: " + conceptId + " level: " + level + " concept: "
				+ concept + "]";
	}
}
