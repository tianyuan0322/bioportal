package org.ncbo.stanford.service.search;

public interface IndexService {

	public void indexAllOntologies() throws Exception;

	public void indexOntology(Integer ontologyId) throws Exception;

	public void indexOntology(Integer ontologyId, boolean doBackup)
			throws Exception;

	public void removeOntology(Integer ontologyId) throws Exception;

	public void backupIndex() throws Exception;
}
