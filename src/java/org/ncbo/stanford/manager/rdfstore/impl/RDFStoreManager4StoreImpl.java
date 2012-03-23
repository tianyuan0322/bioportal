package org.ncbo.stanford.manager.rdfstore.impl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import org.ncbo.stanford.manager.rdfstore.AbstractRDFStoreManager;
import org.ncbo.stanford.manager.rdfstore.RDFStoreManager;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

public class RDFStoreManager4StoreImpl extends AbstractRDFStoreManager
		implements RDFStoreManager {

	private String updatePath = "update/";
	private String sparqlPath = "sparql/";

	@Override
	public void addTriple(Statement statement, String graph) throws Exception {
		String triplesToAdd = convertStatementToTTL(statement);
		insert(triplesToAdd, graph);
	}

	@Override
	public void addTriples(List<Statement> statements, String graph)
			throws Exception {
		String triplesToAdd = convertStatementsToTTL(statements);
		insert(triplesToAdd, graph);
	}

	@Override
	public void deleteTriple(Statement statement) throws Exception {
		String triplesToDelete = convertStatementToTTL(statement);
		delete(triplesToDelete);
	}

	@Override
	public void deleteTriples(List<Statement> statements) throws Exception {
		String triplesToDelete = convertStatementsToTTL(statements);
		delete(triplesToDelete);
	}

	private String convertStatementToTTL(Statement statement) {
		StringWriter out = new StringWriter();
		RDFWriter ttl = Rio.createWriter(RDFFormat.TURTLE, out);

		try {
			ttl.startRDF();
			ttl.handleStatement(statement);
			ttl.endRDF();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}

		return out.toString();
	}

	private String convertStatementsToTTL(List<Statement> statements) {
		StringWriter out = new StringWriter();
		RDFWriter ttl = Rio.createWriter(RDFFormat.TURTLE, out);

		try {
			ttl.startRDF();
			for (Statement statement : statements) {
				ttl.handleStatement(statement);
			}
			ttl.endRDF();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}

		return out.toString();
	}

	private void insert(String turtle, String graph) throws Exception {
		String insertQuery = "INSERT DATA { GRAPH <%graph%> { %turtle% } }";
		insertQuery = insertQuery.replace("%turtle%", turtle).replace(
				"%graph%", graph);
		executeSPARQLUpdate(insertQuery);
	}

	private void delete(String turtle) throws Exception {
		String deleteQuery = "DELETE DATA { %turtle% }";
		deleteQuery = deleteQuery.replace("%turtle%", turtle);
		executeSPARQLUpdate(deleteQuery);
	}

	private void executeSPARQLUpdate(String query) throws Exception {
		HashMap<String, String> postParams = new HashMap<String, String>();
		postParams.put("update", query);

		String postUrl = MessageUtils.getMessage("rdf.4store.endpoint.url") + updatePath;

		RequestUtils.doHttpPost(postUrl, postParams);
	}

	@Override
	public void initializeRepository() {
		try {
			if (!isAvailable()) {
				repository = new SPARQLRepository(
						MessageUtils.getMessage("rdf.4store.endpoint.url")
								+ sparqlPath);
				repository.initialize();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup() {
		if (repository != null) {
			try {
				repository.shutDown();
				repository = null;
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}

}
