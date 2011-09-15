package org.ncbo.stanford.util.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.SubscriptionsBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.logging.UsageLoggingBean;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;

public class BeanHelper {

	/**
	 * Creates UserBean object and populate from Request object
	 *
	 * source: request, destination: userBean
	 *
	 * @param Request
	 */
	public static void populateUserBeanFromRequest(Request request,
			UserBean userBean) {
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);

		String username = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.username"));
		String password = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.password"));
		String email = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.email"));

		String firstname = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.firstname"));
		String lastname = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.lastname"));
		String phone = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.phone"));
		String dateCreated = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.dateCreated"));
		String ontologyAcl = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.ontologyacl"));

		if (!StringHelper.isNullOrNullString(username)) {
			userBean.setUsername(username);
		}

		if (!StringHelper.isNullOrNullString(password)) {
			userBean.setPassword(password);
		}

		if (!StringHelper.isNullOrNullString(email)) {
			userBean.setEmail(email);
		}

		if (firstname != null) {
			userBean.setFirstname(firstname);
		}

		if (lastname != null) {
			userBean.setLastname(lastname);
		}

		if (phone != null) {
			userBean.setPhone(phone);
		}

		if (!StringHelper.isNullOrNullString(dateCreated)) {
			userBean.setDateCreated(DateHelper.getDateFrom(dateCreated));
		}

		List<Integer> ontologyAclList = RequestUtils
				.parseIntegerListParam(ontologyAcl);

