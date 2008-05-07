package org.ncbo.stanford.util.helper;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.restlet.data.Request;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.DateHelper;


public class UserHelper {
		

	
	/**
	 * Creates UserBean object and populate from Request object
	 * 
	 * source: request, destination: userBean
	 * 
	 * @param Request
	 */
	public static UserBean populateUserBeanFromRequest (Request request) {
		

		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);
		
		// TODO code clean up later. Use constants!
		String username = httpServletRequest.getParameter("username");
		String password = httpServletRequest.getParameter("password");
		String firstname = httpServletRequest.getParameter("firstname");
		String lastname = httpServletRequest.getParameter("lastname");
		String email = httpServletRequest.getParameter("email");
		String phone = httpServletRequest.getParameter("phone");
		Date dateCreated = DateHelper.getDateFrom(httpServletRequest.getParameter("dateCreated"));
	
		UserBean userBean = new UserBean();
		userBean.setUsername(username);
		userBean.setPassword(password);
		userBean.setEmail(email);
		userBean.setFirstname(firstname);
		userBean.setLastname(lastname);
		userBean.setPhone(phone);
		userBean.setDateCreated(dateCreated);
	
		
		// DEBUG STATETMENT - to be removed later
		System.out.println("**************************");
		System.out.println("username = " + userBean.getUsername());
		System.out.println("password = " + userBean.getPassword());
		System.out.println("first name = " + userBean.getFirstname());
		System.out.println("last name = " + userBean.getLastname());
		System.out.println("email = " + userBean.getEmail());
		System.out.println("dateCreated = " + userBean.getDateCreated());
		System.out.println("**************************");
		
		return userBean;
	}

}

