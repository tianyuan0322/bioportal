package org.ncbo.stanford.domain.generated;

import java.sql.Timestamp;
import java.util.Set;

/**
 * NcboUser entity. @author MyEclipse Persistence Tools
 */
public class NcboUser extends AbstractNcboUser implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public NcboUser() {
	}

	/** minimal constructor */
	public NcboUser(String username, String apiKey, String password,
			String email, Timestamp dateCreated) {
		super(username, apiKey, password, email, dateCreated);
	}

	/** full constructor */
	public NcboUser(String username, String apiKey, String openId,
			String password, String email, String firstname, String lastname,
			String phone, Timestamp dateCreated, Set ncboUserRoles,
			Set ncboOntologyAcls) {
		super(username, apiKey, openId, password, email, firstname, lastname,
				phone, dateCreated, ncboUserRoles, ncboOntologyAcls);
	}

}
