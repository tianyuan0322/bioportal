package org.ncbo.stanford.domain.generated;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * AbstractNcboUser entity provides the base persistence definition of the
 * NcboUser entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboUser implements java.io.Serializable {

	// Fields

	private Integer id;
	private String username;
	private String apiKey;
	private String openId;
	private String password;
	private String email;
	private String firstname;
	private String lastname;
	private String phone;
	private Timestamp dateCreated;
	private Set ncboUserRoles = new HashSet(0);
	private Set ncboOntologyAcls = new HashSet(0);

	// Constructors

	/** default constructor */
	public AbstractNcboUser() {
	}

	/** minimal constructor */
	public AbstractNcboUser(String username, String apiKey, String password,
			String email, String firstname, String lastname,
			Timestamp dateCreated) {
		this.username = username;
		this.apiKey = apiKey;
		this.password = password;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public AbstractNcboUser(String username, String apiKey, String openId,
			String password, String email, String firstname, String lastname,
			String phone, Timestamp dateCreated, Set ncboUserRoles,
			Set ncboOntologyAcls) {
		this.username = username;
		this.apiKey = apiKey;
		this.openId = openId;
		this.password = password;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.phone = phone;
		this.dateCreated = dateCreated;
		this.ncboUserRoles = ncboUserRoles;
		this.ncboOntologyAcls = ncboOntologyAcls;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getApiKey() {
		return this.apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getOpenId() {
		return this.openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Timestamp getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Set getNcboUserRoles() {
		return this.ncboUserRoles;
	}

	public void setNcboUserRoles(Set ncboUserRoles) {
		this.ncboUserRoles = ncboUserRoles;
	}

	public Set getNcboOntologyAcls() {
		return this.ncboOntologyAcls;
	}

	public void setNcboOntologyAcls(Set ncboOntologyAcls) {
		this.ncboOntologyAcls = ncboOntologyAcls;
	}

}