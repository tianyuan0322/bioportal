package org.ncbo.stanford.bean.notes;

import org.ncbo.stanford.enumeration.NoteAppliesToTypeEnum;

public class AppliesToBean {
	private String id;
	private String type;

	/**
	 * Creates a new AppliesToBean based on type. We check the type so that the
	 * correct properties are initialized.
	 * 
	 * @param id
	 * @param type
	 */
	public AppliesToBean(String id, NoteAppliesToTypeEnum appliesToType) {
		this.id = id;
		this.type = appliesToType.toString();
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

}
