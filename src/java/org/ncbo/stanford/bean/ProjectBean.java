package org.ncbo.stanford.bean;

public class ProjectBean {
	
	private Integer id;
	private String description;
	private String institution;
	private String homePage;
	private String name;
	private String people;
	private Integer userId;
	
	public ProjectBean() {
		id = null;
		description = null;
	}
	
	@Override
	public String toString() {
		return "ProjectBean(id: "+id+")";
	}
	
	// Boilerplate getters and setters
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
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
