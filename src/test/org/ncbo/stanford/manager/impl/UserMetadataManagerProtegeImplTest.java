package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.manager.metadata.impl.UserMetadataManagerProtegeImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests user metadata related operations using the
 * UserMetadataManagerProtegeImpl
 * 
 * @author Csongor Nyulas
 */
public class UserMetadataManagerProtegeImplTest extends AbstractBioPortalTest {
	
	private static int ID_USER_1 = 2002;
	private static String USERNAME_1 = "test";
	private static String PASSWORD_1 = "test_pwd";

	private static int ID_USER_ROLE_1 = 2825;

	@Autowired
	UserMetadataManagerProtegeImpl userMetadataManagerProtege;


	@Test
	public void testSaveUserRoleMetadata() {
		System.out.println("Starting testSaveUserRoleMetadata");
		
		try {
			userMetadataManagerProtege.saveUserRole(ID_USER_ROLE_1, "TEST_ROLE", "This user role was created by testing");
		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			fail("save user role metadata failed: " + e.getMessage());
		}
		
		System.out.println("End testSaveUserRoleMetadata");
	}
	
	@Test
	public void testFindUserRoleMetadataById() {
		System.out.println("Starting testFindUserRoleMetadataById");
		
		try {
			String roleName = userMetadataManagerProtege.findUserRoleNameById(ID_USER_ROLE_1);
			
			System.out.println("UserRole name: " + roleName);
			assertNotNull("Retrieving ontology metadata by ID has failed", roleName);
		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			fail("find user role metadata by ID failed: " + e.getMessage());
		}
		
		System.out.println("End testFindUserRoleMetadataById");
	}

	@Test
	public void testSaveUserMetadata() {
		System.out.println("Starting testSaveUserMetadata");
		
		try {
			UserBean userBean = createUserBean(ID_USER_1, USERNAME_1, PASSWORD_1);
			userMetadataManagerProtege.saveUser(userBean);
		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			fail("save user metadata failed: " + e.getMessage());
		}
		
		System.out.println("End testSaveUserMetadata");
	}
	
	@Test
	public void testFindUserMetadataById() {
		System.out.println("Starting testFindUserMetadataById");
		
		try {
			UserBean ub = userMetadataManagerProtege.findUserById(ID_USER_1);
			
			System.out.println("UserBean: " + ub);
			assertNotNull("Retrieving ontology metadata by ID has failed", ub);
			
			System.out.println("Date Created (metadata:timestampCreation): " + ub.getDateCreated());
		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			fail("find user metadata by ID failed: " + e.getMessage());
		}
		
		System.out.println("End testFindUserMetadataById");
	}
	
	

	private UserBean createUserBean(int id, String username, String password) {
		UserBean ub = createUserBeanBase();
		setUserBeanEssentialProperties(ub, id, username, password);
		return ub;
	}
	
	private void setUserBeanEssentialProperties(UserBean ub,
			int id, String username, String password) {
		ub.setId(id);
		ub.setUsername(username);
		ub.setPassword(password);
	}
	
	private UserBean createUserBeanBase() {
		UserBean bean = new UserBean();
		// bean.setId(3000);
		bean.setDateCreated(new Date());
		bean.setEmail("nick@email.com");
		bean.setFirstname("Nick");
		bean.setLastname("Griffith");
		bean.setPhone("(555)-123-4567");
		return bean;
	}

}
