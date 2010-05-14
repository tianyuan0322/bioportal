package org.ncbo.stanford.service.notes;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.notes.NoteBean;
import org.ncbo.stanford.enumeration.NoteAppliesToTypeEnum;
import org.protege.notesapi.NotesException;
import org.protege.notesapi.notes.Annotation;
import org.protege.notesapi.notes.NoteType;
import org.protege.notesapi.notes.Status;

public interface NotesService {

	// Create methods

	public NoteBean createNote(OntologyBean ont, String appliesTo,
			NoteAppliesToTypeEnum appliesToType, NoteType noteType,
			String subject, String content, String author, Long created)
			throws Exception;

	public NoteBean createNewTermProposal(OntologyBean ont, String appliesTo,
			NoteAppliesToTypeEnum appliesToType, NoteType noteType,
			String subject, String content, String author, Long created,
			String reasonForChange, String contactInfo, String termDefinition,
			String termId, String termParent, String termPreferredName,
			List<String> termSynonyms) throws NotesException;

	public NoteBean createNewRelationshipProposal(OntologyBean ont,
			String appliesTo, NoteAppliesToTypeEnum appliesToType,
			NoteType noteType, String subject, String content, String author,
			Long created, String reasonForChange, String contactInfo,
			String relationshipType, String relationshipTarget,
			String relationshipOldTarget) throws NotesException;

	public NoteBean createNewPropertyValueChangeProposal(OntologyBean ont,
			String appliesTo, NoteAppliesToTypeEnum appliesToType,
			NoteType noteType, String subject, String content, String author,
			Long created, String reasonForChange, String contactInfo,
			String propertyNewValue, String propertyOldValue, String propertyId)
			throws NotesException;

	// Update methods

	public NoteBean updateNote(OntologyBean ont, String noteId,
			NoteType noteType, String subject, String content, String author,
			Long created, Status status, String appliesTo,
			NoteAppliesToTypeEnum appliesToType) throws Exception;

	public void archiveNote(OntologyBean ont, String noteId);

	public void unarchiveNote(OntologyBean ont, String noteId);

	public void archiveThread(OntologyBean ont, String rootNoteId);

	public void unarchiveThread(OntologyBean ont, String rootNoteId);

	// Delete methods

	public void deleteNote(OntologyBean ont, String noteId);

	// Retrieval methods

	public Annotation getNote(OntologyBean ont, String iri);

	public List<NoteBean> getAllNotesForOntology(OntologyBean ont,
			Boolean threaded, Boolean topLevelOnly);

	public List<NoteBean> getAllNotesForOntologyByAuthor(OntologyBean ont,
			Integer author);

	public List<NoteBean> getAllNotesForConcept(OntologyBean ont,
			ClassBean concept, Boolean threaded);

	public List<NoteBean> getAllNotesForIndividual(OntologyBean ont,
			String instanceId, Boolean threaded);

	public List<NoteBean> getAllNotesForNote(OntologyBean ont, String noteId,
			Boolean threaded);
}
