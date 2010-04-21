package org.ncbo.stanford.manager.notes;

import org.ncbo.stanford.bean.OntologyBean;

public interface NotesPool {

	public org.protege.notesapi.NotesManager getNotesManagerForOntology(
			OntologyBean ont);

}
