package org.ncbo.stanford.aop.notification;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.bean.notes.AbstractProposalBean;
import org.ncbo.stanford.bean.notes.NoteBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;
import org.ncbo.stanford.enumeration.NoteAppliesToTypeEnum;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.exception.NoteNotFoundException;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;
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
	protected CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	protected OntologyMetadataManager ontologyMetadataManager;
	protected OntologyLoadSchedulerService ontologyLoadSchedulerService;
	
	/**
	 * This method is applied for AOP for processOntology by using Spring "<aop:aspect>" 
	 * "after-returning" 
	 * @param loadQueue
	 * @param formatHandler
	 * @param ontologyBean
	 * @return
	 */
	public void afterReturnProcessOntology(NcboOntologyLoadQueue loadQueue,String formatHandler,OntologyBean ontologyBean) throws Throwable{
		
		try {
			
			// Map For Keywords
			HashMap<String, String> keywords = new HashMap<String, String>();
			
			//List For NoteBean
			List<NoteBean> notes = null;
			
			//information on the status of the loading of their ontology is 
			//successful by Email Nitification
			List<String> fileforOntology=ontologyBean.getFilenames();
			
			//Here OntologyBean is passed by dynamically following by Spring AOP
			// Putting the OntologyId inside HashMap according to key
			keywords.put(ApplicationConstants.ONTOLOGY_VERSION_ID, ontologyBean
					.getId().toString());
			
			//Putting the DisplayLabel inside HashMap
			keywords.put(ApplicationConstants.ONTOLOGY_DISPLAY_LABEL,
					ontologyBean.getDisplayLabel());
	
			// Taking the UserName
			String authorName = getAuthor(ncboUserDAO, ontologyBean);
			
			// Putting the UserName inside HahaMap according to UserName
			// keywords
			keywords.put(ApplicationConstants.USERNAME, "gyan");
			
			// Taking the MessageId
			String messageId = getMessageIdForOntology(ontologyBean.getId()
					.toString(), "gyan");
			
			// Putting the messageId inside HashMap according to Key
			keywords.put("messageId", messageId);
			
			// Calling the sendNotification
			notes = notesService.getAllNotesForOntologyByAuthor(ontologyBean,authorName);
			//Iterating the List of NoteBean
			for(NoteBean note: notes){
			
			// Generate in-reply-to ID (if needed)
			if (getAppliesToType(note) == NoteAppliesToTypeEnum.Note) {
				NoteBean parentNote = notesService.getNoteBean(ontologyBean, note
						.getAppliesToList().get(0).getId());

				// Look for valid author name for parent note
				String parentAuthorName = getAuthor(ncboUserDAO, parentNote);

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
			}
			
			emailNotificationService.sendNotification(
					NotificationTypeEnum.PARSE_ONTOLOGY_NOTIFICATION,
					ontologyBean, keywords);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error sending notification for new Updation Ontology");
		}

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

			// Look for valid author name
			String authorName = getAuthor(ncboUserDAO, note);
			keywords.put(ApplicationConstants.NOTE_USERNAME, authorName);

			// Generate message ID
			String messageId = getMessageIdForNote(note.getId(), authorName);
			keywords.put("messageId", messageId);

			// Generate in-reply-to ID (if needed)
			if (getAppliesToType(note) == NoteAppliesToTypeEnum.Note) {
				NoteBean parentNote = notesService.getNoteBean(ont, note
						.getAppliesToList().get(0).getId());

				// Look for valid author name for parent note
				String parentAuthorName = getAuthor(ncboUserDAO, parentNote);

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

	/**
	 * Gets the generated proposal HTML from the bean.
	 * 
	 * @param note
	 * @return
	 */
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
	 * Get UserBean for note author and add username if found Because we allow
	 * arbitrary authors to be submitted, we try to parse an integer which we
	 * assume is a BioPortal user id. If no integer is found (runtime exception)
	 * then use the raw value.
	 * 
	 * @param ncboUserDAO
	 * @param note
	 * @return
	 */
	private String getAuthor(NcboUserDAO ncboUserDAO, NoteBean note) {
		String authorName = "unknown";

		try {
			Integer userId = Integer.parseInt(note.getAuthor());
			NcboUser ncboUser = ncboUserDAO.findById(userId);
			if (ncboUser != null) {
				UserBean userBean = new UserBean();
				userBean.populateFromEntity(ncboUser);
				authorName = userBean.getUsername();
			}
		} catch (NumberFormatException nfe) {
			authorName = note.getAuthor();
		}

		return authorName;
	}
	
	/**
	 * Get UserBean for note author and add username if found Because we allow
	 * arbitrary authors to be submitted, we try to parse an integer which we
	 * assume is a BioPortal user id. If no integer is found (runtime exception)
	 * then use the raw value.
	 * 
	 * @param ncboUserDAO
	 * @param note
	 * @return
	 */
	private String getAuthor(NcboUserDAO ncboUserDAO, OntologyBean ont) {
		String authorName = "unknown";

		try {
			Integer userId = ont.getUserId();
			NcboUser ncboUser = ncboUserDAO.findById(userId);
			if (ncboUser != null) {
				UserBean userBean = new UserBean();
				userBean.populateFromEntity(ncboUser);
				authorName = userBean.getUsername();
			}
		} catch (NumberFormatException nfe) {
			nfe.getMessage();
		}

		return authorName;
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
	 * Returns the enum type of a note using the first appliesTo target in the
	 * list.
	 * 
	 * @param note
	 * @return
	 */
	/**
	private OntologyAppliesToTypeEnum getAppliesToType(OntologyBean ont) {
		return NoteAppliesToTypeEnum.valueOf(ont.getFormat());
	}
**/
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
	 * Generates a unique message id using the note's id.
	 * 
	 * @param note
	 * @return
	 */
	private String getMessageIdForOntology(String noteId, String authorName) {
		return noteId + "." + authorName + "@" + "bioportal.bioontology.org";
	}


	/**
	 * 
	 * @param emailNotificationService
	 */
	public void setEmailNotificationService(
			EmailNotificationService emailNotificationService) {
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
	
	/**
	 * 
	 * @param ontologyMetadataManager
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}
	/**
	 * 
	 * @param ontologyLoadSchedulerService
	 */
	public void setOntologyLoadSchedulerService(
			OntologyLoadSchedulerService ontologyLoadSchedulerService) {
		this.ontologyLoadSchedulerService = ontologyLoadSchedulerService;
	}
	
	/**
	 * 
	 * @param ncboOntologyLoadQueueDAO
	 */
	public void setNcboOntologyLoadQueueDAO(
			CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO) {
		this.ncboOntologyLoadQueueDAO = ncboOntologyLoadQueueDAO;
	}

}
