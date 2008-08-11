package org.ncbo.stanford.domain.generated;

import java.util.Date;
import java.util.Set;

/**
 * NcboUser entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class NcboUser extends AbstractNcboUser implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboUser() {
	}

	/** minimal constructor */
	public NcboUser(String username, String password, String email,
			String firstname, String lastname, Date dateCreated) {
		super(username, password, email, firstname, lastname, dateCreated);
	}

	/** full constructor */
	public NcboUser(String username, String password, String email,
			String firstname, String lastname, String phone, Date dateCreated,
			Set ncboUserRoles, Set ncboOntologyVersions) {
		super(username, password, email, firstname, lastname, phone,
				dateCreated, ncboUserRoles, ncboOntologyVersions);
	}

}
