/**
 * 
 */
package org.ncbo.stanford.service.user;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test create/find/findAll/update/delete
 * 
 * @author cyoun
 * 
 */
public class UserServiceTest extends AbstractBioPortalTest {

	@Autowired
	UserService userService;

	@Test
	public void testFindUsers() {
		List<UserBean> users = userService.findUsers();

		System.out
				.println("UserServiceTest: testfindUsers().......................BEGIN");

		for (UserBean user : users) {

			System.out.println(user.toString());
		}

		System.out
				.println("UserServiceTest: testfindUsers().........................DONE");
	}

	@Test
	public void testCreateUser() {
		System.out
				.println("UserServiceTest: testCreateUser()........................BEGIN");

		UserBean userBean = createTestBean();
		userService.createUser(userBean);

		System.out
				.println("UserServiceTest: testCreateUser().........................DONE");
	}

	@Test
	public void testFindUser() {
		System.out
				.println("UserServiceTest: testFindUser()..........................BEGIN");

		UserBean userBean = userService.findUser(new Integer(2850));

		if (userBean != null) {
			System.out.println("...Username is " + userBean.getUsername());
			System.out.println("...Email is " + userBean.getEmail());
		}

		System.out
				.println("UserServiceTest: testFindUser()...........................DONE");
	}

	@Test
	public void testUpdateUser() {
		System.out
				.println("UserServiceTest: testUpdateUsers().......................BEGIN");

		UserBean userBean = userService.findUser("myusername");

		if (userBean != null) {
			System.out.println(".....Updating phone number to 111-222-3333");

			userBean.setPhone("333-222-3333");
			userService.updateUser(userBean);
		} else {
			System.out.println(".....No matching record!");
		}

		System.out
				.println("UserServiceTest: testUpdateUsers()........................DONE");
	}

	@Test
	public void testDeleteUser() {
		System.out
				.println("UserServiceTest: testDeleteUser()........................BEGIN");

		// "getUser" by username does not work if duplicate is allowed in DB.
		UserBean userBean = userService.findUser("myusername");

		if (userBean != null) {
			userService.deleteUser(userBean);
		} else {
			System.out.println(".....No matching record!");
		}

		System.out
				.println("UserServiceTest: testDeleteUser().........................DONE");
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
		//Adding this line for testing the creation of userAccount inside the metadata
		ArrayList<RoleEnum> roles = new ArrayList<RoleEnum>(2);
		roles.add(RoleEnum.ROLE_DEVELOPER);
		roles.add(RoleEnum.ROLE_ADMINISTRATOR);
		bean.setRoles(roles);

		return bean;
	}
}
