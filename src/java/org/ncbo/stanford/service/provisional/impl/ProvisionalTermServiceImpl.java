package org.ncbo.stanford.service.provisional.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.ProvisionalTermBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.ClassBeanResultListBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.ProvisionalTermExistsException;
import org.ncbo.stanford.exception.ProvisionalTermMissingException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.provisional.ProvisionalTermService;
import org.ncbo.stanford.sparql.bean.ProvisionalTerm;
import org.ncbo.stanford.sparql.dao.provisional.ProvisionalTermDAO;
import org.ncbo.stanford.util.paginator.Paginator;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.paginator.impl.PaginatorImpl;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import com.mysql.jdbc.StringUtils;

public class ProvisionalTermServiceImpl implements ProvisionalTermService {

	private ProvisionalTermDAO provisionalTermDAO;
	private ConceptService conceptService;
	private OntologyService ontologyService;

	@Override
	public ClassBean createProvisionalTerm(List<Integer> ontologyIds,
			String label, List<String> synonyms, String definition,
			URI provisionalSubclassOf, Date created, Date updated,
			Integer submittedBy, String noteId, String status, URI permanentId)
			throws ProvisionalTermExistsException {

		if (created == null)
			created = new Date();

		ProvisionalTerm newTerm = populateProvisionalTerm(ontologyIds, label,
				synonyms, definition, provisionalSubclassOf, created, updated,
				submittedBy, noteId, status, permanentId);

		newTerm = provisionalTermDAO.createProvisionalTerm(newTerm);

		return convertProvisionalTermToClassBean(newTerm);
	}

	@Override
	public void deleteProvisionalTerm(String id)
			throws ProvisionalTermMissingException {
		provisionalTermDAO.deleteProvisionalTerm(new URIImpl(id));
	}

	@Override
	public ClassBean getProvisionalTerm(String id)
			throws ProvisionalTermMissingException {
		return convertProvisionalTermToClassBean(provisionalTermDAO
				.getProvisionalTerm(new URIImpl(id)));
	}

	@Override
	public Page<ClassBean> getAllProvisionalTerms(Integer pageSize,
			Integer pageNum, SPARQLFilterGenerator parameters)
			throws InvalidInputException {
		if (pageNum == null || pageNum <= 0) {
			pageNum = 1;
		}

		if (pageSize == null || pageSize <= 0) {
			pageSize = 100;
		}

		ClassBeanResultListBean pageTerms = new ClassBeanResultListBean(0);
		Integer totalResults = provisionalTermDAO.getCount(null, parameters);

		Paginator<ClassBean> p = new PaginatorImpl<ClassBean>(pageTerms,
				pageSize, totalResults);

		int offset = pageNum * pageSize - pageSize;

		List<ProvisionalTerm> terms = provisionalTermDAO.getProvisionalTerms(
				pageSize, offset, null, parameters);

		for (ProvisionalTerm term : terms) {
			pageTerms.add(convertProvisionalTermToClassBean(term));
		}

		return p.getCurrentPage(pageNum);
	}

	@Override
	public ClassBean updateProvisionalTerm(String id, ProvisionalTermBean term)
			throws ProvisionalTermMissingException {
		URI URIid = new URIImpl(id);
		ProvisionalTerm updatedTerm = provisionalTermDAO.updateProvisionalTerm(
				URIid, term.getOntologyIds(), term.getLabel(), term
						.getSynonyms(), term.getDefinition(), term
						.getProvisionalSubclassOf(), term.getCreated(), term
						.getUpdated(), term.getSubmittedBy(), term.getNoteId(),
				term.getStatus(), term.getPermanentId());
		return convertProvisionalTermToClassBean(updatedTerm);
	}

	@Override
	public ClassBean updateProvisionalTerm(String id,
			List<Integer> ontologyIds, String label, List<String> synonyms,
			String definition, URI provisionalSubclassOf, Date created,
			Date updated, Integer submittedBy, String noteId, String status,
			URI permanentId) throws ProvisionalTermMissingException {
		URI URIid = new URIImpl(id);
		ProvisionalTerm updatedTerm = provisionalTermDAO.updateProvisionalTerm(
				URIid, ontologyIds, label, synonyms, definition,
				provisionalSubclassOf, created, updated, submittedBy, noteId,
				status, permanentId);
		return convertProvisionalTermToClassBean(updatedTerm);
	}

