package org.ncbo.stanford.bean;

/**
 * 
 * @author Csongor Nyulas
 */
public class GroupBean {

	private Integer id;
	private String name;
	private String acronym;
	private String description;
	private String urlHomepage;

	public GroupBean() {
		id = null;
		name = null;
		acronym = null;
		description = null;
		urlHomepage = null;
	}
	
	public GroupBean(int id, String name, String acronym) {
		this.id = id;
		this.name = name;
		this.acronym = acronym;
	}

	public GroupBean(int id, String name, String acronym, String description, String urlHomepage) {
		this (id, name, acronym);
		this.description = description;
		this.urlHomepage = urlHomepage;
	}
	
	@Override
	public String toString() {
		return "GroupBean( id: " + id + "; name: " + name + 
		"; acronym: " + acronym + "; description: " + description + 
		"; urlHomepage: " + urlHomepage + ")";
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the acronym
	 */
	public String getAcronym() {
		return acronym;
	}

	/**
	 * @param acronym the acronym to set
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the urlHomepage
	 */
	public String getUrlHomepage() {
		return urlHomepage;
	}

	/**
	 * @param url the url to the groups homepage to be set
	 */
	public void setUrlHomepage(String url) {
		this.urlHomepage = url;
	}
	
}