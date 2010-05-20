package org.ncbo.stanford.view.rest.restlet.notes;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.bean.notes.NoteBean;
import org.ncbo.stanford.enumeration.NoteAppliesToTypeEnum;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.exception.InstanceNotFoundException;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.NoteNotFoundException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.notes.NotesService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.protege.notesapi.notes.Annotation;
import org.protege.notesapi.notes.NoteType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

public class NotesRestlet extends AbstractBaseRestlet {
	private static final Log log = LogFactory.getLog(NotesRestlet.class);
	private NotesService notesService;
	private OntologyService ontologyService;
	private ConceptService conceptService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		listNotes(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void postRequest(Request request, Response response) {
		createNote(request, response);
	}

	/**
	 * Handle PUT calls here
	 */
	@Override
	public void putRequest(Request request, Response response) {
		updateNote(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void deleteRequest(Request request, Response response) {
		deleteNote(request, response);
	}

	// Private helpers

	private void listNotes(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Get necessary parameters
		String noteId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_ID);
		Boolean threaded = RequestUtils.parseBooleanParam(httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_THREADED));
		Boolean topLevelOnly = RequestUtils.parseBooleanParam(httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_TOP_LEVEL_ONLY));
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		String conceptId = getConceptId(request);
		String instanceId = httpRequest.getParameter(MessageUtils
				.getMessage("entity.instanceid"));

		// Post-process parameters
		Integer ontologyIdInt = RequestUtils.parseIntegerParam(ontologyId);
		Integer ontologyVersionIdInt = RequestUtils
				.parseIntegerParam(ontologyVersionId);

		OntologyBean ont = null;
		List<NoteBean> notesList = null;
		try {
			if (ontologyIdInt != null) {
				ont = ontologyService
						.findLatestOntologyOrViewVersion(ontologyIdInt);
			} else if (ontologyVersionIdInt != null) {
				ont = ontologyService.findOntologyOrView(ontologyVersionIdInt);
			}

			// Check to make sure the ontology is valid
			if (ont == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}

			// Get a list of notes depending on whether user wants notes for
			// concept, ont, or note
			if (conceptId != null) {
				notesList = listNotesForConcept(ont, conceptId, threaded);
			} else if (noteId != null) {
				notesList = listNotesForNote(ont, noteId, threaded);
			} else if (instanceId != null) {
				notesList = listNotesForIndividual(ont, instanceId, threaded);
			} else {
				notesList = notesService.getAllNotesForOntology(ont, threaded,
						topLevelOnly);
			}

			if (notesList == null || notesList.isEmpty()) {
				throw new NoteNotFoundException();
			}
		} catch (NoteNotFoundException nnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, nnfe.getMessage());
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (ConceptNotFoundException cnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, cnfe.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					notesList);
		}

	}

	private List<NoteBean> listNotesForConcept(OntologyBean ont,
			String conceptId, Boolean threaded) throws Exception {
		ClassBean concept = conceptService.findConcept(ont.getId(), conceptId,
				null, false, false);

		// Check to make sure the concept is valid
		if (concept == null) {
			throw new ConceptNotFoundException(MessageUtils
					.getMessage("msg.error.conceptNotFound"));
		}

		return notesService.getAllNotesForConcept(ont, concept, threaded);
	}

	private List<NoteBean> listNotesForNote(OntologyBean ont, String noteId,
			Boolean threaded) throws NoteNotFoundException {
		Annotation noteToList = notesService.getNote(ont, noteId);

		return notesService.getAllNotesForNote(ont, noteToList.getId(), threaded);
	}

	private List<NoteBean> listNotesForIndividual(OntologyBean ont,
			String individualId, Boolean threaded) throws Exception {
		InstanceBean instance = conceptService.findInstanceById(ont.getId(),
				individualId);

		if (instance == null) {
			throw new InstanceNotFoundException();
		}

		return notesService.getAllNotesForIndividual(ont, individualId,
				threaded);
	}

	private void createNote(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String appliesTo = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_APPLIES_TO);
		String appliesToTypeStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_APPLIES_TO_TYPE);
		String noteTypeStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_TYPE);
		String subject = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_SUBJECT);
		String content = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_CONTENT);
		String author = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_AUTHOR);
		String reasonForChange = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_REASON_FOR_CHANGE);
		String contactInfo = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_CONTACT_INFO);
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));

		// Get other parameters as needed
		String termDefinition = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NEW_TERM_DEFINITION);
		String termId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NEW_TERM_ID);
		String termParent = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NEW_TERM_PARENT);
		String termPreferredName = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NEW_TERM_PREFERRED_NAME);
		String termSynonyms = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NEW_TERM_SYNONYMS);
		String relationshipType = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NEW_REL_TYPE);
		String relationshipTarget = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NEW_REL_TARGET);
		String relationshipOldTarget = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NEW_REL_OLD_TARGET);
		String propertyNewValue = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROP_NEW_VALUE);
		String propertyOldValue = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROP_OLD_VALUE);
		String propertyId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_PROP_ID);
		String created = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_CREATED);

		// Post-process parameters
		NoteType noteType = NoteType.valueOf(noteTypeStr);
		NoteAppliesToTypeEnum appliesToType = NoteAppliesToTypeEnum
				.valueOf(appliesToTypeStr);
		List<String> synonymList = RequestUtils
				.parseStringListParam(termSynonyms);
		Integer ontologyIdInt = RequestUtils.parseIntegerParam(ontologyId);
		Integer ontologyVersionIdInt = RequestUtils
				.parseIntegerParam(ontologyVersionId);
		Long createdLong = RequestUtils.parseLongParam(created);

		OntologyBean ont = null;
		NoteBean noteBean = null;
		try {
			if (ontologyIdInt != null) {
				ont = ontologyService
						.findLatestOntologyOrViewVersion(ontologyIdInt);
			} else if (ontologyVersionIdInt != null) {
				ont = ontologyService.findOntologyOrView(ontologyVersionIdInt);
			}

			if (ont == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}

			// Check to make sure that the appliesTo is valid
			// We also set the appliesTo to use fullId in the case a shortId is
			// passed by a user
			switch (appliesToType) {
			case Class:
				ClassBean concept = conceptService.findConcept(ont.getId(),
						appliesTo, null, false, false);

				if (concept == null) {
					throw new ConceptNotFoundException(MessageUtils
							.getMessage("msg.error.conceptNotFound"));
				}

				appliesTo = concept.getFullId();

				break;
			case Property:
				// TODO: Add check for valid property
				break;
			case Individual:
				InstanceBean instance = conceptService.findInstanceById(ont
						.getId(), appliesTo);

				if (instance == null) {
					throw new InstanceNotFoundException();
				}

				appliesTo = instance.getFullId();

				break;
			case Note:
				Annotation note = notesService.getNote(ont, appliesTo);
				if (note == null) {
					throw new NoteNotFoundException();
				}
				break;
			}

			switch (noteType) {
			case ProposalForCreateEntity:
				noteBean = notesService.createNewTermProposal(ont,
						appliesTo, appliesToType, noteType, subject,
						content, author, createdLong, reasonForChange,
						contactInfo, termDefinition, termId, termParent,
						termPreferredName, synonymList);
				break;
			case ProposalForChangeHierarchy:
				noteBean = notesService.createNewRelationshipProposal(ont,
						appliesTo, appliesToType, noteType, subject, content,
						author, createdLong, reasonForChange, contactInfo,
						relationshipType, relationshipTarget,
						relationshipOldTarget);
				break;
			case ProposalForChangePropertyValue:
				noteBean = notesService.createNewPropertyValueChangeProposal(
						ont, appliesTo, appliesToType, noteType, subject,
						content, author, createdLong, reasonForChange,
						contactInfo, propertyNewValue, propertyOldValue,
						propertyId);
				break;
			default:
				noteBean = notesService.createNote(ont, appliesTo,
						appliesToType, noteType, subject, content, author,
						createdLong);
				break;
			}

		} catch (NoteNotFoundException nnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, nnfe.getMessage());
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (ConceptNotFoundException cnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, cnfe.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					noteBean);
		}
	}

	private void updateNote(Request request, Response response) {
		// TODO: The notes API currently does not support a method to
		// handle updating notes. We essentially retrieve the annotation
		// and set individual properties.
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);
		String noteId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_ID);
		String archive = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_ARCHIVE);
		String archiveThread = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_ARCHIVE_THREAD);
		String unarchive = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_UNARCHIVE);
		String unarchiveThread = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_UNARCHIVE_THREAD);
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));
		String appliesTo = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_APPLIES_TO);
		String appliesToTypeStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_APPLIES_TO_TYPE);
		String noteTypeStr = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_TYPE);
		String subject = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_SUBJECT);
		String content = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_CONTENT);
		String author = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_AUTHOR);
		String created = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_CREATED);

		// Post-process parameters
		Boolean archiveBool = RequestUtils.parseBooleanParam(archive);
		Boolean archiveThreadBool = RequestUtils
				.parseBooleanParam(archiveThread);
		Boolean unarchiveBool = RequestUtils.parseBooleanParam(unarchive);
		Boolean unarchiveThreadBool = RequestUtils
				.parseBooleanParam(unarchiveThread);
		Integer ontologyIdInt = RequestUtils.parseIntegerParam(ontologyId);
		Integer ontologyVersionIdInt = RequestUtils
				.parseIntegerParam(ontologyVersionId);
		Long createdLong = RequestUtils.parseLongParam(created);

		NoteType noteType = null;
		if (noteTypeStr != null) {
			noteType = NoteType.valueOf(noteTypeStr);
		}

		NoteAppliesToTypeEnum appliesToType = null;
		if (appliesToTypeStr != null) {
			appliesToType = NoteAppliesToTypeEnum.valueOf(appliesToTypeStr);
		}

		OntologyBean ont = null;
		try {
			if (ontologyIdInt != null) {
				ont = ontologyService
						.findLatestOntologyOrViewVersion(ontologyIdInt);
			} else if (ontologyVersionIdInt != null) {
				ont = ontologyService.findOntologyOrView(ontologyVersionIdInt);
			}

			if (ont == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}

			Annotation noteToUpdate = notesService.getNote(ont, noteId);

			if (noteToUpdate == null) {
				throw new NoteNotFoundException();
			}

			if (archiveBool) {
				if (archiveThreadBool) {
					notesService.archiveThread(ont, noteId);
				} else {
					notesService.archiveNote(ont, noteId);
				}
			} else if (unarchiveBool) {
				if (unarchiveThreadBool) {
					notesService.unarchiveThread(ont, noteId);
				} else {
					notesService.unarchiveNote(ont, noteId);
				}
			} else {
				// TODO: Set status properly (unclear how Notes-api handles
				// this)
				notesService.updateNote(ont, noteId, noteType, subject,
						content, author, createdLong, null, appliesTo,
						appliesToType);
			}

		} catch (NoteNotFoundException nnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, nnfe.getMessage());
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		}
	}

	private void deleteNote(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Get necessary parameters
		String noteId = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NOTE_ID);
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		String ontologyVersionId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid"));

		// Post-process parameters
		Integer ontologyIdInt = RequestUtils.parseIntegerParam(ontologyId);
		Integer ontologyVersionIdInt = RequestUtils
				.parseIntegerParam(ontologyVersionId);

		OntologyBean ont = null;
		try {
			if (ontologyIdInt != null) {
				ont = ontologyService
						.findLatestOntologyOrViewVersion(ontologyIdInt);
			} else if (ontologyVersionIdInt != null) {
				ont = ontologyService.findOntologyOrView(ontologyVersionIdInt);
			}

			if (ont == null) {
				throw new InvalidInputException(MessageUtils
						.getMessage("msg.error.ontologyversionidinvalid"));
			}

			Annotation noteToDelete = notesService.getNote(ont, noteId);

			if (noteToDelete == null) {
				throw new NoteNotFoundException();
			} else {
				notesService.deleteNote(ont, noteId);
			}

		} catch (NoteNotFoundException nnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, nnfe.getMessage());
		} catch (OntologyNotFoundException onfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, onfe.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService
					.generateStatusXMLResponse(request, response);
		}
	}

	/**
	 * @param notesService
	 *            the notesService to set
	 */
	public void setNotesService(NotesService notesService) {
		this.notesService = notesService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

}
