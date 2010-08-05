package org.ncbo.stanford.service.notes.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.eclipse.emf.ecore.xml.type.internal.DataValue.URI;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.AbstractConceptBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.notes.AppliesToBean;
import org.ncbo.stanford.bean.notes.NoteBean;
import org.ncbo.stanford.bean.notes.ProposalNewRelationshipBean;
import org.ncbo.stanford.bean.notes.ProposalNewTermBean;
import org.ncbo.stanford.bean.notes.ProposalPropertyValueChangeBean;
import org.ncbo.stanford.enumeration.NoteAppliesToTypeEnum;
import org.ncbo.stanford.exception.NoteNotFoundException;
import org.ncbo.stanford.manager.notes.NotesPool;
import org.ncbo.stanford.service.notes.NotesService;
import org.protege.notesapi.NotesConstants;
import org.protege.notesapi.NotesException;
import org.protege.notesapi.NotesManager;
import org.protege.notesapi.notes.AnnotatableThing;
import org.protege.notesapi.notes.Annotation;
import org.protege.notesapi.notes.LinguisticEntity;
import org.protege.notesapi.notes.NoteType;
import org.protege.notesapi.notes.ProposalChangeHierarchy;
import org.protege.notesapi.notes.ProposalChangePropertyValue;
import org.protege.notesapi.notes.ProposalCreateEntity;
import org.protege.notesapi.notes.Status;
import org.protege.notesapi.notes.StatusAnnotation;
import org.protege.notesapi.notes.impl.DefaultComment;
import org.protege.notesapi.notes.impl.DefaultProposal;
import org.protege.notesapi.oc.OntologyClass;
import org.protege.notesapi.oc.OntologyComponent;
import org.protege.notesapi.oc.OntologyProperty;
import org.protege.notesapi.oc.impl.DefaultOntologyClass;
import org.protege.notesapi.oc.impl.DefaultOntologyIndividual;
import org.protege.notesapi.oc.impl.DefaultOntologyProperty;

public class NotesServiceImpl implements NotesService {

	private NotesPool notesPool;

