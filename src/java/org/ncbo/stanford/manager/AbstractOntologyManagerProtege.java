package org.ncbo.stanford.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.cache.expiration.system.ExpirationSystem;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;

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
	protected String metadataUserInstPrefix;
	protected String metadataUserInstSuffix;
	protected String metadataUserRoleInstPrefix;
	protected String metadataUserRoleInstSuffix;
	protected String metadataOntologyDomainInstPrefix;
	protected String metadataOntologyDomainInstSuffix;
	protected String metadataOntologyGroupInstPrefix;
	protected String metadataOntologyGroupInstSuffix;
	protected String metadataOntologyInstPrefix;
	protected String metadataOntologyInstSuffix;
	protected String metadataOntologyViewInstPrefix;
	protected String metadataOntologyViewInstSuffix;
	protected String metadataVirtualOntologyInstPrefix;
	protected String metadataVirtualOntologyInstSuffix;
	protected String metadataVirtualViewInstPrefix;
	protected String metadataVirtualViewInstSuffix;

	protected ExpirationSystem<Integer, KnowledgeBase> protegeKnowledgeBases = null;
	private OWLModel owlModel = null;
	private Object createOwlModelLock = new Object();
	private String METADATA_TABLE_NAME = "metadata";

	/**
	 * Programmatically reloads the metadata ontology stored in the memory
	 */
	public void reloadMetadataOWLModel() {
		if (log.isDebugEnabled()) {
			log.debug("Reloading metadata...");
		}

		synchronized (createOwlModelLock) {
			if (owlModel != null) {
				owlModel.getProject().dispose();
				owlModel = createMetadataKnowledgeBaseInstance();
			}
		}
	}

	// to collide with the user-uploaded
	// Protege tables

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
	 * Returns a singleton KnowledgeBase instance for given ontologyVersion.
	 */
	protected KnowledgeBase getKnowledgeBase(OntologyBean ontology) {
		KnowledgeBase kb;

		synchronized (protegeKnowledgeBases) {
			kb = (KnowledgeBase) protegeKnowledgeBases.get(ontology.getId());
		}

		if (kb == null) {
			kb = createKnowledgeBaseInstance(ontology);

			synchronized (protegeKnowledgeBases) {
				KnowledgeBase other = protegeKnowledgeBases.get(ontology
						.getId());

				if (other == null) {
					protegeKnowledgeBases.put(ontology.getId(), kb);
				} else {
					kb.getProject().dispose();
					kb = other;
				}
			}
		}

		return kb;
	}

	/**
	 * Gets the Protege ontology associated with the specified ontology id.
	 */
	@SuppressWarnings("unchecked")
	private KnowledgeBase createKnowledgeBaseInstance(OntologyBean ontology) {
		DatabaseKnowledgeBaseFactory factory = null;

		if (ontology.getFormat().contains(ApplicationConstants.FORMAT_OWL)) {
			factory = new OWLDatabaseKnowledgeBaseFactory();
		} else {
			factory = new DatabaseKnowledgeBaseFactory();
		}

		List errors = new ArrayList();
		Project prj = Project.createBuildProject(factory, errors);
		DatabaseKnowledgeBaseFactory.setSources(prj.getSources(),
				protegeJdbcDriver, protegeJdbcUrl, getTableName(ontology
						.getId()), protegeJdbcUsername, protegeJdbcPassword);

		// there is a bug in Protege that leaves a connectionReaper open
		// even if the project threw a RuntimeExceptoin
		try {
			prj.createDomainKnowledgeBase(factory, errors, true);
		} catch (RuntimeException re) {
			prj.dispose();
			throw re;
		}

		KnowledgeBase kb = prj.getKnowledgeBase();

		setBrowserSlotByPreferredNameSlot(kb, getPreferredNameSlot(kb, ontology
				.getPreferredNameSlot()));

		if (log.isDebugEnabled()) {
			log.debug("Created new knowledgebase: " + kb.getName());
		}

		return kb;
	}

	/**
	 * Gets the Metadata ontology instance
	 */
	@SuppressWarnings("unchecked")
	private OWLModel createMetadataKnowledgeBaseInstance() {
		// TODO this solution is a temporary hack.
		// We should use the creator after migration to Protege 3.4.1
		// start...
		DatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();

		List errors = new ArrayList();
		Project project = Project.createBuildProject(factory, errors);
		DatabaseKnowledgeBaseFactory.setSources(project.getSources(),
				protegeJdbcDriver, protegeJdbcUrl, METADATA_TABLE_NAME,
				protegeJdbcUsername, protegeJdbcPassword);
		project.createDomainKnowledgeBase(factory, errors, false);
		KnowledgeBase kb = project.getKnowledgeBase();
		OWLModel owlModel = (OWLModel) kb;
		Repository repository = new LocalFolderRepository(new File(MessageUtils
				.getMessage("bioportal.metadata.includes.path")), true);
		owlModel.getRepositoryManager().addProjectRepository(repository);

		MergingNarrowFrameStore mnfs = MergingNarrowFrameStore.get(owlModel);
		owlModel.setGenerateEventsEnabled(false);
		NarrowFrameStore nfs = factory.createNarrowFrameStore("<new>");
		mnfs.addActiveFrameStore(nfs);
		factory.loadKnowledgeBase(owlModel, project.getSources(), errors);
		owlModel.setGenerateEventsEnabled(true);
		owlModel.setChanged(false);
		project.getInternalProjectKnowledgeBase().setChanged(false);
		// end

		if (!errors.isEmpty()) {
			log.error("Errors during Protege metadata project creation: "
					+ errors);
		}

		if (log.isDebugEnabled()) {
			log.debug("Created new metadata knowledgebase: " + kb.getName());
		}

		return owlModel;
	}

	protected OWLModel getMetadataOWLModel() {
		synchronized (createOwlModelLock) {
			if (owlModel == null) {
				owlModel = createMetadataKnowledgeBaseInstance();
			}
		}

		return owlModel;
	}

	protected SQWRLQueryEngine getMetadataSQWRLEngine() throws SQWRLException {
		OWLModel owlModel = getMetadataOWLModel();

		return SQWRLQueryEngineFactory.create(owlModel);
	}

	protected SWRLFactory getMetadataSWRLFactory() throws SQWRLException {
		OWLModel owlModel = getMetadataOWLModel();

		return new SWRLFactory(owlModel);
	}

	/**
	 * Gets the table name associated with an protege ontology id.
	 */
	protected String getTableName(Integer ontologyVersionId) {
		return protegeTablePrefix + ontologyVersionId + protegeTableSuffix;
	}

	/**
	 * Gets the BioPortalUser individual name associated with a user id.
	 */
	protected String getUserIndividualName(Integer userId) {
		return metadataUserInstPrefix + userId + metadataUserInstSuffix;
	}

	/**
	 * Gets the BioPortalUserRole individual name associated with a user role
	 * id.
	 */
	protected String getUserRoleIndividualName(Integer userId) {
		return metadataUserRoleInstPrefix + userId + metadataUserRoleInstSuffix;
	}

	/**
	 * Gets the OMV:OntologyDomain individual name associated with a category
	 * id.
	 */
	protected String getOntologyDomainIndividualName(Integer catId) {
		return metadataOntologyDomainInstPrefix + catId
				+ metadataOntologyDomainInstSuffix;
	}

	/**
	 * Gets the OntologyGroup individual name associated with a group id.
	 */
	protected String getOntologyGroupIndividualName(Integer groupId) {
		return metadataOntologyGroupInstPrefix + groupId
				+ metadataOntologyGroupInstSuffix;
	}

	/**
	 * Gets the OMV:Ontology individual name associated with an ontology version
	 * id.
	 */
	protected String getOntologyIndividualName(Integer ontologyVersionId) {
		return metadataOntologyInstPrefix + ontologyVersionId
				+ metadataOntologyInstSuffix;
	}

	/**
	 * Gets the OntologyView individual name associated with an ontology view
	 * version id.
	 */
	protected String getOntologyViewIndividualName(Integer ontologyViewVersionId) {
		return metadataOntologyViewInstPrefix + ontologyViewVersionId
				+ metadataOntologyViewInstSuffix;
	}

	/**
	 * Gets the VirtualOntology individual name associated with a virtual
	 * ontology id.
	 */
	protected String getVirtualOntologyIndividualName(Integer ontologyId) {
		return metadataVirtualOntologyInstPrefix + ontologyId
				+ metadataVirtualOntologyInstSuffix;
	}

	/**
	 * Gets the VirtualView individual name associated with a virtual ontology
	 * view id.
	 */
	protected String getVirtualViewIndividualName(Integer ontologyViewId) {
		return metadataVirtualViewInstPrefix + ontologyViewId
				+ metadataVirtualViewInstSuffix;
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
	 * @return the metadataUserInstPrefix
	 */
	public String getMetadataUserInstPrefix() {
		return metadataUserInstPrefix;
	}

	/**
	 * @param metadataUserInstPrefix
	 *            the metadataUserInstPrefix to set
	 */
	public void setMetadataUserInstPrefix(String metadataUserInstPrefix) {
		this.metadataUserInstPrefix = metadataUserInstPrefix;
	}

	/**
	 * @return the metadataUserInstSuffix
	 */
	public String getMetadataUserInstSuffix() {
		return metadataUserInstSuffix;
	}

	/**
	 * @param metadataUserInstSuffix
	 *            the metadataUserInstSuffix to set
	 */
	public void setMetadataUserInstSuffix(String metadataUserInstSuffix) {
		this.metadataUserInstSuffix = metadataUserInstSuffix;
	}

	/**
	 * @return the metadataUserRoleInstPrefix
	 */
	public String getMetadataUserRoleInstPrefix() {
		return metadataUserRoleInstPrefix;
	}

	/**
	 * @param metadataUserRoleInstPrefix
	 *            the metadataUserRoleInstPrefix to set
	 */
	public void setMetadataUserRoleInstPrefix(String metadataUserRoleInstPrefix) {
		this.metadataUserRoleInstPrefix = metadataUserRoleInstPrefix;
	}

	/**
	 * @return the metadataUserRoleInstSuffix
	 */
	public String getMetadataUserRoleInstSuffix() {
		return metadataUserRoleInstSuffix;
	}

	/**
	 * @param metadataUserRoleInstSuffix
	 *            the metadataUserRoleInstSuffix to set
	 */
	public void setMetadataUserRoleInstSuffix(String metadataUserRoleInstSuffix) {
		this.metadataUserRoleInstSuffix = metadataUserRoleInstSuffix;
	}

	/**
	 * @return the metadataOntologyDomainInstPrefix
	 */
	public String getMetadataOntologyDomainInstPrefix() {
		return metadataOntologyDomainInstPrefix;
	}

	/**
	 * @param metadataOntologyDomainInstPrefix
	 *            the metadataOntologyDomainInstPrefix to set
	 */
	public void setMetadataOntologyDomainInstPrefix(
			String metadataOntologyDomainInstPrefix) {
		this.metadataOntologyDomainInstPrefix = metadataOntologyDomainInstPrefix;
	}

	/**
	 * @return the metadataOntologyDomainInstSuffix
	 */
	public String getMetadataOntologyDomainInstSuffix() {
		return metadataOntologyDomainInstSuffix;
	}

	/**
	 * @param metadataOntologyDomainInstSuffix
	 *            the metadataOntologyDomainInstSuffix to set
	 */
	public void setMetadataOntologyDomainInstSuffix(
			String metadataOntologyDomainInstSuffix) {
		this.metadataOntologyDomainInstSuffix = metadataOntologyDomainInstSuffix;
	}

	/**
	 * @return the metadataOntologyGroupInstPrefix
	 */
	public String getMetadataOntologyGroupInstPrefix() {
		return metadataOntologyGroupInstPrefix;
	}

	/**
	 * @param metadataOntologyGroupInstPrefix
	 *            the metadataOntologyGroupInstPrefix to set
	 */
	public void setMetadataOntologyGroupInstPrefix(
			String metadataOntologyGroupInstPrefix) {
		this.metadataOntologyGroupInstPrefix = metadataOntologyGroupInstPrefix;
	}

	/**
	 * @return the metadataOntologyGroupInstSuffix
	 */
	public String getMetadataOntologyGroupInstSuffix() {
		return metadataOntologyGroupInstSuffix;
	}

	/**
	 * @param metadataOntologyGroupInstSuffix
	 *            the metadataOntologyGroupInstSuffix to set
	 */
	public void setMetadataOntologyGroupInstSuffix(
			String metadataOntologyGroupInstSuffix) {
		this.metadataOntologyGroupInstSuffix = metadataOntologyGroupInstSuffix;
	}

	/**
	 * @return the metadataOntologyInstPrefix
	 */
	public String getMetadataOntologyInstPrefix() {
		return metadataOntologyInstPrefix;
	}

	/**
	 * @param metadataOntologyInstPrefix
	 *            the metadataOntologyInstPrefix to set
	 */
	public void setMetadataOntologyInstPrefix(String metadataOntologyInstPrefix) {
		this.metadataOntologyInstPrefix = metadataOntologyInstPrefix;
	}

	/**
	 * @return the metadataOntologyInstSuffix
	 */
	public String getMetadataOntologyInstSuffix() {
		return metadataOntologyInstSuffix;
	}

	/**
	 * @param metadataOntologyInstSuffix
	 *            the metadataOntologyInstSuffix to set
	 */
	public void setMetadataOntologyInstSuffix(String metadataOntologyInstSuffix) {
		this.metadataOntologyInstSuffix = metadataOntologyInstSuffix;
	}

	/**
	 * @return the metadataOntologyViewInstPrefix
	 */
	public String getMetadataOntologyViewInstPrefix() {
		return metadataOntologyViewInstPrefix;
	}

	/**
	 * @param metadataOntologyViewInstPrefix
	 *            the metadataOntologyViewInstPrefix to set
	 */
	public void setMetadataOntologyViewInstPrefix(
			String metadataOntologyViewInstPrefix) {
		this.metadataOntologyViewInstPrefix = metadataOntologyViewInstPrefix;
	}

	/**
	 * @return the metadataOntologyViewInstSuffix
	 */
	public String getMetadataOntologyViewInstSuffix() {
		return metadataOntologyViewInstSuffix;
	}

	/**
	 * @param metadataOntologyViewInstSuffix
	 *            the metadataOntologyViewInstSuffix to set
	 */
	public void setMetadataOntologyViewInstSuffix(
			String metadataOntologyViewInstSuffix) {
		this.metadataOntologyViewInstSuffix = metadataOntologyViewInstSuffix;
	}

	/**
	 * @return the metadataVirtualOntologyInstPrefix
	 */
	public String getMetadataVirtualOntologyInstPrefix() {
		return metadataVirtualOntologyInstPrefix;
	}

	/**
	 * @param metadataVirtualOntologyInstPrefix
	 *            the metadataVirtualOntologyInstPrefix to set
	 */
	public void setMetadataVirtualOntologyInstPrefix(
			String metadataVirtualOntologyInstPrefix) {
		this.metadataVirtualOntologyInstPrefix = metadataVirtualOntologyInstPrefix;
	}

	/**
	 * @return the metadataVirtualOntologyInstSuffix
	 */
	public String getMetadataVirtualOntologyInstSuffix() {
		return metadataVirtualOntologyInstSuffix;
	}

	/**
	 * @param metadataVirtualOntologyInstSuffix
	 *            the metadataVirtualOntologyInstSuffix to set
	 */
	public void setMetadataVirtualOntologyInstSuffix(
			String metadataVirtualOntologyInstSuffix) {
		this.metadataVirtualOntologyInstSuffix = metadataVirtualOntologyInstSuffix;
	}

	/**
	 * @return the metadataVirtualViewInstPrefix
	 */
	public String getMetadataVirtualViewInstPrefix() {
		return metadataVirtualViewInstPrefix;
	}

	/**
	 * @param metadataVirtualViewInstPrefix
	 *            the metadataVirtualViewInstPrefix to set
	 */
	public void setMetadataVirtualViewInstPrefix(
			String metadataVirtualViewInstPrefix) {
		this.metadataVirtualViewInstPrefix = metadataVirtualViewInstPrefix;
	}

	/**
	 * @return the metadataVirtualViewInstSuffix
	 */
	public String getMetadataVirtualViewInstSuffix() {
		return metadataVirtualViewInstSuffix;
	}

	/**
	 * @param metadataVirtualViewInstSuffix
	 *            the metadataVirtualViewInstSuffix to set
	 */
	public void setMetadataVirtualViewInstSuffix(
			String metadataVirtualViewInstSuffix) {
		this.metadataVirtualViewInstSuffix = metadataVirtualViewInstSuffix;
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
}