	private ProvisionalTerm populateProvisionalTerm(List<Integer> ontologyIds,
			String label, List<String> synonyms, String definition,
			URI provisionalSubclassOf, Date created, Date updated,
			Integer submittedBy, String noteId, String status, URI permanentId) {
		ProvisionalTerm term = new ProvisionalTerm();

		if (ontologyIds != null)
			term.setOntologyIds(ontologyIds);
		if (label != null)
			term.setLabel(label);
		if (synonyms != null)
			term.setSynonyms(synonyms);
		if (definition != null)
			term.setDefinition(definition);
		if (provisionalSubclassOf != null)
			term.setProvisionalSubclassOf(provisionalSubclassOf);
		if (created != null)
			term.setCreated(created);
		if (updated != null)
			term.setUpdated(updated);
		if (submittedBy != null)
			term.setSubmittedBy(submittedBy);
		if (noteId != null)
			term.setNoteId(noteId);
		if (status != null)
			term.setStatus(status);
		if (permanentId != null)
			term.setPermanentId(permanentId);

		return term;
	}

	private ClassBean convertProvisionalTermToClassBean(ProvisionalTerm term) {
		ClassBean concept = new ClassBean();

		if (term.getPermanentId() == null
				|| StringUtils.isNullOrEmpty(term.getPermanentId().toString())
				&& term.getOntologyIds().size() > 1) {
			populateClassBeanFromTerm(concept, term);
		} else {
			// TODO: Should we be storing the implementedTermOntologyId? For now
			// only end up here if there is one ontology id associated with a
			// provisional id.
			OntologyBean ont;
			try {
				ont = ontologyService
						.findLatestActiveOntologyOrViewVersion(term
								.getOntologyIds().get(0));
				concept = conceptService
						.findConcept(ont.getId(), term.getPermanentId()
								.toString(), null, false, false, false);

				if (concept == null)
					throw new Exception();

				// Add provisional info to relations in classBean
				concept.addRelation("provisionalId", term.getId().toString());
				concept.addRelation("provisionalFullId", term.getId()
						.toString());
				concept.addRelation("provisionalSynonyms", term.getSynonyms());
				concept
						.addRelation("provisionalPreferredName", term
								.getLabel());

				List<String> definitions = new ArrayList<String>();
				definitions.add(term.getDefinition());
				concept.addRelation("provisionalDefinition", definitions);

			} catch (Exception e) {
				// Try to recover and display a classBean anyway
				concept = new ClassBean();
				populateClassBeanFromTerm(concept, term);
			}
		}

		// Add provisional-specific info to the relations
		concept.addRelation("provisionalSubmittedBy", term.getSubmittedBy());
		concept.addRelation("provisionalSubclassOf", term
				.getProvisionalSubclassOf());
		concept.addRelation("provisionalCreated", term.getCreated());
		concept.addRelation("provisionalUpdated", term.getUpdated());
		concept.addRelation("provisionalRelatedOntologyIds", term.getOntologyIds());
		concept.addRelation("provisionalRelatedNoteId", term.getNoteId());
		concept.addRelation("provisionalTermStatus", term.getStatus());

		String permanentId = (term.getPermanentId() == null) ? null : term
				.getPermanentId().toString();
		concept.addRelation("provisionalPermanentId", permanentId);

		return concept;
	}

	private ClassBean populateClassBeanFromTerm(ClassBean concept,
			ProvisionalTerm term) {
		concept.setId(term.getId().toString());
		concept.setFullId(term.getId().toString());
		concept.setSynonyms(term.getSynonyms());
		concept.setLabel(term.getLabel());

		List<String> definitions = new ArrayList<String>();
		definitions.add(term.getDefinition());
		concept.setDefinitions(definitions);

		return concept;
	}

	public void setProvisionalTermDAO(ProvisionalTermDAO provisionalTermDAO) {
		this.provisionalTermDAO = provisionalTermDAO;
	}

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

}
