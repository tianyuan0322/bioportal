package org.ncbo.stanford.bean.metadata;

import org.ncbo.stanford.bean.metadata.TimedIdBean;

/**
 * Project objects describe the users' projects.
 * People working on ontologies.
 * 
 * @author Tony Loeser
 */
public class ProjectBean extends TimedIdBean {
	
	private String description = null;
	private String institution = null;
	private String homePage = null;
	private String name = null;
	private String people = null;
	private Integer userId = null;
	
	public ProjectBean() {
		super();
	}
	
	public ProjectBean(Integer id) {
		super(id);
	}
	
	@Override
	public String toString() {
		return "ProjectBean(id: "+getId()+")";
	}
	
	// Boilerplate getters and setters
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeople() {
		return people;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
