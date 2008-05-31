package org.ncbo.stanford.util.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.restlet.data.Request;

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
	 * Creates OntologyBean object and populate from Request object.
	 * 
	 * 		<ontology Id>, <internal_version_number> - will be determined at ontology creation time
	 * 		<parent_id> - soon to be OBSOLETE
	 * 
	 * The following attributes are only for System or Admin. 
	 * 		<statusId>, <codingScheme> - updated by Scheduler
	 * 		<isReviewed> - updated by Admin
	 * 
	 * source: request, destination: ontologyBean
	 * 
	 * @param Request
	 */
	public static OntologyBean populateOntologyBeanFromRequest (Request request) {
		
		//TODO <userId> - retrieve user obj from session. 
		//populate OntologyBean userId field here? or in OntologyBean "populate"?
		
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);
		
		String versionNumber = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.versionNumber"));
		String versionStatus = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.versionStatus"));
		String filePath = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.filePath"));
		String isCurrent = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.isCurrent"));
		String isRemote = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.isRemote"));
		String dateCreated = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.dateCreated"));
		String dateReleased = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.dateReleased"));
		String displayLabel = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.displayLabel"));
		String format = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.format"));

		String contactName = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.contactName"));
		String contactEmail = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.contactEmail"));
		String homepage = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.homepage"));
		String documentation = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.documentation"));
		String publication = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.publication"));
		String urn = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.urn"));
		String isFoundry = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.isFoundry"));

		List <Integer> categoryIds = new ArrayList<Integer>();
		
		String[] categoryIdsStr = httpServletRequest.getParameterValues(MessageUtils.getMessage("form.ontology.categoryId"));
		if (categoryIdsStr != null) {
			for (String categoryIdStr : categoryIdsStr) {
				categoryIds.add(Integer.parseInt(categoryIdStr));
			}
		}
		
		// only accessible by Admin
		String isReviewed = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.isReviewed"));

		// only accessible by Scheduler (system)
		String statusId = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.statusId"));
		String codingScheme = httpServletRequest.getParameter(MessageUtils.getMessage("form.ontology.codingScheme"));
				
		OntologyBean bean = new OntologyBean();
		bean.setVersionNumber(versionNumber);
		bean.setVersionStatus(versionStatus);
		bean.setFilePath(filePath);
		
		if ( isCurrent != null) bean.setIsCurrent(Byte.parseByte(isCurrent));
		if ( isRemote != null) bean.setIsRemote(Byte.parseByte(isRemote));
		if ( isReviewed != null) bean.setIsReviewed(Byte.parseByte(isReviewed));
		if ( statusId != null) bean.setStatusId(Integer.parseInt(statusId));
		if ( dateCreated != null) bean.setDateCreated(DateHelper.getDateFrom(dateCreated));
		if ( dateReleased != null) bean.setDateReleased(DateHelper.getDateFrom(dateReleased));
		
		bean.setDisplayLabel(displayLabel);
		bean.setFormat(format);
		bean.setContactName(contactName);
		bean.setContactEmail(contactEmail);
		bean.setHomepage(homepage);
		bean.setDocumentation(documentation);
		bean.setPublication(publication);
		bean.setUrn(urn);
		bean.setCodingScheme(codingScheme);
		
		if ( isFoundry != null) bean.setIsFoundry(Byte.parseByte(isFoundry));
		
		if (categoryIds.size() > 0) bean.setCategoryIds(categoryIds);
		
		
		// DEBUG STATETMENT - to be removed later
		System.out.println("**************************");
		System.out.println("HTTP REQUEST CONTENT:");
		System.out.println("versionNumber = " + bean.getVersionNumber());
		System.out.println("filePath = " + bean.getFilePath());
		System.out.println("contactName = " + bean.getContactName());
		System.out.println("contactEmail = " + bean.getContactEmail());
		System.out.println("**************************");
		
		return bean;

	}
	
}

