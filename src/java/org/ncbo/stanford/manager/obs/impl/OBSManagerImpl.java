package org.ncbo.stanford.manager.obs.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.obs.ChildBean;
import org.ncbo.stanford.bean.obs.ParentBean;
import org.ncbo.stanford.bean.response.AbstractResponseBean;
import org.ncbo.stanford.bean.response.ErrorStatusBean;
import org.ncbo.stanford.bean.response.SuccessBean;
import org.ncbo.stanford.manager.obs.OBSManager;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

public class OBSManagerImpl implements OBSManager {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(OBSManagerImpl.class);
	private XMLSerializationService xmlSerializationService;

	@SuppressWarnings("unchecked")
	public List<ClassBean> findParents(Integer ontologyVersionId,
			String conceptId) throws Exception {
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.parents.url")
						+ ontologyVersionId + "/" + conceptId, null);
		List<ClassBean> parents = null;

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<ParentBean> obsParents = (ArrayList<ParentBean>) xmlSerializationService
					.fromXML(data);
			parents = new ArrayList<ClassBean>(0);

			for (ParentBean obsParent : obsParents) {
				ClassBean parent = new ClassBean();
				parent.setId(obsParent.getParentLocalConceptId());
				parent.addRelation(ApplicationConstants.LEVEL, obsParent
						.getLevel());
				parents.add(parent);
			}
		} else {
			ErrorStatusBean error = (ErrorStatusBean) response;
			String message = error.getLongMessage();

			if (StringHelper.isNullOrNullString(message)) {
				message = error.getShortMessage();
			}

			throw new Exception(message);
		}

		return parents;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findChildren(Integer ontologyVersionId,
			String conceptId) throws Exception {
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.children.url")
						+ ontologyVersionId + "/" + conceptId, null);
		List<ClassBean> children = null;

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<ChildBean> obsChildren = (ArrayList<ChildBean>) xmlSerializationService
					.fromXML(data);
			children = new ArrayList<ClassBean>(0);

			for (ChildBean obsChild : obsChildren) {
				ClassBean child = new ClassBean();
				child.setId(obsChild.getLocalConceptId());
				child.addRelation(ApplicationConstants.LEVEL, obsChild
						.getLevel());
				children.add(child);
			}
		} else {
			ErrorStatusBean error = (ErrorStatusBean) response;
			String message = error.getLongMessage();

			if (StringHelper.isNullOrNullString(message)) {
				message = error.getShortMessage();
			}

			throw new Exception(message);
		}

		return children;
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		this.xmlSerializationService = xmlSerializationService;
	}
}
