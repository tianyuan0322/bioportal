package org.ncbo.stanford.manager.notes.impl;

import java.io.File;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.notes.NotesPool;
import org.ncbo.stanford.util.MessageUtils;
import org.protege.notesapi.NotesException;
import org.protege.notesapi.NotesManager;
import org.protege.notesapi.NotesManagerPool;
import org.protege.notesapi.db.DatabaseBackedOntologyHandler;
import org.protege.notesapi.db.DatabaseBackedOntologyHandlerImpl;
import org.semanticweb.owlapi.model.IRI;

public class NotesPoolImpl implements NotesPool {

	private String notesJdbcUrl;
	private String notesJdbcDriver;
	private String notesJdbcUsername;
	private String notesJdbcPassword;

	public NotesManager getNotesManagerForOntology(OntologyBean ont) {
		NotesManager notesManager = null;

		try {
			notesManager = getNotesManagerFromPool(ont);
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return notesManager;
	}

	private NotesManager getNotesManagerFromPool(OntologyBean ont)
			throws NotesException {
		NotesManagerPool notesManagerPool = new NotesManagerPool();
		notesManagerPool.setChaoDocumentIRI(IRI.create(
				new File(MessageUtils
						.getMessage("bioportal.notes.changes.includes.path"))).toString());
		DatabaseBackedOntologyHandler dbOntologyHandler = new DatabaseBackedOntologyHandlerImpl(
				notesJdbcUrl, notesJdbcUsername, notesJdbcPassword);
		return notesManagerPool.getNotesManagerForDatabase(dbOntologyHandler,
				ont.getOntologyId());
	}

	/**
	 * @param notesJdbcUrl
	 *            the notesJdbcUrl to set
	 */
	public void setNotesJdbcUrl(String notesJdbcUrl) {
		this.notesJdbcUrl = notesJdbcUrl;
	}

	/**
	 * @param notesJdbcDriver
	 *            the notesJdbcDriver to set
	 */
	public void setNotesJdbcDriver(String notesJdbcDriver) {
		this.notesJdbcDriver = notesJdbcDriver;
	}

	/**
	 * @param notesJdbcUsername
	 *            the notesJdbcUsername to set
	 */
	public void setNotesJdbcUsername(String notesJdbcUsername) {
		this.notesJdbcUsername = notesJdbcUsername;
	}

	/**
	 * @param notesJdbcPassword
	 *            the notesJdbcPassword to set
	 */
	public void setNotesJdbcPassword(String notesJdbcPassword) {
		this.notesJdbcPassword = notesJdbcPassword;
	}

}
