package org.ncbo.stanford.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.bean.acl.OntologyAcl;
import org.ncbo.stanford.domain.generated.NcboOntologyAcl;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserRole;
import org.ncbo.stanford.enumeration.RoleEnum;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

public class UserBean {

	private Integer id;
	private String username;
	private String password;
	private String apiKey;
	private String email;
	private String firstname;
	private String lastname;
	private String phone;
	private Date dateCreated;
	private List<String> roles = new ArrayList<String>(0);
	private OntologyAcl ontologyAcl = new OntologyAcl(0);

	public String toString() {
		return "{Id: " + this.getId() + ", Username: " + this.getUsername()
				+ ", API Key: " + this.getApiKey() + ", Password: "
				+ this.getPassword() + ", Email: " + this.getEmail()
				+ ", Firstname: " + this.getFirstname() + ", Lastname: "
				+ this.getLastname() + ", Phone: " + this.getPhone() + ", "
				+ "Roles: " + this.getRoles() + ", ACL: "
				+ this.getOntologyAcl() + "}";
	}

	/**
	 * Checks whether user has access to a given ontology (the ontology is
	 * present in his/her ACL)
	 * 
	 * @param ontologyId
	 * @return
	 */
	public boolean isInAcl(Integer ontologyId) {
		return ontologyAcl.containsKey(ontologyId);
	}

	/**
	 * Adds an ontology Id to this user's access list (ACL)
	 * 
	 * @param ontologyId
	 * @param isOwner
	 * @return
	 */
	public void addOntologyToAcl(Integer ontologyId, Boolean isOwner) {
		ontologyAcl.put(ontologyId, isOwner);
	}

	/**
	 * Checks on whether the user has Administrator privileges
	 * 
	 * @return
	 */
	public boolean isAdmin() {
		return roles.contains(RoleEnum.ROLE_ADMINISTRATOR.getLabel());
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
			this.setApiKey(ncboUser.getApiKey());
			this.setEmail(ncboUser.getEmail());
			this.setFirstname(ncboUser.getFirstname());
			this.setLastname(ncboUser.getLastname());
			this.setPhone(ncboUser.getPhone());
			this.setDateCreated(ncboUser.getDateCreated());

			for (Object role : ncboUser.getNcboUserRoles()) {
				roles.add(((NcboUserRole) role).getNcboLRole().getName());
			}

			for (Object ontAcl : ncboUser.getNcboOntologyAcls()) {
				ontologyAcl.put(((NcboOntologyAcl) ontAcl).getOntologyId(),
						((NcboOntologyAcl) ontAcl).getIsOwner());
			}
		}
	}

	/**
	 * Populates the NcboUser object with data from this instance
	 * 
	 * @param ncboUser
	 */
	public void populateToEntity(NcboUser ncboUser) {
		if (ncboUser != null) {
			ncboUser.setId(this.getId());
			ncboUser.setUsername(this.getUsername());
			ncboUser.setApiKey(this.getApiKey());

			if (!StringHelper.isNullOrNullString(this.getPassword())) {
				ncboUser.setPassword(this.getPassword());
			}

			ncboUser.setEmail(this.getEmail());
			ncboUser.setFirstname(this.getFirstname());
			ncboUser.setLastname(this.getLastname());
			ncboUser.setPhone(this.getPhone());

			Date dateCreated = this.getDateCreated();

			if (dateCreated == null) {
				dateCreated = new Date();
			}

			ncboUser.setDateCreated(new Timestamp(dateCreated.getTime()));
		}
	}

	/**
	 * Extracts default passwords and sets it in the bean
	 */
	public void generateDefaultPassword() {
		this.setPassword(MessageUtils.getMessage("default.user.password"));
	}

	/**
	 * Extracts default passwords and sets it in the bean
	 */
	public void generateDefaultRole() {
		roles.add(ApplicationConstants.DEFAULT_USER_ROLE);
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

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @param apiKey
	 *            the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return the ontologyAcl
	 */
	public Map<Integer, Boolean> getOntologyAcl() {
		return ontologyAcl;
	}
}
