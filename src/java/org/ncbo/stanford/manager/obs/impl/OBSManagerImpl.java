package org.ncbo.stanford.manager.obs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.obs.AbstractConceptBean;
import org.ncbo.stanford.bean.obs.ChildBean;
import org.ncbo.stanford.bean.obs.ConceptBean;
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
import org.ncbo.stanford.view.util.constants.RequestParamConstants;

public class OBSManagerImpl implements OBSManager {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(OBSManagerImpl.class);

	private static final String OBS_ONTOLOGY_ID_PATTERN = "\\w+/";
	private static final String OBS_CONCEPT_ID_PATTERN = "^(.+)/(.+)$";

	private XMLSerializationService xmlSerializationService;

	@SuppressWarnings("unchecked")
	public List<ClassBean> findParents(String ontologyVersionId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception {
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.parents.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(level, offset, limit));
		List<ClassBean> parents = null;

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<ParentBean> obsParents = (ArrayList<ParentBean>) xmlSerializationService
					.fromXML(data);
			parents = new ArrayList<ClassBean>(0);

			for (ParentBean obsParent : obsParents) {
				ClassBean parent = new ClassBean();
				populateBaseClassBean(obsParent, parent);
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
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception {
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.children.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(level, offset, limit));
		List<ClassBean> children = null;

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<ChildBean> obsChildren = (ArrayList<ChildBean>) xmlSerializationService
					.fromXML(data);
			children = new ArrayList<ClassBean>(0);

			for (ChildBean obsChild : obsChildren) {
				ClassBean child = new ClassBean();
				populateBaseClassBean(obsChild, child);
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
			String conceptId, Integer offset, Integer limit) throws Exception {
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.rootpath.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(null, offset, limit));
		List<ClassBean> rootPaths = null;

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<PathBean> obsRootPaths = (ArrayList<PathBean>) xmlSerializationService
					.fromXML(data);
			rootPaths = new ArrayList<ClassBean>(0);

			for (PathBean obsRootPath : obsRootPaths) {
				ClassBean rootPath = new ClassBean();
				populateBaseClassBean(obsRootPath, rootPath);
				rootPath.addRelation(ApplicationConstants.PATH, obsRootPath
						.getPath().replaceAll(OBS_ONTOLOGY_ID_PATTERN, ""));
				rootPaths.add(rootPath);
			}
		} else {
			handleError(response);
		}

		return rootPaths;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findSiblings(String ontologyVersionId,
			String conceptId, Integer level, Integer offset) throws Exception {
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.siblings.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(level, offset, null));
		List<ClassBean> siblings = null;

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<SiblingBean> obsSiblings = (ArrayList<SiblingBean>) xmlSerializationService
					.fromXML(data);
			siblings = new ArrayList<ClassBean>(0);

			for (SiblingBean obsSibling : obsSiblings) {
				ClassBean sibling = new ClassBean();
				populateBaseClassBean(obsSibling, sibling);
				sibling.addRelation(ApplicationConstants.LEVEL, obsSibling
						.getLevel());
				siblings.add(sibling);
			}
		} else {
			handleError(response);
		}

		return siblings;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findLeaves(String ontologyVersionId,
			String conceptId, Integer offset, Integer limit) throws Exception {
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.leafpath.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(null, offset, limit));
		List<ClassBean> leaves = null;

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<PathBean> obsLeaves = (ArrayList<PathBean>) xmlSerializationService
					.fromXML(data);
			leaves = new ArrayList<ClassBean>(0);

			for (PathBean obsLeaf : obsLeaves) {
				ClassBean leaf = new ClassBean();
				populateBaseClassBean(obsLeaf, leaf);
				leaf.addRelation(ApplicationConstants.PATH, obsLeaf.getPath()
						.replaceAll(OBS_ONTOLOGY_ID_PATTERN, ""));
				leaves.add(leaf);
			}
		} else {
			handleError(response);
		}

		return leaves;
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		// OBS Wrapper Aliases
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.conceptbean"), ConceptBean.class);
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.parentbean"), ParentBean.class);
		xmlSerializationService.aliasField(MessageUtils
				.getMessage("property.obs.parentlocalconceptid"),
				ParentBean.class, MessageUtils
						.getMessage("property.obs.localconceptid"));
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.childbean"), ChildBean.class);
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.pathbean"), PathBean.class);
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.siblingbean"), SiblingBean.class);
		this.xmlSerializationService = xmlSerializationService;
	}

	private void populateBaseClassBean(AbstractConceptBean obsBean,
			ClassBean classBean) {
		String fullConceptId = obsBean.getLocalConceptId();
		Pattern mask = Pattern.compile(OBS_CONCEPT_ID_PATTERN);
		Matcher matcher = mask.matcher(fullConceptId);
		matcher.find();

		if (matcher.matches() && matcher.groupCount() > 1) {
			classBean.setOntologyVersionId(matcher.group(1));
			classBean.setId(matcher.group(2));
		} else {
			classBean.setId(fullConceptId);
		}
	}

	private void handleError(AbstractResponseBean response) throws Exception {
		ErrorStatusBean error = (ErrorStatusBean) response;
		String message = error.getLongMessage();

		if (StringHelper.isNullOrNullString(message)) {
			message = error.getShortMessage();
		}

		throw new Exception(message);
	}

	private HashMap<String, String> assembleGetParameters(Integer level,
			Integer offset, Integer limit) {
		HashMap<String, String> getParams = new HashMap<String, String>(0);

		if (level != null) {
			getParams.put(RequestParamConstants.PARAM_LEVEL, level.toString());
		}

		if (offset != null) {
			getParams
					.put(RequestParamConstants.PARAM_OFFSET, offset.toString());
		}

		if (limit != null) {
			getParams.put(RequestParamConstants.PARAM_LIMIT, limit.toString());
		}

		return getParams;
	}
}
