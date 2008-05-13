package org.ncbo.stanford.util.helper;

import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.restlet.data.Request;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.helper.DateHelper;
//import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.ncbo.stanford.util.MessageUtils;

public class BeanHelper {
		
	
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
						
		String username = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.username"));
		String password = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.password"));
		String firstname = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.firstname"));
		String lastname = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.lastname"));
		String email = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.email"));
		String phone = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.phone"));
		String dateCreated = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.dateCreated"));
				
		UserBean userBean = new UserBean();
		userBean.setUsername(username);
		userBean.setPassword(password);
		userBean.setEmail(email);
		userBean.setFirstname(firstname);
		userBean.setLastname(lastname);
		userBean.setPhone(phone);
		userBean.setDateCreated(DateHelper.getDateFrom(dateCreated));
	
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


	
	
	/**
	 * Creates UserBean object and populate from Request object
	 * 
	 * source: request, destination: userBean
	 * 
	 * @param Request
	 */
	public static OntologyBean populateOntologyBeanFromRequest (Request request) {
		

		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);
		
/*		
		<ontology>
		<id>4519</id>
		<ontologyId>1001</ontologyId>
		<internalVersionNumber>0</internalVersionNumber>
		<versionNumber>1.2</versionNumber>
		<versionStatus>production</versionStatus>
		<filePath>/4519</filePath>
		<isCurrent>1</isCurrent>
		<isRemote>0</isRemote>
		<isReviewed>1</isReviewed>
		<dateCreated class="sql-timestamp">2007-01-16 13:51:11.0</dateCreated>
		<dateReleased class="sql-timestamp">2007-01-10 00:00:00.0</dateReleased>
		<displayLabel>Amino Acid</displayLabel>
		<format>OWL-FULL</format>
*/		
		
		//Set<String> names = httpServletRequest.getAttributeNames()
		String contactEmail = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.contactEmail"));
		String contactName = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.contactName"));
		String dateCreated = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.dateCreated"));
		String dateReleased = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.dateReleased"));		
		String displayLabel = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.displayLabel"));
		String documentation = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.documentation"));
		String format = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.format"));
		String homepage = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.homepage"));
		String isFoundry = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.isFoundry"));
		String publication = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.publication"));
		String urn = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.urn"));
		String versionNumber = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.versionNumber"));
		String versionStatus = httpServletRequest.getParameter(MessageUtils.getMessage("form.user.versionStatus"));
		
		OntologyBean bean = new OntologyBean();
		bean.setContactEmail(contactEmail);
		bean.setContactName(contactName);
		bean.setDateCreated(DateHelper.getDateFrom(dateCreated));
		bean.setDateReleased(DateHelper.getDateFrom(dateReleased));
		bean.setDisplayLabel(displayLabel);
		bean.setDocumentation(documentation);
		bean.setFormat(format);
		bean.setHomepage(homepage);
		bean.setIsFoundry(Byte.parseByte(isFoundry));
		bean.setPublication(publication);
		bean.setUrn(urn);
		bean.setVersionNumber(versionNumber);
		bean.setVersionStatus(versionStatus);
		
		return bean;

	}
	
	/*
	private OntologyBean buildBeanFromForm(Form form) {
		OntologyBean bean = new OntologyBean();
		Set<String> names = form.getNames();
		bean.setContactEmail(form.getValues("contactEmail"));
		bean.setContactName(form.getValues("contactName"));
		bean.setDateCreated(DateHelper.getDateFrom(form
				.getValues("dateCreated")));
		bean.setDateReleased(DateHelper.getDateFrom(form
				.getValues("dateReleased")));
		bean.setDisplayLabel(form.getValues("displayLabel"));
		bean.setDocumentation(form.getValues("documentation"));
		bean.setFormat(form.getValues("format"));
		bean.setHomepage(form.getValues("homepage"));
		bean.setIsFoundry(Byte.parseByte(form.getValues("isFoundry")));
		bean.setPublication(form.getValues("publication"));
		bean.setUrn(form.getValues("urn"));
		bean.setVersionNumber(form.getValues("versionNumber"));
		bean.setVersionStatus(form.getValues("versionStatus"));
		return bean;
	}
	*/
	
}