	public void archiveNote(OntologyBean ont, String noteId) {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);
		notesManager.archiveNote(noteId, ont.getId());
	}

	public void archiveThread(OntologyBean ont, String rootNoteId) {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);
		notesManager.archiveThread(rootNoteId, ont.getId());
	}

	public NoteBean createNote(OntologyBean ont, String appliesTo,
			NoteAppliesToTypeEnum appliesToType, NoteType noteType,
			String subject, String content, String author, String status,
			Long created) throws Exception {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);

		AnnotatableThing annotated = getAnnotatableThing(notesManager,
				appliesTo, appliesToType);

		Annotation newAnnotation = notesManager.createSimpleNote(noteType,
				subject, content, author, annotated, ont.getId());

		if (created != null)
			newAnnotation.setCreatedAt(created);

		// If status, create status object and set
		if (status != null) {
			newAnnotation.setHasStatus(getStatus(ont, notesManager, status));
		}

		return convertAnnotationToNoteBean(newAnnotation, ont);
	}

	public NoteBean createNewPropertyValueChangeProposal(OntologyBean ont,
			String appliesTo, NoteAppliesToTypeEnum appliesToType,
			NoteType noteType, String subject, String content, String author,
			String status, Long created, String reasonForChange,
			String contactInfo, String propertyNewValue,
			String propertyOldValue, String propertyId) throws NotesException {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);

		OntologyProperty property = notesManager
				.getOntologyProperty(propertyId);

		Annotation proposal = notesManager.createProposalChangePropertyValue(
				subject, content, property, propertyOldValue, propertyNewValue,
				reasonForChange, contactInfo);

		if (created != null)
			proposal.setCreatedAt(created);

		proposal.addAnnotates(getAnnotatableThing(notesManager, appliesTo,
				appliesToType));
		proposal.setAuthor(author);
		proposal.setCreatedInOntologyRevision(ont.getId());

		// If status, create status object and set
		if (status != null) {
			proposal.setHasStatus(getStatus(ont, notesManager, status));
		}

		return convertAnnotationToNoteBean(proposal, ont);
	}

	public NoteBean createNewRelationshipProposal(OntologyBean ont,
			String appliesTo, NoteAppliesToTypeEnum appliesToType,
			NoteType noteType, String subject, String content, String author,
			String status, Long created, String reasonForChange,
			String contactInfo, String relationshipType,
			String relationshipTarget, String relationshipOldTarget)
			throws NotesException {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);

		OntologyComponent target = notesManager
				.getOntologyClass(relationshipTarget);
		OntologyComponent oldTarget = notesManager
				.getOntologyClass(relationshipOldTarget);

		Collection<? extends OntologyComponent> targetCollection = Collections
				.singleton(target);
		Collection<? extends OntologyComponent> oldTargetCollection = Collections
				.singleton(oldTarget);

		Annotation proposal = notesManager.createProposalChangeHierarchy(
				subject, content, oldTargetCollection, targetCollection,
				relationshipType, reasonForChange, contactInfo);

		if (created != null)
			proposal.setCreatedAt(created);

		proposal.addAnnotates(getAnnotatableThing(notesManager, appliesTo,
				appliesToType));
		proposal.setAuthor(author);
		proposal.setCreatedInOntologyRevision(ont.getId());

		// If status, create status object and set
		if (status != null) {
			proposal.setHasStatus(getStatus(ont, notesManager, status));
		}

		return convertAnnotationToNoteBean(proposal, ont);
	}

	public NoteBean createNewTermProposal(OntologyBean ont, String appliesTo,
			NoteAppliesToTypeEnum appliesToType, NoteType noteType,
			String subject, String content, String author, String status,
			Long created, String reasonForChange, String contactInfo,
			String termDefinition, String termId, String termParent,
			String termPreferredName, List<String> termSynonyms)
			throws NotesException {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);

		LinguisticEntity preferredName = notesManager.createLinguisticEntity(
				termPreferredName, null);
		LinguisticEntity definition = notesManager.createLinguisticEntity(
				termDefinition, null);

		Collection<LinguisticEntity> synonymsList = new ArrayList<LinguisticEntity>();
		for (String synonym : termSynonyms) {
			synonymsList
					.add(notesManager.createLinguisticEntity(synonym, null));
		}

		OntologyClass ontClass = notesManager.getOntologyClass(termParent);
		Collection<? extends OntologyClass> parent = Collections
				.singleton(ontClass);

		Annotation proposal = notesManager.createProposalCreateClass(subject,
				content, termId, preferredName, synonymsList, definition,
				parent, reasonForChange, contactInfo);

		if (created != null)
			proposal.setCreatedAt(created);

		proposal.addAnnotates(getAnnotatableThing(notesManager, appliesTo,
				appliesToType));
		proposal.setAuthor(author);
		proposal.setCreatedInOntologyRevision(ont.getId());

		// If status, create status object and set
		if (status != null) {
			proposal.setHasStatus(getStatus(ont, notesManager, status));
		}

		return convertAnnotationToNoteBean(proposal, ont);
	}

	public void deleteNote(OntologyBean ont, String noteId) {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);
		notesManager.deleteNote(noteId);
	}

	public List<NoteBean> getAllNotesForOntology(OntologyBean ont,
			Boolean threaded, Boolean topLevelOnly) {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);

		Set<Annotation> annotations;
		if (threaded || topLevelOnly) {
			annotations = notesManager.getAllThreadStarterNotes();
		} else {
			annotations = notesManager.getAllNotes();
		}

		List<NoteBean> notes = new ArrayList<NoteBean>();

		for (Annotation annotation : annotations) {
			if (annotation != null) {
				if (threaded && !topLevelOnly) {
					notes
							.add(convertAnnotationToNoteBean(annotation, ont,
									true));
				} else {
					notes.add(convertAnnotationToNoteBean(annotation, ont));
				}
			}
		}

		return notes;
	}

	public List<NoteBean> getAllNotesForOntologyByAuthor(OntologyBean ont,
			Integer author) {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);
		Set<Annotation> annotations = notesManager.getAllNotesByAuthor(author
				.toString());

		List<NoteBean> notesList = new ArrayList<NoteBean>();
		for (Annotation annotation : annotations) {
			notesList.add(convertAnnotationToNoteBean(annotation, ont));
		}
		return notesList;
	}

	public List<NoteBean> getAllNotesForConcept(OntologyBean ont,
			ClassBean concept, Boolean threaded) {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);
		OntologyComponent oc = notesManager.getOntologyClass(getFullIdProper(
				concept, ont));
		Collection<Annotation> annotations = oc.getAssociatedAnnotations();

		List<NoteBean> notesList = new ArrayList<NoteBean>();
		for (Annotation annotation : annotations) {
			notesList
					.add(convertAnnotationToNoteBean(annotation, ont, threaded));
		}

		return notesList;
	}

	public List<NoteBean> getAllNotesForIndividual(OntologyBean ont,
			String instanceId, Boolean threaded) {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);
		OntologyComponent oc = notesManager.getOntologyIndividual(instanceId);
		Collection<Annotation> annotations = oc.getAssociatedAnnotations();

		List<NoteBean> notesList = new ArrayList<NoteBean>();
		for (Annotation annotation : annotations) {
			notesList
					.add(convertAnnotationToNoteBean(annotation, ont, threaded));
		}

		return notesList;
	}

	public List<NoteBean> getAllNotesForNote(OntologyBean ont, String noteId,
			Boolean threaded) throws NoteNotFoundException {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);

		Annotation root = notesManager.getNote(noteId);

		if (root == null) {
			throw new NoteNotFoundException();
		}

		List<NoteBean> notesList = new ArrayList<NoteBean>();
		notesList.add(convertAnnotationToNoteBean(root, ont, threaded));

		return notesList;
	}

	public Annotation getNote(OntologyBean ont, String iri)
			throws NoteNotFoundException {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);

		Annotation note = notesManager.getNote(iri);

		if (note == null) {
			throw new NoteNotFoundException();
		}

		return note;
	}

	public NoteBean getNoteBean(OntologyBean ont, String noteId)
			throws NoteNotFoundException {
		return convertAnnotationToNoteBean(getNote(ont, noteId), ont);
	}

	public NoteBean getRootNote(OntologyBean ont, NoteBean note)
			throws NoteNotFoundException {
		while (note.getAppliesToList().get(0).getType()
				.equalsIgnoreCase("Note")) {
			note = getRootNote(ont, convertAnnotationToNoteBean(getNote(ont,
					note.getAppliesToList().get(0).getId()), ont));
		}
		return note;
	}

	public void unarchiveNote(OntologyBean ont, String noteId) {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);
		notesManager.unarchiveNote(noteId);
	}

	public void unarchiveThread(OntologyBean ont, String rootNoteId) {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);
		notesManager.unarchiveThread(rootNoteId);
	}

	public NoteBean updateNote(OntologyBean ont, String noteId,
			NoteType noteType, String subject, String content, String author,
			Long created, String status, String appliesTo,
			NoteAppliesToTypeEnum appliesToType) throws Exception {
		NotesManager notesManager = notesPool.getNotesManagerForOntology(ont);
		Annotation annotation = notesManager.getNote(noteId);

		// If status, create status object and set
		if (status != null) {
			annotation.setHasStatus(getStatus(ont, notesManager, status));
		}

		if (content != null)
			annotation.setBody(content);
		if (author != null)
			annotation.setAuthor(author);
		if (subject != null)
			annotation.setSubject(subject);
		if (created != null)
			annotation.setCreatedAt(created);

		if (appliesTo != null && appliesToType != null) {
			AnnotatableThing annotated = getAnnotatableThing(notesManager,
					appliesTo, appliesToType);
			annotation.setAnnotates(Collections.singleton(annotated));
		}

		return convertAnnotationToNoteBean(annotation, ont);
	}

	/**
	 * @param notesPool
	 *            the notesPool to set
	 */
	public void setNotesPool(org.ncbo.stanford.manager.notes.NotesPool notesPool) {
		this.notesPool = notesPool;
	}

	// Private helper methods

	private AnnotatableThing getAnnotatableThing(NotesManager notesManager,
			String appliesTo, NoteAppliesToTypeEnum appliesToType) {
		AnnotatableThing annotated = null;
		switch (appliesToType) {
		case Ontology:
			annotated = notesManager.getOntologyComponent(appliesTo);
			break;
		case Class:
			annotated = notesManager.getOntologyClass(appliesTo);
			break;
		case Property:
			annotated = notesManager.getOntologyProperty(appliesTo);
			break;
		case Individual:
			annotated = notesManager.getOntologyProperty(appliesTo);
			break;
		case Note:
			annotated = notesManager.getNote(appliesTo);
			break;
		default:
			annotated = notesManager.getOntologyComponent(appliesTo);
		}

		return annotated;
	}

	/**
	 * Method to convert Annotation to NoteBean.
	 * 
	 * @param annotation
	 *            The annotation to convert.
	 * @return
	 */
	private NoteBean convertAnnotationToNoteBean(Annotation annotation,
			OntologyBean ont) {
		return convertAnnotationToNoteBean(annotation, ont, false);
	}

	/**
	 * Converts an Annotation object to a NoteBean. Includes options for
	 * threading.
	 * 
	 * @param annotation
	 *            The annotation to convert.
	 * @param threaded
	 *            Boolean for whether or not the method should recurse.
	 * @param root
	 *            Boolean indicating whether this is the root note.
	 * @return
	 */
	private NoteBean convertAnnotationToNoteBean(Annotation annotation,
			OntologyBean ont, Boolean threaded) {
		NoteBean nb = new NoteBean();

		Collection<AnnotatableThing> annotates = annotation.getAnnotates();
		for (AnnotatableThing appliesTo : annotates) {
			Class<? extends AnnotatableThing> clas = appliesTo.getClass();
			if (appliesTo instanceof DefaultComment
					|| appliesTo instanceof DefaultProposal) {
				nb.addAppliesToList(new AppliesToBean(appliesTo.getId(),
						NoteAppliesToTypeEnum.Note));
			} else if (clas == DefaultOntologyClass.class) {
				nb.addAppliesToList(new AppliesToBean(appliesTo.getId(),
						NoteAppliesToTypeEnum.Class));
			} else if (clas == DefaultOntologyIndividual.class) {
				nb.addAppliesToList(new AppliesToBean(appliesTo.getId(),
						NoteAppliesToTypeEnum.Individual));
			} else if (clas == DefaultOntologyProperty.class) {
				nb.addAppliesToList(new AppliesToBean(appliesTo.getId(),
						NoteAppliesToTypeEnum.Property));
			} else {
				nb.addAppliesToList(new AppliesToBean(appliesTo.getId(),
						NoteAppliesToTypeEnum.Default));
			}
		}

		nb.setOntologyId(ont.getOntologyId());
		if (annotation.getAuthor() != null) {
			nb.setAuthor(Integer.parseInt(annotation.getAuthor()));
		}

		if (annotation.getHasStatus() != null)
			nb.setStatus(annotation.getHasStatus().getDescription());

		nb.setBody(annotation.getBody());
		nb.setCreated(annotation.getCreatedAt());
		nb.setId(annotation.getId());
		nb.setSubject(annotation.getSubject());
		nb.setType(annotation.getType().toString());
		nb.setUpdated(annotation.getModifiedAt());
		nb.setArchived(annotation.getArchived());
		nb.setArchivedInOntologyVersion(annotation
				.getArchivedInOntologyRevision());
		nb.setCreatedInOntologyVersion(annotation
				.getCreatedInOntologyRevision());

		// Add non-common values
		nb.setValues(convertAdditionalProperties(ont, annotation));

		// Convert associated annotations
		if (annotation.hasAssociatedAnnotations() && threaded == true) {
			Collection<Annotation> associated = annotation
					.getAssociatedAnnotations();
			for (Annotation subAnnotation : associated) {
				nb.addAssociated(convertAnnotationToNoteBean(subAnnotation,
						ont, threaded));
			}
		}

		return nb;
	}

	/**
	 * Creates key/value pairs for non-common properties for the NoteBean.
	 * 
	 * @param ont
	 * @param annotation
	 * @return
	 */
	private List<Object> convertAdditionalProperties(OntologyBean ont,
			Annotation annotation) {
		ArrayList<Object> valueStore = new ArrayList<Object>();

		switch (annotation.getType()) {
		case Comment:
			valueStore = null;
			break;
		case ProposalForCreateEntity:
			ProposalCreateEntity newTermAnnot = (ProposalCreateEntity) annotation;
			ProposalNewTermBean newTerm = new ProposalNewTermBean();
			newTerm.setDefinition(newTermAnnot.getDefinition().getLabel());
			newTerm.setId(newTermAnnot.getEntityId());
			newTerm
					.setPreferredName(newTermAnnot.getPreferredName()
							.getLabel());
			newTerm.setReasonForChange(newTermAnnot.getReasonForChange());
			newTerm.setContactInfo(newTermAnnot.getContactInformation());

			ArrayList<String> parents = new ArrayList<String>();
			for (OntologyComponent parent : newTermAnnot.getParents()) {
				parents.add(parent.getId());
			}
			newTerm.setParent(parents);

			ArrayList<String> synonyms = new ArrayList<String>();
			for (LinguisticEntity synonym : newTermAnnot.getSynonym()) {
				synonyms.add(synonym.getLabel());
			}
			newTerm.setSynonyms(synonyms);

			valueStore.add(newTerm);
			break;
		case ProposalForChangeHierarchy:
			ProposalChangeHierarchy changeHierarchyAnnot = (ProposalChangeHierarchy) annotation;
			ProposalNewRelationshipBean newChangeHierarchy = new ProposalNewRelationshipBean();
			newChangeHierarchy.setRelationshipType(changeHierarchyAnnot
					.getRelationType());
			newChangeHierarchy.setReasonForChange(changeHierarchyAnnot
					.getReasonForChange());
			newChangeHierarchy.setContactInfo(changeHierarchyAnnot
					.getContactInformation());

			ArrayList<String> relationshipTarget = new ArrayList<String>();
			for (OntologyComponent target : changeHierarchyAnnot
					.getNewParents()) {
				relationshipTarget.add(target.getId());
			}
			newChangeHierarchy.setRelationshipTarget(relationshipTarget);

			ArrayList<String> oldRelationshipTarget = new ArrayList<String>();
			for (OntologyComponent oldTarget : changeHierarchyAnnot
					.getOldParents()) {
				oldRelationshipTarget.add(oldTarget.getId());
			}
			newChangeHierarchy.setOldRelationshipTarget(oldRelationshipTarget);

			valueStore.add(newChangeHierarchy);
			break;
		case ProposalForChangePropertyValue:
			ProposalChangePropertyValue propValueChangeAnnot = (ProposalChangePropertyValue) annotation;
			ProposalPropertyValueChangeBean newValueChange = new ProposalPropertyValueChangeBean();
			newValueChange.setNewValue(propValueChangeAnnot.getNewValue());
			newValueChange.setOldValue(propValueChangeAnnot.getOldValue());
			newValueChange.setPropertyId(propValueChangeAnnot.getProperty()
					.getId());
			newValueChange.setReasonForChange(propValueChangeAnnot
					.getReasonForChange());
			newValueChange.setContactInfo(propValueChangeAnnot
					.getContactInformation());
			valueStore.add(newValueChange);
			break;
		}
		return valueStore;
	}

	/**
	 * Get an id based on the ontology type. OBO ontologies return the short id,
	 * everything else uses the fullId.
	 * 
	 * This method exists in these classes: NotesServiceImpl.java
	 * NotesRestlet.java
	 * 
	 * @param concept
	 * @param ont
	 * @return
	 */
	private String getFullIdProper(AbstractConceptBean concept, OntologyBean ont) {
		// This is a quick-and-dirty method to get the 'proper' id for a given
		// concept. We're doing this because OBO ontologies aren't yet using a
		// URI as their term ids. Once they start, we should remove this and
		// just use the fullId.
		if (ont.getFormat().equalsIgnoreCase("OBO")
				|| ont.getFormat().equalsIgnoreCase("RRF")
				|| ont.getFormat().equalsIgnoreCase("LEXGRID-XML")
				|| ont.getFormat().equalsIgnoreCase("META")) {
			return concept.getId();
		} else {
			return concept.getFullId();
		}
	}

	/**
	 * This method compares the provided status with known statuses in order to
	 * re-use existing statuses in ChAO but also provide ontology authors with
	 * the ability to use arbitrary ones.
	 * 
	 * @param status
	 * @return
	 * @throws NotesException
	 */
	private Status getStatus(OntologyBean ont, NotesManager notesManager,
			String status) throws NotesException {
		// Check to see if we're asking for a ChAO-provided status and return if
		// found
		Collection<StatusAnnotation> chaoStatuses = notesManager
				.getAllChaoNoteStatuses();
		for (StatusAnnotation chaoStatus : chaoStatuses) {
			if (chaoStatus.getDescription().equalsIgnoreCase(status))
				return chaoStatus;
		}
		
		// Otherwise, generate a BioPortal ontology-specific status
		String statusFormatted = URI.encode(WordUtils.uncapitalize(status));
		String nameIRI = "http://bioportal.bioontology.org/ontologies/virtual/"
				+ ont.getOntologyId() + "/note_status#" + statusFormatted;
		return notesManager.createStatus(nameIRI, WordUtils.capitalizeFully(status));
	}

}
