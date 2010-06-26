package org.ncbo.stanford.aop.notification;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

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
import org.ncbo.stanford.exception.NoteNotFoundException;
import org.ncbo.stanford.service.notes.NotesService;
import org.ncbo.stanford.service.notification.EmailNotificationService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * @author g.prakash
 * 
 */

public class NotificationAdvice {

	private static final Log log = LogFactory.getLog(NotificationAdvice.class);

	private EmailNotificationService emailNotificationService;
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
		/** Reference implementaion. Disabled until needed.
		emailNotificationService.sendNotification(
				NotificationTypeEnum.UPDATE_ONTOLOGY_NOTIFICATION,
				ontologyBean, null); **/
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

			// Generate URL
			keywords.put(ApplicationConstants.NOTE_URL, generateUrlForNote(
					note, ont));

			// Get UserBean for note author and add username if found
			NcboUser ncboUser = ncboUserDAO.findById(note.getAuthor());
			String authorName = "unknown";
			if (ncboUser != null) {
				UserBean userBean = new UserBean();
				userBean.populateFromEntity(ncboUser);
				authorName = userBean.getUsername();
			}
			keywords.put(ApplicationConstants.NOTE_USERNAME, authorName);

			// Generate message ID
			String messageId = getMessageIdForNote(note.getId(), authorName);
			keywords.put("messageId", messageId);

			// Generate in-reply-to ID (if needed)
			if (getAppliesToType(note) == NoteAppliesToTypeEnum.Note) {
				NoteBean parentNote = notesService.getNoteBean(ont, note
						.getAppliesToList().get(0).getId());

				// Get parent note user
				NcboUser parentUser = ncboUserDAO.findById(parentNote
						.getAuthor());
				String parentAuthorName = "unknown";
				if (parentUser != null) {
					UserBean userBean = new UserBean();
					userBean.populateFromEntity(parentUser);
					parentAuthorName = userBean.getUsername();
				}

				String parentMessageId = getMessageIdForNote(
						parentNote.getId(), parentAuthorName);
				keywords.put("inReplyTo", parentMessageId);
			}

			// Get proposal information
			keywords.put(ApplicationConstants.NOTE_PROPOSAL_INFO,
					getProposalText(note));

			// Add note-specific keywords
			keywords.put(ApplicationConstants.NOTE_SUBJECT, note.getSubject());
			keywords.put(ApplicationConstants.NOTE_BODY, note.getBody());

			// Send notification
			emailNotificationService.sendNotification(
					NotificationTypeEnum.CREATE_NOTE_NOTIFICATION, ont,
					keywords);

		} catch (Exception e) {
			log.error("Error sending notification for new note");
		}
	}

	/**
	 * Returns the URL for the UI representation of a note.
	 * 
	 * @param note
	 * @param ont
	 * @return
	 * @throws NoteNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private String generateUrlForNote(NoteBean note, OntologyBean ont)
			throws NoteNotFoundException, UnsupportedEncodingException {
		// Add concept keywords

		// Get note type using the first item in the list (should be only item)
		// TODO: If we start supporting multiple annotation targets this will
		// need modification
		NoteAppliesToTypeEnum appliesToType = getAppliesToType(note);

		String uiUrl = MessageUtils.getMessage("ui.url");
		String noteUrl = null;

		switch (appliesToType) {
		case Class:
		case Property:
		case Individual:
			String conceptPath = MessageUtils
					.getMessage("ui.path.notes.concept");
			String conceptPathReplaced = conceptPath.replaceAll(
					"<" + ApplicationConstants.CONCEPT_ID + ">",
					URLEncoder.encode(note.getAppliesToList().get(0).getId(),
							"UTF-8")).replaceAll(
					"<" + ApplicationConstants.ONTOLOGY_VERSION_ID + ">",
					ont.getId().toString());
			noteUrl = uiUrl + conceptPathReplaced;
			break;
		case Note:
			NoteBean rootNote = notesService.getRootNote(ont, note);
			noteUrl = generateUrlForNote(rootNote, ont);
			break;
		case Ontology:
			String ontPath = MessageUtils.getMessage("ui.path.notes.ontology");
			String ontPathReplaced = ontPath.replaceAll("<"
					+ ApplicationConstants.ONTOLOGY_VERSION_ID + ">", ont
					.getId().toString());
			noteUrl = uiUrl + ontPathReplaced;
			break;
		}

		return noteUrl;
	}

	private String getProposalText(NoteBean note) {
		String proposalText = "";

		if (note.getValues() != null && !note.getValues().isEmpty()) {
			AbstractProposalBean proposal = (AbstractProposalBean) note
					.getValues().get(0);
			proposalText = proposal.toHTML();
		}

		return proposalText;
	}

	/**
	 * Returns the enum type of a note using the first appliesTo target in the
	 * list.
	 * 
	 * @param note
	 * @return
	 */
	private NoteAppliesToTypeEnum getAppliesToType(NoteBean note) {
		return NoteAppliesToTypeEnum.valueOf(note.getAppliesToList().get(0)
				.getType());
	}

	/**
	 * Generates a unique message id using the note's id.
	 * 
	 * @param note
	 * @return
	 */
	private String getMessageIdForNote(String noteId, String authorName) {
		return noteId + "." + authorName + "@" + "bioportal.bioontology.org";
	}

	/**
	 * 
	 * @param emailNotificationService
	 */
	public void setEmailNotificationService(EmailNotificationService emailNotificationService) {
		this.emailNotificationService = emailNotificationService;
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
