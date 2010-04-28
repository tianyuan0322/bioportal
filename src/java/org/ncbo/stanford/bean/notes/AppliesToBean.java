package org.ncbo.stanford.bean.notes;

import org.ncbo.stanford.enumeration.NoteAppliesToTypeEnum;

public class AppliesToBean {
	private String noteId;
	private String ontologyId;
	private String fullId;
	private String type;

	/**
	 * Creates a new AppliesToBean based on type. We check the type so that the
	 * correct properties are initialized.
	 * 
	 * @param id
	 * @param type
	 */
	public AppliesToBean(String id, NoteAppliesToTypeEnum appliesToType) {
		switch (appliesToType) {
		case Ontology:
			this.ontologyId = id;
			break;
		case Class:
			this.fullId = id;
			break;
		case Property:
			this.fullId = id;
			break;
		case Individual:
			this.fullId = id;
			break;
		case Note:
			this.noteId = id;
			break;
		default:
			this.ontologyId = id;
			break;
		}

		this.type = appliesToType.toString();
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the noteId
	 */
	public String getNoteId() {
		return noteId;
	}

	/**
	 * @param noteId
	 *            the noteId to set
	 */
	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	/**
	 * @return the ontologyId
	 */
	public String getOntologyId() {
		return ontologyId;
	}

	/**
	 * @param ontologyId
	 *            the ontologyId to set
	 */
	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}
}
