package org.ncbo.stanford.bean;

import java.util.ArrayList;
import java.util.List;

public class OntologyViewBean extends OntologyBean {

	// views
	private List<Integer> viewOnOntologyVersionId = new ArrayList<Integer>(0);
	
	private String viewDefinition;
	private String viewDefinitionLanguage;
	private String viewGenerationEngine;
	
	public String toString() {
		final int max = 80;
		String viewDef = this.getViewDefinition();
		if (viewDef != null && viewDef.length()>max) {
			viewDef = viewDef.substring(0, max) + "...";
		}
		return "{Id: " + this.getId() + ", Ontology Id: "
				+ this.getOntologyId() + ", Remote: " + this.getIsRemote()
				+ ", Obo Foundry Id: " + this.getOboFoundryId()
				+ ", Internal Version Number: "
				+ this.getInternalVersionNumber() + ", User Id: "
				+ this.getUserId() + ", Version Number: "
				+ this.getVersionNumber() + ", Version Status: "
				+ this.getVersionStatus() + ", Display Label: "
				+ this.getDisplayLabel() + ", Description: "
				+ this.getDescription() + ", Abbreviation: "
				+ this.getAbbreviation() + ", Format: " + this.getFormat()
				+ ", Contact Name: " + this.getContactName()
				+ ", Contact Email: " + this.getContactEmail() + ", Foundry: "
				+ this.getIsFoundry() + " Coding Scheme: "
				+ this.getCodingScheme() + ", Target Terminologies: "
				+ this.getTargetTerminologies() + ", Synonym Slot: "
				+ this.getSynonymSlot() + ", Preferred Name Slot: "
				+ this.getPreferredNameSlot() + 
				", View Definition: " + viewDef + 
				", View Definition Language: " + this.getViewDefinitionLanguage() + 
				", View Generation Engine: " + this.getViewGenerationEngine() + 
				", View on Ontology Versions: " + this.getViewOnOntologyVersionId() + "}";
	}	
	
	/**
	 * @return the viewOnOntologyVersionId
	 */
	public List<Integer> getViewOnOntologyVersionId() {
		return viewOnOntologyVersionId;
	}
	/**
	 * @param viewOnOntologyVersionId the viewOnOntologyVersionId to set
	 */
	public void setViewOnOntologyVersionId(List<Integer> viewOnOntologyVersionId) {
		this.viewOnOntologyVersionId = viewOnOntologyVersionId;
	}
	/**
	 * @return the viewDefinition
	 */
	public String getViewDefinition() {
		return viewDefinition;
	}
	/**
	 * @param viewDefinition the viewDefinition to set
	 */
	public void setViewDefinition(String viewDefinition) {
		this.viewDefinition = viewDefinition;
	}
	/**
	 * @return the viewDefinitionLanguage
	 */
	public String getViewDefinitionLanguage() {
		return viewDefinitionLanguage;
	}
	/**
	 * @param viewDefinitionLanguage the viewDefinitionLanguage to set
	 */
	public void setViewDefinitionLanguage(String viewDefinitionLanguage) {
		this.viewDefinitionLanguage = viewDefinitionLanguage;
	}
	/**
	 * @return the viewGenerationEngine
	 */
	public String getViewGenerationEngine() {
		return viewGenerationEngine;
	}
	/**
	 * @param viewGenerationEngine the viewGenerationEngine to set
	 */
	public void setViewGenerationEngine(String viewGenerationEngine) {
		this.viewGenerationEngine = viewGenerationEngine;
	}
}
