/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import java.util.List;

import org.ncbo.stanford.domain.generated.NcboLRole;
import org.ncbo.stanford.domain.generated.NcboLRoleDAO;

/**
 * @author Michael Dorf
 *
 */
public class CustomNcboLRoleDAO extends NcboLRoleDAO {

	/**
	 * 
	 */
	public CustomNcboLRoleDAO() {
		super();
	}

	/**
	 * Finds a role using its name
	 * 
	 * @param roleName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public NcboLRole findRoleByName(String roleName) {
		NcboLRole role = null;
		List<NcboLRole> roles = findByName(roleName);
		
		if (roles.size() > 0) {
			role = roles.get(0);
		}
		
		return role;
	}
}
