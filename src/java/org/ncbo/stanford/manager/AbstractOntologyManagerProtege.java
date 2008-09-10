package org.ncbo.stanford.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.util.cache.expiration.system.ExpirationSystem;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.query.api.QueryApi;
import edu.stanford.smi.protege.query.api.QueryConfiguration;
import edu.stanford.smi.protege.query.indexer.Indexer;
import edu.stanford.smi.protege.query.indexer.StdIndexer;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;

/**
 * Abstract class to incorporate functionality common between Protege loader and
 * retrieval manager classes
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractOntologyManagerProtege {

	private static final Log log = LogFactory
			.getLog(AbstractOntologyManagerProtege.class);

	protected String protegeJdbcUrl;
	protected String protegeJdbcDriver;
	protected String protegeJdbcUsername;
	protected String protegeJdbcPassword;
	protected String protegeTablePrefix;
	protected String protegeTableSuffix;
	protected Integer protegeBigFileThreshold;
	protected String protegeIndexLocation;
	protected ExpirationSystem<Integer, KnowledgeBase> protegeKnowledgeBases = null;

	/**
	 * Sets the configuration for Protege Lucene index
	 * 
	 * @param kb
	 * @param api
	 * @param ob
	 */
	protected void setIndexConfiguration(KnowledgeBase kb, QueryApi api,
			OntologyBean ob) {
		QueryConfiguration config = new QueryConfiguration(kb);

		config.setBaseIndexPath(protegeIndexLocation + ob.getOntologyDirPath());

		Set<Indexer> indexers = new HashSet<Indexer>();
		indexers.add(new StdIndexer());
		config.setIndexers(indexers);

		api.install(config);
	}

	/**
	 * Returns a singleton KnowledgeBase instance for given ontology.
	 * Assumes that the OntologyBean object is populated.
	 */
	protected KnowledgeBase getKnowledgeBase(OntologyBean ob) {
		KnowledgeBase kb = (KnowledgeBase) protegeKnowledgeBases
				.get(ob.getId());

		if (kb == null) {
			kb = createKnowledgeBaseInstance(ob);
			protegeKnowledgeBases.put(ob.getId(), kb);
		}

		return kb;
	}
	
	/**
	 * Returns a singleton KnowledgeBase instance for given ontologyVersion.
	 */
	protected KnowledgeBase getKnowledgeBase(VNcboOntology ontologyVersion) {
		KnowledgeBase kb = (KnowledgeBase) protegeKnowledgeBases
				.get(ontologyVersion.getId());

		if (kb == null) {
			OntologyBean ob = new OntologyBean();
			ob.populateFromEntity(ontologyVersion);
			kb = createKnowledgeBaseInstance(ob);

			protegeKnowledgeBases.put(ontologyVersion.getId(), kb);
		}

		return kb;
	}

	/**
	 * Gets the Protege ontology associated with the specified ontology id.
	 */
	@SuppressWarnings("unchecked")
	private KnowledgeBase createKnowledgeBaseInstance(OntologyBean ob) {
		DatabaseKnowledgeBaseFactory factory = null;

		if (ob.getFormat().contains("OWL")) {
			factory = new OWLDatabaseKnowledgeBaseFactory();
		} else {
			factory = new DatabaseKnowledgeBaseFactory();
		}

		List errors = new ArrayList();
		Project prj = Project.createBuildProject(factory, errors);
		DatabaseKnowledgeBaseFactory.setSources(prj.getSources(),
				protegeJdbcDriver, protegeJdbcUrl, getTableName(ob.getId()),
				protegeJdbcUsername, protegeJdbcPassword);
		prj.createDomainKnowledgeBase(factory, errors, true);
		KnowledgeBase kb = prj.getKnowledgeBase();

		QueryApi api = new QueryApi(kb);
		setIndexConfiguration(kb, api, ob);

		log.debug("Created new knowledgebase: " + kb.getName());

		return prj.getKnowledgeBase();
	}

	/**
	 * Gets the table name associated with an protege ontology id.
	 */
	protected String getTableName(Integer ontologyVersionId) {
		return protegeTablePrefix + ontologyVersionId + protegeTableSuffix;
	}

	/**
	 * @return the protegeJdbcUrl
	 */
	public String getProtegeJdbcUrl() {
		return protegeJdbcUrl;
	}

	/**
	 * @param protegeJdbcUrl
	 *            the protegeJdbcUrl to set
	 */
	public void setProtegeJdbcUrl(String protegeJdbcUrl) {
		this.protegeJdbcUrl = protegeJdbcUrl;
	}

	/**
	 * @return the protegeJdbcDriver
	 */
	public String getProtegeJdbcDriver() {
		return protegeJdbcDriver;
	}

	/**
	 * @param protegeJdbcDriver
	 *            the protegeJdbcDriver to set
	 */
	public void setProtegeJdbcDriver(String protegeJdbcDriver) {
		this.protegeJdbcDriver = protegeJdbcDriver;
	}

	/**
	 * @return the protegeJdbcUsername
	 */
	public String getProtegeJdbcUsername() {
		return protegeJdbcUsername;
	}

	/**
	 * @param protegeJdbcUsername
	 *            the protegeJdbcUsername to set
	 */
	public void setProtegeJdbcUsername(String protegeJdbcUsername) {
		this.protegeJdbcUsername = protegeJdbcUsername;
	}

	/**
	 * @return the protegeJdbcPassword
	 */
	public String getProtegeJdbcPassword() {
		return protegeJdbcPassword;
	}

	/**
	 * @param protegeJdbcPassword
	 *            the protegeJdbcPassword to set
	 */
	public void setProtegeJdbcPassword(String protegeJdbcPassword) {
		this.protegeJdbcPassword = protegeJdbcPassword;
	}

	/**
	 * @return the protegeTablePrefix
	 */
	public String getProtegeTablePrefix() {
		return protegeTablePrefix;
	}

	/**
	 * @param protegeTablePrefix
	 *            the protegeTablePrefix to set
	 */
	public void setProtegeTablePrefix(String protegeTablePrefix) {
		this.protegeTablePrefix = protegeTablePrefix;
	}

	/**
	 * @return the protegeTableSuffix
	 */
	public String getProtegeTableSuffix() {
		return protegeTableSuffix;
	}

	/**
	 * @param protegeTableSuffix
	 *            the protegeTableSuffix to set
	 */
	public void setProtegeTableSuffix(String protegeTableSuffix) {
		this.protegeTableSuffix = protegeTableSuffix;
	}

	/**
	 * @return the protegeBigFileThreshold
	 */
	public Integer getProtegeBigFileThreshold() {
		return protegeBigFileThreshold;
	}

	/**
	 * @param protegeBigFileThreshold
	 *            the protegeBigFileThreshold to set
	 */
	public void setProtegeBigFileThreshold(Integer protegeBigFileThreshold) {
		this.protegeBigFileThreshold = protegeBigFileThreshold;
	}

	/**
	 * @return the protegeKnowledgeBases
	 */
	public ExpirationSystem<Integer, KnowledgeBase> getProtegeKnowledgeBases() {
		return protegeKnowledgeBases;
	}

	/**
	 * @param protegeKnowledgeBases
	 *            the protegeKnowledgeBases to set
	 */
	public void setProtegeKnowledgeBases(
			ExpirationSystem<Integer, KnowledgeBase> protegeKnowledgeBases) {
		this.protegeKnowledgeBases = protegeKnowledgeBases;
	}

	/**
	 * @return the protegeIndexLocation
	 */
	public String getProtegeIndexLocation() {
		return protegeIndexLocation;
	}

	/**
	 * @param protegeIndexLocation
	 *            the protegeIndexLocation to set
	 */
	public void setProtegeIndexLocation(String protegeIndexLocation) {
		this.protegeIndexLocation = protegeIndexLocation;
	}
}