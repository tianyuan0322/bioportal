package org.ncbo.stanford.bean.acl;

import java.util.HashMap;

public class UserAcl extends HashMap<Integer, Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3140353834321018542L;
	
	public UserAcl(int initialCapacity) {
		super(initialCapacity);
	}
}
