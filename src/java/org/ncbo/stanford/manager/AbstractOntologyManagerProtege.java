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
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;

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
/*	protected void installLuceneIndexMechanism(QueryApi api, OntologyBean ob) {
		api.install(new File(getIndexPath(ob)));
	}
*/
	protected String getIndexPath(OntologyBean ob) {
		return protegeIndexLocation + ob.getOntologyDirPath();
	}

	protected Slot getSynonymSlot(KnowledgeBase kb, String synonymSlot) {
		if (!StringHelper.isNullOrNullString(synonymSlot)) {
			return (kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(synonymSlot) : kb.getSlot(synonymSlot));
		}

		return null;
	}

	protected Slot getPreferredNameSlot(KnowledgeBase kb,
			String preferredNameSlotName) {
		Slot slot = null;

		if (!StringHelper.isNullOrNullString(preferredNameSlotName)) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(preferredNameSlotName) : kb
					.getSlot(preferredNameSlotName);
		}

		if (slot == null) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFSLabelProperty() : kb.getNameSlot();
		}

		return slot;
	}

	/*
	 * protected String getPreferredName(Frame frame, String
	 * preferredNameSlotName) { Slot preferredNameSlot =
	 * getPreferredNameSlot(frame.getKnowledgeBase(), preferredNameSlotName);
	 * Collection values = frame.getOwnSlotValues(preferredNameSlot); if (values ==
	 * null || values.isEmpty() || values.size() > 1) { return frame.getName(); }
	 * else { Object o = values.iterator().next(); if (o instanceof String) {
	 * return (String) o; } else { return frame.getName(); } } }
	 */

	private void setBrowserSlotByPreferredNameSlot(KnowledgeBase kb,
			Slot preferredNameSlot) {
		Set<Cls> types = new HashSet<Cls>();

		if (kb instanceof OWLModel) {
			OWLModel owlModel = (OWLModel) kb;
			types.add(owlModel.getRDFSNamedClassClass());
			types.add(owlModel.getOWLNamedClassClass());

			types.add(owlModel.getRDFPropertyClass());
			types.add(owlModel.getOWLObjectPropertyClass());
			types.add(owlModel.getOWLDatatypePropertyClass());

			types.add(owlModel.getRootCls());
		} else {
			types.add(kb.getRootClsMetaCls());
			types.add(kb.getRootSlotMetaCls());
			types.add(kb.getRootCls());
		}

		for (Cls cls : types) {
			cls.setDirectBrowserSlot(preferredNameSlot);
		}
	}

	/**
	 * Returns a singleton KnowledgeBase instance for given ontology. Assumes
	 * that the OntologyBean object is populated.
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

		if (ob.getFormat().contains(ApplicationConstants.FORMAT_OWL)) {
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

		setBrowserSlotByPreferredNameSlot(kb, getPreferredNameSlot(kb, ob
				.getPreferredNameSlot()));

//		QueryApi api = new QueryApi(kb);
//		installLuceneIndexMechanism(api, ob);

		if (log.isDebugEnabled()) {
			log.debug("Created new knowledgebase: " + kb.getName());
		}

		return kb;
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