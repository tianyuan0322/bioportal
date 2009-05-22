package org.ncbo.stanford.util.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyViewBean;
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
	public static UserBean populateUserBeanFromRequest(Request request) {
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);

		String username = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.username"));
		String password = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.password"));
		String firstname = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.firstname"));
		String lastname = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.lastname"));
		String email = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.email"));
		String phone = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.phone"));
		String dateCreated = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.dateCreated"));

		UserBean userBean = new UserBean();
		userBean.setUsername(username);
		userBean.setPassword(password);
		userBean.setEmail(email);
		userBean.setFirstname(firstname);
		userBean.setLastname(lastname);
		userBean.setPhone(phone);
		userBean.setDateCreated(DateHelper.getDateFrom(dateCreated));

		return userBean;
	}

	/**
	 * Creates OntologyViewBean object and populate from Request object.
	 * 
	 * <ontology Id>, <internal_version_number> - will be determined at ontology
	 * creation time <parent_id> - soon to be OBSOLETE
	 * 
	 * The following attributes are only for System or Admin. <statusId>,
	 * <codingScheme> - updated by Scheduler <isReviewed> - updated by Admin
	 * 
	 * source: request, destination: ontologyViewBean
	 * 
	 * @param Request
	 */
	public static OntologyViewBean populateOntologyViewBeanFromRequest(
			Request request) {
		OntologyViewBean bean = new OntologyViewBean();
		populateOntologyBeanFromRequest(bean, request);
		populateOntologyViewBeanFromRequest(bean, request);
		return bean;
	}

	/**
	 * Creates OntologyBean object and populate from Request object.
	 * 
	 * <ontology Id>, <internal_version_number> - will be determined at ontology
	 * creation time <parent_id> - soon to be OBSOLETE
	 * 
	 * The following attributes are only for System or Admin. <statusId>,
	 * <codingScheme> - updated by Scheduler <isReviewed> - updated by Admin
	 * 
	 * source: request, destination: ontologyBean
	 * 
	 * @param Request
	 */
	public static OntologyBean populateOntologyBeanFromRequest(Request request) {
		OntologyBean bean = new OntologyBean();
		populateOntologyBeanFromRequest(bean, request);
		return bean;
	}

	/**
	 * Creates OntologyBean object and populate from Request object.
	 * 
	 * <ontology Id>, <internal_version_number> - will be determined at ontology
	 * creation time
	 * 
	 * The following attributes are only for System or Admin. <statusId>,
	 * <codingScheme> - updated by Scheduler <isReviewed> - updated by Admin
	 * 
	 * source: request, destination: ontologyBean
	 * 
	 * @param Request
	 */
	public static OntologyBean populateOntologyBeanFromRequest(
			OntologyBean bean, Request request) {
		List<Integer> categoryIds = new ArrayList<Integer>(0);
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);

		// for new version for existing ontology
		String ontologyId = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.ontologyId"));
		// get userId from request
		String userId = httpServletRequest.getParameter(MessageUtils
				.getMessage("http.param.userId"));
		String isManual = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.isManual"));
		String versionNumber = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.versionNumber"));
		String versionStatus = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.versionStatus"));
		String isRemote = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.isRemote"));
		// only accessible by Admin
		String isReviewed = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.isReviewed"));
		String dateCreated = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.dateCreated"));
		String dateReleased = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.dateReleased"));
		// only accessible by Scheduler (system)
		String statusId = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.statusId"));
		String displayLabel = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.displayLabel"));
		String description = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.description"));
		String abbreviation = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.abbreviation"));
		String format = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.format"));
		String contactName = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.contactName"));
		String contactEmail = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.contactEmail"));
		String homepage = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.homepage"));
		String documentation = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.documentation"));
		String publication = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.publication"));
		String urn = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.urn"));
		String codingScheme = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.codingScheme"));
		String isFoundry = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.isFoundry"));
		String targetTerminologies = httpServletRequest
				.getParameter(MessageUtils
						.getMessage("form.ontology.targetTerminologies"));
		String synonymSlot = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.synonymSlot"));
		String preferredNameSlot = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.preferredNameSlot"));

		if (!StringHelper.isNullOrNullString(ontologyId)) {
			bean.setOntologyId(Integer.parseInt(ontologyId));
		}

		if (!StringHelper.isNullOrNullString(userId)) {
			bean.setUserId(Integer.parseInt(userId));
		}

		if (!StringHelper.isNullOrNullString(isManual)) {
			bean.setIsManual(Byte.parseByte(isManual));
		}

		if (!StringHelper.isNullOrNullString(isRemote)) {
			bean.setIsRemote(Byte.parseByte(isRemote));
		}

		if (isReviewed != null) {
			bean.setIsReviewed(Byte.parseByte(isReviewed));
		}

		if (!StringHelper.isNullOrNullString(dateCreated)) {
			bean.setDateCreated(DateHelper.getDateFrom(dateCreated));
		}

		if (!StringHelper.isNullOrNullString(statusId)) {
			bean.setStatusId(Integer.parseInt(statusId));
		}

		if (displayLabel != null) {
			bean.setDisplayLabel(displayLabel);
		}

		if (description != null) {
			bean.setDescription(description);
		}

		if (abbreviation != null) {
			bean.setAbbreviation(abbreviation);
		}

		if (format != null) {
			bean.setFormat(format);
		}

		if (versionNumber != null) {
			bean.setVersionNumber(versionNumber);
		}

		if (versionStatus != null) {
			bean.setVersionStatus(versionStatus);
		}

		if (dateReleased != null) {
			bean.setDateReleased(DateHelper.getDateFrom(dateReleased));
		}

		if (preferredNameSlot != null) {
			bean.setPreferredNameSlot(preferredNameSlot);
		}

		if (synonymSlot != null) {
			bean.setSynonymSlot(synonymSlot);
		}

		if (contactName != null) {
			bean.setContactName(contactName);
		}

		if (contactEmail != null) {
			bean.setContactEmail(contactEmail);
		}

		if (homepage != null) {
			bean.setHomepage(homepage);
		}

		if (documentation != null) {
			bean.setDocumentation(documentation);
		}

		if (publication != null) {
			bean.setPublication(publication);
		}

		if (urn != null) {
			bean.setUrn(urn);
		}

		if (codingScheme != null) {
			bean.setCodingScheme(codingScheme);
		}

		if (!StringHelper.isNullOrNullString(isFoundry)) {
			bean.setIsFoundry(Byte.parseByte(isFoundry));
		}

		if (targetTerminologies != null) {
			bean.setTargetTerminologies(targetTerminologies);
		}

		String categoryIdsStr = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.categoryId"));

		if (categoryIdsStr != null) {
			categoryIds = RequestUtils.parseIntegerListParam(categoryIdsStr);
			bean.setCategoryIds(categoryIds);
		}

		// set file attribute in ontologyBean
		FileItem fileItem = (FileItem) httpServletRequest
				.getAttribute(MessageUtils.getMessage("form.ontology.filePath"));

		if (fileItem != null) {
			bean.setFileItem(fileItem);
		}

		return bean;
	}

	private static void populateOntologyViewBeanFromRequest(
			OntologyViewBean bean, Request request) {
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);

		// for new version for existing ontology view
		String viewDefinition = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.viewDefinition"));
		String viewDefinitionLanguage = httpServletRequest
				.getParameter(MessageUtils
						.getMessage("form.ontology.viewDefinitionLanguage"));
		String viewGenerationEngine = httpServletRequest
				.getParameter(MessageUtils
						.getMessage("form.ontology.viewGenerationEngine"));

		List<Integer> ontVerIds = new ArrayList<Integer>();

		String[] ontVerIdsStr = httpServletRequest
				.getParameterValues(MessageUtils
						.getMessage("form.ontology.viewOntologyVersionId"));

		if (ontVerIdsStr != null) {
			for (String categoryIdStr : ontVerIdsStr) {
				ontVerIds.add(Integer.parseInt(categoryIdStr));
			}
		}

		// now populate the OntologyViewBean
		if (!StringHelper.isNullOrNullString(viewDefinition)) {
			bean.setViewDefinition(viewDefinition);
		}
		
		if (!StringHelper.isNullOrNullString(viewDefinitionLanguage)) {
			bean.setViewDefinition(viewDefinitionLanguage);
		}
		
		if (!StringHelper.isNullOrNullString(viewGenerationEngine)) {
			bean.setViewDefinition(viewGenerationEngine);
		}

		if (ontVerIds.size() > 0) {
			bean.setViewOnOntologyVersionId(ontVerIds);
		}
	}
}
