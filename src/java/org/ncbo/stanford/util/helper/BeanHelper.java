package org.ncbo.stanford.util.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.logging.UsageLoggingBean;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
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

		//first decide if it is a view or not
		String isView = httpServletRequest.getParameter(MessageUtils
				.getMessage("form.ontology.isView"));
		if (!StringHelper.isNullOrNullString(isView)) {
			bean.setView(Boolean.parseBoolean(isView));
		}

		
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

		//bean.setVirtualViewIds(...)
		//we do not set here the virtualViewIds of the ontology bean
		//from the request, because that is only a read-only field, 
		//calculated from the metadata ontology on a read operation 
		
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
		
		//specifying this in the request is optional. Views should be added to a
		//version by specifying value for the "viewOnOntologyVersionId" parameter
		//on a create/update view request
		String[] hasViewIdValues = httpServletRequest
		.getParameterValues(MessageUtils
				.getMessage("form.ontology.hasView"));
		
		if (hasViewIdValues != null) {
			hasViewIds = RequestUtils.parseIntegerListParam(hasViewIdValues);
			bean.setHasViews(hasViewIds);
		}
		
		
		//populate view specific values
		if (bean.isView()) {
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
		UsageLoggingBean usageLoggingBean = new UsageLoggingBean();
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);

		String applicationId = RequestUtils.getApplicationId(request);
		String resourceParameters = RequestUtils
				.getResourceAttributesAsString(request);
		String requestParameters = RequestUtils
				.getRequestParametersAsString(httpServletRequest);

		if (applicationId != null) {
			usageLoggingBean.setApplicationId(applicationId);
		}

		usageLoggingBean.setRequestUrl(httpServletRequest.getPathInfo());
		usageLoggingBean.setHttpMethod(httpServletRequest.getMethod());

		if (resourceParameters != null) {
			usageLoggingBean.setResourceParameters(resourceParameters);
		}

		if (requestParameters != null) {
			usageLoggingBean.setRequestParameters(requestParameters);
		}

		usageLoggingBean.setDateAccessed(DateHelper.getTodaysDate());

		return usageLoggingBean;
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
		UsageLoggingBean usageLoggingBean = new UsageLoggingBean();
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);

		String applicationId = RequestUtils.getApplicationId(request);
		String requestUrl = (String) httpServletRequest
				.getParameter(RequestParamConstants.PARAM_REQUEST_URL);
		String resourceParameters = (String) httpServletRequest
				.getParameter(RequestParamConstants.PARAM_RESOURCE_PARAMETERS);
		String startDateAccessed = (String) httpServletRequest
				.getParameter(RequestParamConstants.PARAM_START_DATE_ACCESSED);
		String endDateAccessed = (String) httpServletRequest
				.getParameter(RequestParamConstants.PARAM_END_DATE_ACCESSED);

		Date startDate = RequestUtils.parseDateParam(startDateAccessed);
		Date endDate = RequestUtils.parseDateParam(endDateAccessed);

		if (applicationId != null) {
			usageLoggingBean.setApplicationId(applicationId);
		}

		if (requestUrl != null) {
			usageLoggingBean.setRequestUrl(requestUrl);
		}

		if (resourceParameters != null) {
			usageLoggingBean.setResourceParameters(resourceParameters);
		}

		if (startDate != null) {
			usageLoggingBean.setStartDateAccessed(startDate);
		}

		if (endDate != null) {
			usageLoggingBean.setEndDateAccessed(endDate);
		}

		return usageLoggingBean;
	}
}
