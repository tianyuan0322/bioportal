package org.ncbo.stanford.aop.notification;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.notes.AbstractProposalBean;
import org.ncbo.stanford.bean.notes.NoteBean;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;
import org.ncbo.stanford.enumeration.NoteAppliesToTypeEnum;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.service.notes.NotesService;
import org.ncbo.stanford.service.notification.NotificationService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * @author g.prakash
 * 
 */

public class NotificationAdvice {

	private static final Log log = LogFactory.getLog(NotificationAdvice.class);

	private NotificationService notificationService;
	private OntologyService ontologyService;
	private NotesService notesService;
	private NcboUserDAO ncboUserDAO;

	/**
	 * 
	 * @param call
	 * @param ontologyVersion
	 * @param ontologyId
	 * @return
	 * @throws Throwable
	 */
	public void adviceUpdateOntology(OntologyBean ontologyBean)
			throws Throwable {
		notificationService.sendNotification(
				NotificationTypeEnum.UPDATE_ONTOLOGY_NOTIFICATION,
				ontologyBean, null);
	}

	public void adviceCreateNote(NoteBean note) throws Throwable {
		try {
		
		HashMap<String, String> keywords = new HashMap<String, String>();

		// Add ontology keywords
		OntologyBean ont = ontologyService
				.findLatestActiveOntologyOrViewVersion(note.getOntologyId());

		keywords.put(ApplicationConstants.ONTOLOGY_VERSION_ID, ont.getId()
				.toString());
		keywords.put(ApplicationConstants.ONTOLOGY_DISPLAY_LABEL, ont
				.getDisplayLabel());

		// Add concept keywords

		// Get note type using the first item in the list (should be only item)
		// TODO: If we start supporting multiple annotation targets this will
		// need modification
		NoteAppliesToTypeEnum appliesToType = NoteAppliesToTypeEnum
				.valueOf(note.getAppliesToList().get(0).getType());

		String uiUrl = MessageUtils.getMessage("ui.url");

		switch (appliesToType) {
		case Class:
		case Property:
		case Individual:
			String conceptPath = MessageUtils
					.getMessage("ui.path.notes.concept");
			String conceptPathReplaced = conceptPath.replaceAll(
					"<" + ApplicationConstants.CONCEPT_ID + ">",
					note.getAppliesToList().get(0).getId()).replaceAll(
					"<" + ApplicationConstants.ONTOLOGY_VERSION_ID + ">",
					ont.getId().toString());
			keywords.put(ApplicationConstants.NOTE_URL, uiUrl
					+ conceptPathReplaced);
			break;
		case Note:
			NoteBean rootNote = notesService.getRootNote(ont, note);
			String conceptForNotePath = MessageUtils
					.getMessage("ui.path.notes.concept");
			String conceptForNoteReplaced = conceptForNotePath.replaceAll(
					"<" + ApplicationConstants.CONCEPT_ID + ">",
					rootNote.getAppliesToList().get(0).getId()).replaceAll(
					"<" + ApplicationConstants.ONTOLOGY_VERSION_ID + ">",
					ont.getId().toString());
			keywords.put(ApplicationConstants.NOTE_URL, uiUrl
					+ conceptForNoteReplaced);
			break;
		case Ontology:
			String ontPath = MessageUtils.getMessage("ui.path.notes.ontology");
			String ontPathReplaced = ontPath.replaceAll("<"
					+ ApplicationConstants.ONTOLOGY_VERSION_ID + ">", ont
					.getId().toString());
			keywords
					.put(ApplicationConstants.NOTE_URL, uiUrl + ontPathReplaced);
			break;
		}

		// Get UserBean for note author and add username if found
		NcboUser ncboUser = ncboUserDAO.findById(note.getAuthor());
		if (ncboUser != null) {
			UserBean userBean = new UserBean();
			userBean.populateFromEntity(ncboUser);
			keywords.put(ApplicationConstants.NOTE_USERNAME, userBean
					.getUsername());
		}

		// Get proposal information
		List<Object> proposalList = note.getValues();
		if (proposalList != null) {
			AbstractProposalBean proposalBean = (AbstractProposalBean) proposalList
					.get(0);
			keywords.put(ApplicationConstants.NOTE_REASON_FOR_CHANGE,
					proposalBean.getReasonForChange());
		}

		// Add note-specific keywords
		keywords.put(ApplicationConstants.NOTE_SUBJECT, note.getSubject());
		keywords.put(ApplicationConstants.NOTE_BODY, note.getBody());

		// Send notification
		notificationService.sendNotification(
				NotificationTypeEnum.CREATE_NOTE_NOTIFICATION, ont, keywords);
		
		} catch (Exception e) {
			log.error("Error sending notification for new note");
		}
	}

	/**
	 * 
	 * @param notificationService
	 */
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @param notesService
	 *            the notesService to set
	 */
	public void setNotesService(NotesService notesService) {
		this.notesService = notesService;
	}

	/**
	 * @param ncboUserDAO
	 *            the ncboUserDAO to set
	 */
	public void setNcboUserDAO(NcboUserDAO ncboUserDAO) {
		this.ncboUserDAO = ncboUserDAO;
	}
}
