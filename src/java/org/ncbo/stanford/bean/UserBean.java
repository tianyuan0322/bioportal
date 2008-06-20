package org.ncbo.stanford.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserRole;

public class UserBean {

	private Integer id;
	private String username;
	private String password;
	private String email;
	private String firstname;
	private String lastname;
	private String phone;
	private Date dateCreated;
	private List roles;

	
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
			List roles = new ArrayList();
			for(Object role :  ncboUser.getNcboUserRoles()){
				
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
			ncboUser.setPassword(this.getPassword());
			ncboUser.setEmail(this.getEmail());
			ncboUser.setFirstname(this.getFirstname());
			ncboUser.setLastname(this.getLastname());
			ncboUser.setPhone(this.getPhone());
			ncboUser.setDateCreated(this.getDateCreated());			
		}
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

	public List getRoles() {
		return roles;
	}

	public void setRoles(List roles) {
		this.roles = roles;
	}
	
	
	
}
