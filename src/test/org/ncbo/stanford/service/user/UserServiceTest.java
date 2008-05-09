/**
 * 
 */
package org.ncbo.stanford.service.user;

import java.util.Date;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.UserBean;

/**
 * Test create/find/findAll/update/delete 
 * 
 * @author cyoun
 * 
 */
public class UserServiceTest extends AbstractBioPortalTest {

	
	public void testFindUsers() {

		List<UserBean> users = getUserService().findUsers();

		System.out.println ("UserServiceTest: testfindUsers().......................BEGIN");
		
		for (UserBean user : users) {

			System.out.println(user.toString());
		}
		
		System.out.println ("UserServiceTest: testfindUsers().........................DONE");

	}
	

	public void testCreateUser() {
		
		System.out.println ("UserServiceTest: testCreateUser()........................BEGIN");
			
		UserBean userBean = createTestBean();
		getUserService().createUser(userBean);
		
		System.out.println ("UserServiceTest: testCreateUser().........................DONE");
	}
	
	
	public void testFindUser() {
		
		System.out.println ("UserServiceTest: testFindUser()..........................BEGIN");
		
		UserBean userBean = getUserService().findUser(new Integer(2850));
		
		if (userBean != null) {
			System.out.println ("...Username is " +  userBean.getUsername());
			System.out.println ("...Email is " +  userBean.getEmail());
		}
		
		System.out.println ("UserServiceTest: testFindUser()...........................DONE");
		
	}
	
	

	public void testUpdateUser() {
		
		System.out.println ("UserServiceTest: testUpdateUsers().......................BEGIN");
		
		UserBean userBean = getUserService().findUser("myusername");
				
		if (userBean != null) {
			
			System.out.println (".....Updating phone number to 111-222-3333");
			
			userBean.setPhone("333-222-3333");
			getUserService().updateUser(userBean);
		}
		else {
			System.out.println (".....No matching record!");
		}
		
		System.out.println ("UserServiceTest: testUpdateUsers()........................DONE");
		
	}
	
	
	
	public void testDeleteUser() {
		
		System.out.println ("UserServiceTest: testDeleteUser()........................BEGIN");
		
		// "getUser" by username does not work if duplicate is allowed in DB.
		UserBean userBean = getUserService().findUser("myusername");
		
		if ( userBean != null ) {

			getUserService().deleteUser(userBean);
			
		} else {
			System.out.println (".....No matching record!");
		}
		
		System.out.println ("UserServiceTest: testDeleteUser().........................DONE");
		
	}
	
	
	private UserBean createTestBean() {
		
		UserBean bean = new UserBean();
		bean.setUsername("myusername");
		bean.setPassword("mypassword");
		bean.setEmail("myemail@stanford.edu");
		bean.setFirstname("myfirstname");
		bean.setLastname("mylastname");
		bean.setPhone("123-456-7890");
		bean.setDateCreated(new Date());

		return bean;
	}
	
	
	private UserService getUserService() {
		
		UserService service = (UserService) applicationContext.getBean(
				"userService", UserService.class);
	
		return service;
	}

}
