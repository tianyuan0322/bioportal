package org.ncbo.stanford.bean.notes;

import java.util.List;

public class ProposalNewRelationshipBean extends AbstractProposalBean {
	private String relationshipType;
	private List<String> relationshipTarget;
	private List<String> oldRelationshipTarget;

	/**
	 * @return the relationshipType
	 */
	public String getRelationshipType() {
		return relationshipType;
	}

	/**
	 * @param relationshipType
	 *            the relationshipType to set
	 */
	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	/**
	 * @return the relationshipTarget
	 */
	public List<String> getRelationshipTarget() {
		return relationshipTarget;
	}

	/**
	 * @param relationshipTarget
	 *            the relationshipTarget to set
	 */
	public void setRelationshipTarget(List<String> relationshipTarget) {
		this.relationshipTarget = relationshipTarget;
	}

	/**
	 * @return the oldRelationshipTarget
	 */
	public List<String> getOldRelationshipTarget() {
		return oldRelationshipTarget;
	}

	/**
	 * @param oldRelationshipTarget
	 *            the oldRelationshipTarget to set
	 */
	public void setOldRelationshipTarget(List<String> oldRelationshipTarget) {
		this.oldRelationshipTarget = oldRelationshipTarget;
	}
	
	public String toHTML() {
		return "<p>Relationship Type: " + this.relationshipType + "</p>" + 
			"<p>Relationship Target: " + this.relationshipTarget + "</p>" +
			"<p>Old Relationship Target: " + this.oldRelationshipTarget + "</p>" +
			"<p>Reason for Change: " + this.reasonForChange + "</p>";
	}

}
