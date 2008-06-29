package org.ncbo.stanford.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserRole;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.helper.StringHelper;

public class UserBean {

	private Integer id;
	private String username;
	private String password;
	private String email;
	private String firstname;
	private String lastname;
	private String phone;
	private Date dateCreated;
	private List<String> roles;

	public String toString() {
		return "{Id: " + this.getId() + ", Username: " + this.getUsername()
				+ ", Password: " + this.getPassword() + ", Email: "
				+ this.getEmail() + ", Firstname: " + this.getFirstname()
				+ ", Lastname: " + this.getLastname() + ", Phone: "
				+ this.getPhone() + "}";
	}

	/**
	 * Populates the OntologyBean with data from a NcboOntology
	 * 
	 * @param ncboOntology
	 */
	public void populateFromEntity(NcboUser ncboUser) {
		if (ncboUser != null) {
			this.setId(ncboUser.getId());
			this.setUsername(ncboUser.getUsername());
			this.setPassword(ncboUser.getPassword());
			this.setEmail(ncboUser.getEmail());
			this.setFirstname(ncboUser.getFirstname());
			this.setLastname(ncboUser.getLastname());
			this.setPhone(ncboUser.getPhone());
			this.setDateCreated(ncboUser.getDateCreated());

			List<String> roles = new ArrayList<String>(1);

			for (Object role : ncboUser.getNcboUserRoles()) {
				roles.add(((NcboUserRole) role).getNcboLRole().getName());
			}

			this.setRoles(roles);
		}
	}

	/**
	 * Populates the OntologyBean with data from a NcboOntology
	 * 
	 * @param ncboOntology
	 */
	public void populateToEntity(NcboUser ncboUser) {
		if (ncboUser != null) {
			ncboUser.setId(this.getId());
			ncboUser.setUsername(this.getUsername());
			
			if (!StringHelper.isNullOrNullString(this.getPassword())) {
				ncboUser.setPassword(this.getPassword());
			}
			
			ncboUser.setEmail(this.getEmail());
			ncboUser.setFirstname(this.getFirstname());
			ncboUser.setLastname(this.getLastname());
			ncboUser.setPhone(this.getPhone());
			ncboUser.setDateCreated(this.getDateCreated());

			// time stamp
			if (ncboUser.getDateCreated() == null) {
				ncboUser.setDateCreated(new Date());
			}
		}
	}

	/**
	 * Extracts default passwords and sets it in the bean
	 */
	public void generateDefaultPassword() {
		this.setPassword(MessageUtils.getMessage("default.user.password"));
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname
	 *            the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname
	 *            the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the roles
	 */
	public List<String> getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
