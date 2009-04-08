package org.ncbo.stanford.manager.obs.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.obs.ChildBean;
import org.ncbo.stanford.bean.obs.ParentBean;
import org.ncbo.stanford.bean.obs.PathBean;
import org.ncbo.stanford.bean.obs.SiblingBean;
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
	public List<ClassBean> findParents(String ontologyVersionId,
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
			handleError(response);
		}

		return parents;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findChildren(String ontologyVersionId,
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
			handleError(response);
		}

		return children;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findRootPaths(String ontologyVersionId,
			String conceptId) throws Exception {
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.rootpath.url")
						+ ontologyVersionId + "/" + conceptId, null);
		List<ClassBean> rootPaths = null;

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<PathBean> obsRootPaths = (ArrayList<PathBean>) xmlSerializationService
					.fromXML(data);
			rootPaths = new ArrayList<ClassBean>(0);

			for (PathBean obsRootPath : obsRootPaths) {
				ClassBean rootPath = new ClassBean();
				rootPath.setId(obsRootPath.getLocalConceptId());
				rootPath.addRelation(ApplicationConstants.PATH, obsRootPath
						.getPath());
				rootPaths.add(rootPath);
			}
		} else {
			handleError(response);
		}

		return rootPaths;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findSiblings(String ontologyVersionId,
			String conceptId) throws Exception {
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.siblings.url")
						+ ontologyVersionId + "/" + conceptId, null);
		List<ClassBean> siblings = null;

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<SiblingBean> obsSiblings = (ArrayList<SiblingBean>) xmlSerializationService
					.fromXML(data);
			siblings = new ArrayList<ClassBean>(0);

			for (SiblingBean obsSibling : obsSiblings) {
				ClassBean sibling = new ClassBean();
				sibling.setId(obsSibling.getLocalConceptId());
				sibling.addRelation(ApplicationConstants.LEVEL, obsSibling
						.getLevel());
				siblings.add(sibling);
			}
		} else {
			handleError(response);
		}

		return siblings;
	}

	private void handleError(AbstractResponseBean response) throws Exception {
		ErrorStatusBean error = (ErrorStatusBean) response;
		String message = error.getLongMessage();

		if (StringHelper.isNullOrNullString(message)) {
			message = error.getShortMessage();
		}

		throw new Exception(message);
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