		for (Integer ontologyId : ontologyAclList) {
			userBean.addOntologyToAcl(ontologyId, false);
		}
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
		OntologyBean bean = new OntologyBean(false);
		populateOntologyBeanFromRequest(bean, request);
		return bean;
	}

	/**
	 * Takes an OntologyBean object and populates it from Request object.
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
	public static void populateOntologyBeanFromRequest(OntologyBean bean,
			Request request) {
		List<Integer> categoryIds = new ArrayList<Integer>(0);
		List<Integer> groupIds = new ArrayList<Integer>(0);
		List<Integer> hasViewIds = new ArrayList<Integer>(0);
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);

		// first decide if it is a view or not
		String isView = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.isView"));

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
		String downloadLocation = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.downloadLocation"));
		String isFlat = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.isFlat"));
		String isFoundry = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.isFoundry"));
		String isMetadataOnly = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.isMetadataOnly"));
		String targetTerminologies = httpServletRequest
				.getParameter(MessageUtils
						.getMessage("form.ontology.targetTerminologies"));
		String synonymSlot = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.synonymSlot"));
		String preferredNameSlot = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.preferredNameSlot"));
		String documentationSlot = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.documentationSlot"));
		String authorSlot = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.authorSlot"));
		String slotWithUniqueValue = httpServletRequest
				.getParameter(MessageUtils
						.getMessage("form.ontology.slotWithUniqueValue"));
		String preferredMaximumSubclassLimit = httpServletRequest
				.getParameter(MessageUtils
						.getMessage("form.ontology.preferredMaximumSubclassLimit"));
		String userAcl = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.useracl"));
		String viewingRestriction = httpServletRequest
				.getParameter(MessageUtils
						.getMessage("form.ontology.viewingRestriction"));
		String licenseInformation = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.licenseInformation"));

		String obsoleteParent = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.obsoleteParent"));
		String obsoleteProperty = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.obsoleteProperty"));

		boolean isViewBool = RequestUtils.parseBooleanParam(isView);

		if (!StringHelper.isNullOrNullString(isView)) {
			bean.setView(isViewBool);
		}

		if (!StringHelper.isNullOrNullString(ontologyId)) {
			bean.setOntologyId(Integer.parseInt(ontologyId));
		}

		// bean.setVirtualViewIds(...)
		// we do not set here the virtualViewIds of the ontology bean
		// from the request, because that is only a read-only field,
		// calculated from the metadata ontology on a read operation

		if (!StringHelper.isNullOrNullString(userId)) {
			Integer userIdInt = Integer.parseInt(userId);
			bean.setUserId(userIdInt);

			// set user ACL if passed in
			if (userAcl != null && !isViewBool) {
				bean.emptyUserAcl();
				// add this user as the owner
				bean.addUserToAcl(userIdInt, true);
				List<Integer> userIds = RequestUtils
						.parseIntegerListParam(userAcl);

				for (Integer usrId : userIds) {
					bean.addUserToAcl(usrId, (usrId.equals(userIdInt)) ? true
							: false);
				}
			}
		}

		if (!StringHelper.isNullOrNullString(viewingRestriction)) {
			bean.setViewingRestriction(viewingRestriction);
		}

		if (!StringHelper.isNullOrNullString(licenseInformation)) {
			bean.setLicenseInformation(licenseInformation);
		}

		if (!StringHelper.isNullOrNullString(isManual)) {
			bean.setIsManual(Byte.parseByte(isManual));
		}

		if (!StringHelper.isNullOrNullString(isRemote)) {
			bean.setIsRemote(Byte.parseByte(isRemote));
		}

		if (!StringHelper.isNullOrNullString(isReviewed)) {
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
			bean.setFormat(format.toUpperCase());
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

		if (documentationSlot != null) {
			bean.setDocumentationSlot(documentationSlot);
		}

		if (authorSlot != null) {
			bean.setAuthorSlot(authorSlot);
		}

		if (slotWithUniqueValue != null) {
			bean.setSlotWithUniqueValue(slotWithUniqueValue);
		}

		if (!StringHelper.isNullOrNullString(preferredMaximumSubclassLimit)) {
			bean.setPreferredMaximumSubclassLimit(Integer
					.parseInt(preferredMaximumSubclassLimit));
		}

		if (!StringHelper.isNullOrNullString(obsoleteParent)) {
			bean.setObsoleteParent(obsoleteParent);
		}

		if (!StringHelper.isNullOrNullString(obsoleteProperty)) {
			bean.setObsoleteProperty(obsoleteProperty);
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

		if (downloadLocation != null) {
			bean.setDownloadLocation(downloadLocation);
		}
		if (!StringHelper.isNullOrNullString(isFlat)) {
			bean.setIsFlat(Byte.parseByte(isFlat));
		}
		if (!StringHelper.isNullOrNullString(isFoundry)) {
			bean.setIsFoundry(Byte.parseByte(isFoundry));
		}

		if (!StringHelper.isNullOrNullString(isMetadataOnly)) {
			bean.setIsMetadataOnly(Byte.parseByte(isMetadataOnly));
		}

		if (targetTerminologies != null) {
			bean.setTargetTerminologies(targetTerminologies);
		}

		String[] categoryIdValues = httpServletRequest
				.getParameterValues(MessageUtils
						.getMessage("form.ontology.categoryId"));

		if (categoryIdValues != null) {
			categoryIds = RequestUtils.parseIntegerListParam(categoryIdValues);
			bean.setCategoryIds(categoryIds);
		}

		String[] groupIdValues = httpServletRequest
				.getParameterValues(MessageUtils
						.getMessage("form.ontology.groupId"));

		if (groupIdValues != null) {
			groupIds = RequestUtils.parseIntegerListParam(groupIdValues);
			bean.setGroupIds(groupIds);
		}

		// set file attribute in ontologyBean
		FileItem fileItem = (FileItem) httpServletRequest
				.getAttribute(MessageUtils.getMessage("form.ontology.filePath"));

		if (fileItem != null) {
			bean.setFileItem(fileItem);
		}

		// specifying this in the request is optional. Views should be added to
		// a
		// version by specifying value for the "viewOnOntologyVersionId"
		// parameter
		// on a create/update view request
		String[] hasViewIdValues = httpServletRequest
				.getParameterValues(MessageUtils
						.getMessage("form.ontology.hasView"));

		if (hasViewIdValues != null) {
			hasViewIds = RequestUtils.parseIntegerListParam(hasViewIdValues);
			bean.setHasViews(hasViewIds);
		}

		// populate view specific values
		if (bean.isView()) {
			// for new version for existing ontology view
			String viewDefinition = httpServletRequest
					.getParameter(MessageUtils
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
							.getMessage("form.ontology.viewOnOntologyVersionId"));

			if (ontVerIdsStr != null) {
				ontVerIds = RequestUtils.parseIntegerListParam(ontVerIdsStr);
				if (ontVerIds.size() > 0) {
					bean.setViewOnOntologyVersionId(ontVerIds);
				}
			}

			// now populate the OntologyViewBean
			if (!StringHelper.isNullOrNullString(viewDefinition)) {
				bean.setViewDefinition(viewDefinition);
			}

			if (!StringHelper.isNullOrNullString(viewDefinitionLanguage)) {
				bean.setViewDefinitionLanguage(viewDefinitionLanguage);
			}

			if (!StringHelper.isNullOrNullString(viewGenerationEngine)) {
				bean.setViewGenerationEngine(viewGenerationEngine);
			}
		}
	}

	/**
	 * Creates UsageLoggingBean object and populates from Request object. This
	 * method is used when populating for data recording purposes. source:
	 * request, destination: usageLoggingBean
	 *
	 * @param Request
	 */
	public static UsageLoggingBean populateUsageLoggingBeanFromRequestForLogging(
			Request request) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		String applicationId = RequestUtils.getApiKey(request);
		String eventType = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_EVENT_TYPE);
		String requestUrl = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_REQUEST_URL);
		String httpMethod = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_METHOD);
		String userId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_USER_ID);
		String apiKey = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_APIKEY);
		String ipAddress = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_IP_ADDRESS);
		String ontologyVersionId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ONTOLOGY_VERSION_ID);
		String ontologyId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ONTOLOGY_ID);
		String ontologyName = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_ONTOLOGY_NAME);
		String conceptId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_CONCEPT_ID);
		String conceptName = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_CONCEPT_NAME);
		String searchQuery = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_QUERY);
		String searchParameters = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_SEARCH_PARAMETERS);
		String numSearchResults = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NUM_SEARCH_RESULTS);

		return new UsageLoggingBean(applicationId, eventType, requestUrl,
				httpMethod, userId, apiKey, ipAddress, ontologyVersionId,
				ontologyId, ontologyName, conceptId, conceptName, searchQuery,
				searchParameters, numSearchResults);
	}

	/**
	 * Creates UsageLoggingBean object and populates from Request object. This
	 * method is used when populating for logging data extraction. source:
	 * request, destination: usageLoggingBean
	 *
	 * @param Request
	 */
	public static UsageLoggingBean populateUsageLoggingBeanFromRequestForDataExtraction(
			Request request) {
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);
		String applicationId = RequestUtils.getApiKey(request);
		String requestUrl = (String) httpServletRequest
				.getParameter(RequestParamConstants.PARAM_REQUEST_URL);
		String startDateAccessed = (String) httpServletRequest
				.getParameter(RequestParamConstants.PARAM_START_DATE_ACCESSED);
		String endDateAccessed = (String) httpServletRequest
				.getParameter(RequestParamConstants.PARAM_END_DATE_ACCESSED);

		Date startDate = RequestUtils.parseDateParam(startDateAccessed);
		Date endDate = RequestUtils.parseDateParam(endDateAccessed);

		UsageLoggingBean usageLoggingBean = new UsageLoggingBean(applicationId,
				null, requestUrl, null, null, null, null, null, null, null,
				null, null, null, null, null);

		if (startDate != null) {
			usageLoggingBean.setStartDateAccessed(startDate);
		}

		if (endDate != null) {
			usageLoggingBean.setEndDateAccessed(endDate);
		}

		return usageLoggingBean;
	}

	/**
	 * Create SubscriptionsBean object and populates from Request object.
	 *
	 * @param request
	 * @return
	 */
	public static SubscriptionsBean populateSubscriptionsBeanFromRequest(
			Request request) {
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);

		String userId = RequestUtils.getAttributeOrRequestParam(MessageUtils
				.getMessage("form.user.userId"), request);

		String notificationType = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.user.notificationType"));
		// Taking the OntologyIds in the List
		List<String> ontologyId = getOntologyIds(request);
		SubscriptionsBean subscriptionsBean = new SubscriptionsBean();
		subscriptionsBean.setUserId(RequestUtils.parseIntegerParam(userId));
		subscriptionsBean.setOntologyIds(ontologyId);
		subscriptionsBean.setNotificationType(NotificationTypeEnum
				.valueOf(StringUtils.upperCase(notificationType)));
		// subscriptionsBean.setOntologyIds(ontologyIds);

		return subscriptionsBean;
	}

	/**
	 * It will collect the OntologyIds from the request
	 *
	 * @param request
	 * @return
	 */
	public static List<String> getOntologyIds(Request request) {
		return RequestUtils.getAttributeOrRequestParams(MessageUtils
				.getMessage("entity.ontologyid"), request);
	}

}
