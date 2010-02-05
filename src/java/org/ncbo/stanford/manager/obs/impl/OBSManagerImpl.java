package org.ncbo.stanford.manager.obs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.obs.AbstractConceptBean;
import org.ncbo.stanford.bean.obs.ConceptBean;
import org.ncbo.stanford.bean.obs.OntologyBean;
import org.ncbo.stanford.bean.obs.PathBean;
import org.ncbo.stanford.bean.obs.RelationBean;
import org.ncbo.stanford.bean.obs.SemanticTypeBean;
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

	public Integer findLatestOntologyVersion(Integer ontologyId)
			throws Exception {
		Integer ontologyVersionId = null;
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.virtual.ontology.url")
						+ ontologyId, null);

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			OntologyBean obsOntology = (OntologyBean) xmlSerializationService
					.fromXML(data);
			ontologyVersionId = obsOntology.getLocalOntologyId();
		} else {
			handleError(response);
		}

		return ontologyVersionId;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findAllConcepts(Integer ontologyVersionId,
			Integer offset, Integer limit) throws Exception {
		List<ClassBean> allConcepts = null;
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.allconcepts.url")
						+ ontologyVersionId, assembleGetParameters(null,
						offset, limit));

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<ConceptBean> obsAllConcepts = (ArrayList<ConceptBean>) xmlSerializationService
					.fromXML(data);
			allConcepts = new ArrayList<ClassBean>(0);

			for (ConceptBean obsConcept : obsAllConcepts) {
				ClassBean concept = new ClassBean();
				populateBaseClassBean(obsConcept, concept);
				concept.setLabel(obsConcept.getPreferredName());
				concept.setIsTopLevel(obsConcept.getIsTopLevel());
				List<String> synonyms = obsConcept.getSynonyms();

				if (synonyms != null && !synonyms.isEmpty()) {
					concept.setSynonyms(obsConcept.getSynonyms());
				}

				concept.addRelation(ApplicationConstants.SEMANTIC_TYPES,
						obsConcept.getSemanticTypes());
				allConcepts.add(concept);
			}
		} else {
			handleError(response);
		}

		return allConcepts;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findParents(Integer ontologyVersionId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception {
		List<ClassBean> parents = null;
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.parents.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(level, offset, limit));

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<RelationBean> obsParents = (ArrayList<RelationBean>) xmlSerializationService
					.fromXML(data);
			parents = new ArrayList<ClassBean>(0);

			for (RelationBean obsParent : obsParents) {
				ClassBean parent = new ClassBean();
				populateBaseClassBean(obsParent.getConcept(), parent);
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
	public List<ClassBean> findChildren(Integer ontologyVersionId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception {
		List<ClassBean> children = null;
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.children.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(level, offset, limit));

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<RelationBean> obsChildren = (ArrayList<RelationBean>) xmlSerializationService
					.fromXML(data);
			children = new ArrayList<ClassBean>(0);

			for (RelationBean obsChild : obsChildren) {
				ClassBean child = new ClassBean();
				populateBaseClassBean(obsChild.getConcept(), child);
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
	public Set<String> findChildrenConceptIds(Integer ontologyVersionId,
			String conceptId, Integer level, Integer offset, Integer limit)
			throws Exception {
		Set<String> childrenConceptIds = null;
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.children.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(level, offset, limit));

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<RelationBean> obsChildren = (ArrayList<RelationBean>) xmlSerializationService
					.fromXML(data);
			childrenConceptIds = new HashSet<String>(0);

			for (RelationBean obsChild : obsChildren) {
				List<String> parsedConceptId = parseOBSConceptId(obsChild
						.getConcept().getLocalConceptId());
				childrenConceptIds.add(parsedConceptId.get(0));
			}
		} else {
			handleError(response);
		}

		return childrenConceptIds;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findRootPaths(Integer ontologyVersionId,
			String conceptId, Integer offset, Integer limit) throws Exception {
		List<ClassBean> rootPaths = null;
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.rootpath.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(null, offset, limit));

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<PathBean> obsRootPaths = (ArrayList<PathBean>) xmlSerializationService
					.fromXML(data);
			rootPaths = new ArrayList<ClassBean>(0);

			for (PathBean obsRootPath : obsRootPaths) {
				ClassBean rootPath = new ClassBean();
				rootPath.setOntologyVersionId(ontologyVersionId.toString());
				rootPath.setId(conceptId);
				List<ConceptBean> conceptBeans = obsRootPath.getConceptBeans();
				rootPath.addRelation(ApplicationConstants.PATH,
						extractPath(conceptBeans));
				rootPaths.add(rootPath);
			}
		} else {
			handleError(response);
		}

		return rootPaths;
	}

	@SuppressWarnings("unchecked")
	public List<ClassBean> findSiblings(Integer ontologyVersionId,
			String conceptId, Integer level, Integer offset) throws Exception {
		List<ClassBean> siblings = null;
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.siblings.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(level, offset, null));

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<RelationBean> obsSiblings = (ArrayList<RelationBean>) xmlSerializationService
					.fromXML(data);
			siblings = new ArrayList<ClassBean>(0);

			for (RelationBean obsSibling : obsSiblings) {
				ClassBean sibling = new ClassBean();
				populateBaseClassBean(obsSibling.getConcept(), sibling);
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
	public List<ClassBean> findLeaves(Integer ontologyVersionId,
			String conceptId, Integer offset, Integer limit) throws Exception {
		List<ClassBean> leaves = null;
		AbstractResponseBean response = xmlSerializationService.processGet(
				MessageUtils.getMessage("obs.rest.leafpath.url")
						+ ontologyVersionId + "/" + conceptId,
				assembleGetParameters(null, offset, limit));

		if (response.isResponseSuccess()) {
			String data = ((SuccessBean) response).getDataXml();
			List<PathBean> obsLeaves = (ArrayList<PathBean>) xmlSerializationService
					.fromXML(data);
			leaves = new ArrayList<ClassBean>(0);

			for (PathBean obsLeaf : obsLeaves) {
				ClassBean leaf = new ClassBean();
				leaf.setOntologyVersionId(ontologyVersionId.toString());
				leaf.setId(conceptId);
				List<ConceptBean> conceptBeans = obsLeaf.getConceptBeans();
				leaf.addRelation(ApplicationConstants.PATH,
						extractPath(conceptBeans));
				leaves.add(leaf);
			}
		} else {
			handleError(response);
		}

		return leaves;
	}

	private String extractPath(List<ConceptBean> paths) {
		String strPath = "";

		for (ConceptBean path : paths) {
			strPath += path.getLocalConceptId().replaceAll(
					OBS_ONTOLOGY_ID_PATTERN, "") + '.';
		}

		if (strPath.length() > 0) {
			strPath = strPath.substring(0, strPath.length() - 1);
		}

		return strPath;
	}

	/**
	 * @param xmlSerializationService
	 *            the xmlSerializationService to set
	 */
	public void setXmlSerializationService(
			XMLSerializationService xmlSerializationService) {
		// OBS Wrapper Aliases
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.ontologybean"), OntologyBean.class);
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.conceptbean"), ConceptBean.class);
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.semantictypebean"),
				SemanticTypeBean.class);
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.relationbean"), RelationBean.class);
		xmlSerializationService.alias(MessageUtils
				.getMessage("entity.obs.pathbean"), PathBean.class);
		this.xmlSerializationService = xmlSerializationService;
	}

	private void populateBaseClassBean(AbstractConceptBean obsBean,
			ClassBean classBean) {
		String fullConceptId = obsBean.getLocalConceptId();
		List<String> parsedConceptId = parseOBSConceptId(fullConceptId);

		if (parsedConceptId.size() > 1) {
			classBean.setOntologyVersionId(parsedConceptId.get(1));
			classBean.setId(parsedConceptId.get(0));
		} else {
			classBean.setId(fullConceptId);
		}
	}

	private List<String> parseOBSConceptId(String fullConceptId) {
		List<String> obsConceptId = new ArrayList<String>(0);
		Pattern mask = Pattern.compile(OBS_CONCEPT_ID_PATTERN);
		Matcher matcher = mask.matcher(fullConceptId);
		matcher.find();

		if (matcher.matches() && matcher.groupCount() > 1) {
			// the first element = concept id
			// the second element = ontology version id
			obsConceptId.add(matcher.group(2));
			obsConceptId.add(matcher.group(1));
		} else {
			obsConceptId.add(fullConceptId);
		}

		return obsConceptId;
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
