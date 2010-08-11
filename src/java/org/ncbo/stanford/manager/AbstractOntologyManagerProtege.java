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
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.RemoteProjectManager;
import edu.stanford.smi.protege.server.util.PingProtegeServerJob;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.creator.OwlDatabaseCreator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngineFactory;

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

	private static String METADATA_TABLE_NAME = "metadata";

	protected Boolean protegeServerEnabled;
	protected String protegeServerHostname;
	protected String protegeServerPort;
	protected String protegeServerUsername;
	protected String protegeServerPassword;
	protected String protegeServerMetaProjectName;
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

	// to collide with the user-uploaded
	// Protege tables

	protected Slot getSynonymSlot(KnowledgeBase kb, String synonymSlot) {
		Slot slot = null;

		if (!StringHelper.isNullOrNullString(synonymSlot)) {
			slot = (kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(synonymSlot) : kb.getSlot(synonymSlot));
		}

		if (slot == null && kb instanceof OWLModel) {
			slot = ((OWLModel) kb)
					.getRDFProperty(OntologyBean.DEFAULT_SYNONYM_SLOT);
		}

		return slot;
	}

	protected Slot getPreferredNameSlot(KnowledgeBase kb,
			String preferredNameSlotName) {
		Slot slot = null;

		if (!StringHelper.isNullOrNullString(preferredNameSlotName)) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(preferredNameSlotName) : kb
					.getSlot(preferredNameSlotName);
		}

		if (slot == null && kb instanceof OWLModel) {
			slot = ((OWLModel) kb)
					.getRDFProperty(OntologyBean.DEFAULT_PREFERRED_NAME_SLOT);
		}

		if (slot == null) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFSLabelProperty() : kb.getNameSlot();
		}

		return slot;
	}

	protected Slot getDefinitionSlot(KnowledgeBase kb, String definitionSlotName) {
		Slot slot = null;

		if (!StringHelper.isNullOrNullString(definitionSlotName)) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(definitionSlotName) : kb
					.getSlot(definitionSlotName);
		}

		if (slot == null) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(OntologyBean.DEFAULT_DEFINITION_SLOT) : kb
					.getSystemFrames().getDocumentationSlot();
		}

		return slot;
	}

	protected Slot getAuthorSlot(KnowledgeBase kb, String authorSlotName) {
		Slot slot = null;

		if (!StringHelper.isNullOrNullString(authorSlotName)) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(authorSlotName) : kb
					.getSlot(authorSlotName);
		}

		if (slot == null) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(OntologyBean.DEFAULT_AUTHOR_SLOT) : kb
					.getSystemFrames().getCreatorSlot();
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
	private void createMetadataKnowledgeBaseInstance() throws Exception {
		List errors = new ArrayList();
		Project p = null;
		String serverPath = protegeServerHostname + ":" + protegeServerPort;

		if (owlModel != null) {
			owlModel.getProject().dispose();
			owlModel = null;
		}

		if (protegeServerEnabled) {
			try {
				p = RemoteProjectManager.getInstance().getProject(serverPath,
						protegeServerUsername, protegeServerPassword,
						protegeServerMetaProjectName, true);
			} catch (Exception e) {
				throw e;
			} catch (Throwable e) {
				throw new Exception(e);
			}

			if (p != null) {
				owlModel = (OWLModel) p.getKnowledgeBase();
			} else {
				throw new Exception(
						"Unable to retrieve remote project. ServerName: "
								+ serverPath + ", Project: "
								+ protegeServerMetaProjectName);
			}
		} else {
			Repository repository = new LocalFolderRepository(
					new File(MessageUtils
							.getMessage("bioportal.metadata.includes.path")));
			OwlDatabaseCreator creator = new OwlDatabaseCreator(false);
			creator.setDriver(protegeJdbcDriver);
			creator.setUsername(protegeJdbcUsername);
			creator.setPassword(protegeJdbcPassword);
			creator.setURL(protegeJdbcUrl);
			creator.setTable(METADATA_TABLE_NAME);
			creator.addRepository(repository);
			creator.create(errors);
			owlModel = creator.getOwlModel();
			owlModel.setChanged(false);

			if (!errors.isEmpty()) {
				log
						.error("Errors during creation of Protege metadata OWL model: "
								+ errors);
			}

			if (log.isDebugEnabled()) {
				log.debug("Created new metadata model: " + owlModel.getName());
			}
		}
	}

	public OWLModel getMetadataOWLModel() throws Exception {
		synchronized (createOwlModelLock) {
			if (owlModel == null) {
				createMetadataKnowledgeBaseInstance();
			}
		}

		return owlModel;
	}

	/**
	 * Programmatically reloads the metadata ontology stored in the memory
	 */
	public void reloadMetadataOWLModel() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Reloading metadata...");
		}

		synchronized (createOwlModelLock) {
			createMetadataKnowledgeBaseInstance();
		}
	}

	public void pingProtegeServer() {
		synchronized (createOwlModelLock) {
			if (owlModel != null && !PingProtegeServerJob.ping(owlModel)) {
				owlModel.getProject().dispose();
				owlModel = null;
				log
						.error("Protege server appears to be down. Discarding Metadata OWL model...");
			}
		}
	}

	protected SQWRLQueryEngine getMetadataSQWRLEngine() throws Exception {
		OWLModel owlModel = getMetadataOWLModel();

		return SQWRLQueryEngineFactory.create(owlModel);
	}

	protected SWRLFactory getMetadataSWRLFactory() throws Exception {
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

	protected String getProtegeURI(String name, OntologyBean ontologyBean) {
		return ApplicationConstants.BASE_CONCEPT_NAMESPACE
				+ ontologyBean.getAbbreviation() + "/" + name;
	}

	protected String getFullId(Frame frame, OntologyBean ontologyBean) {
		return (frame instanceof RDFResource) ? frame.getName()
				: getProtegeURI(frame.getName(), ontologyBean);
	}

	/**
	 * @param protegeServerEnabled
	 *            the protegeServerEnabled to set
	 */
	public void setProtegeServerEnabled(Boolean protegeServerEnabled) {
		this.protegeServerEnabled = protegeServerEnabled;
	}

	/**
	 * @param protegeServerHostname
	 *            the protegeServerHostname to set
	 */
	public void setProtegeServerHostname(String protegeServerHostname) {
		this.protegeServerHostname = protegeServerHostname;
	}

	/**
	 * @param protegeServerPort
	 *            the protegeServerPort to set
	 */
	public void setProtegeServerPort(String protegeServerPort) {
		this.protegeServerPort = protegeServerPort;
	}

	/**
	 * @param protegeServerUsername
	 *            the protegeServerUsername to set
	 */
	public void setProtegeServerUsername(String protegeServerUsername) {
		this.protegeServerUsername = protegeServerUsername;
	}

	/**
	 * @param protegeServerPassword
	 *            the protegeServerPassword to set
	 */
	public void setProtegeServerPassword(String protegeServerPassword) {
		this.protegeServerPassword = protegeServerPassword;
	}

	/**
	 * @param protegeServerMetaProjectName
	 *            the protegeServerMetaProjectName to set
	 */
	public void setProtegeServerMetaProjectName(
			String protegeServerMetaProjectName) {
		this.protegeServerMetaProjectName = protegeServerMetaProjectName;
	}

	/**
	 * @param protegeJdbcUrl
	 *            the protegeJdbcUrl to set
	 */
	public void setProtegeJdbcUrl(String protegeJdbcUrl) {
		this.protegeJdbcUrl = protegeJdbcUrl;
	}

	/**
	 * @param protegeJdbcDriver
	 *            the protegeJdbcDriver to set
	 */
	public void setProtegeJdbcDriver(String protegeJdbcDriver) {
		this.protegeJdbcDriver = protegeJdbcDriver;
	}

	/**
	 * @param protegeJdbcUsername
	 *            the protegeJdbcUsername to set
	 */
	public void setProtegeJdbcUsername(String protegeJdbcUsername) {
		this.protegeJdbcUsername = protegeJdbcUsername;
	}

	/**
	 * @param protegeJdbcPassword
	 *            the protegeJdbcPassword to set
	 */
	public void setProtegeJdbcPassword(String protegeJdbcPassword) {
		this.protegeJdbcPassword = protegeJdbcPassword;
	}

	/**
	 * @param protegeTablePrefix
	 *            the protegeTablePrefix to set
	 */
	public void setProtegeTablePrefix(String protegeTablePrefix) {
		this.protegeTablePrefix = protegeTablePrefix;
	}

	/**
	 * @param protegeTableSuffix
	 *            the protegeTableSuffix to set
	 */
	public void setProtegeTableSuffix(String protegeTableSuffix) {
		this.protegeTableSuffix = protegeTableSuffix;
	}

	/**
	 * @param protegeBigFileThreshold
	 *            the protegeBigFileThreshold to set
	 */
	public void setProtegeBigFileThreshold(Integer protegeBigFileThreshold) {
		this.protegeBigFileThreshold = protegeBigFileThreshold;
	}

	/**
	 * @param metadataUserInstPrefix
	 *            the metadataUserInstPrefix to set
	 */
	public void setMetadataUserInstPrefix(String metadataUserInstPrefix) {
		this.metadataUserInstPrefix = metadataUserInstPrefix;
	}

	/**
	 * @param metadataUserInstSuffix
	 *            the metadataUserInstSuffix to set
	 */
	public void setMetadataUserInstSuffix(String metadataUserInstSuffix) {
		this.metadataUserInstSuffix = metadataUserInstSuffix;
	}

	/**
	 * @param metadataUserRoleInstPrefix
	 *            the metadataUserRoleInstPrefix to set
	 */
	public void setMetadataUserRoleInstPrefix(String metadataUserRoleInstPrefix) {
		this.metadataUserRoleInstPrefix = metadataUserRoleInstPrefix;
	}

	/**
	 * @param metadataUserRoleInstSuffix
	 *            the metadataUserRoleInstSuffix to set
	 */
	public void setMetadataUserRoleInstSuffix(String metadataUserRoleInstSuffix) {
		this.metadataUserRoleInstSuffix = metadataUserRoleInstSuffix;
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
	 * @param metadataOntologyDomainInstSuffix
	 *            the metadataOntologyDomainInstSuffix to set
	 */
	public void setMetadataOntologyDomainInstSuffix(
			String metadataOntologyDomainInstSuffix) {
		this.metadataOntologyDomainInstSuffix = metadataOntologyDomainInstSuffix;
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
	 * @param metadataOntologyGroupInstSuffix
	 *            the metadataOntologyGroupInstSuffix to set
	 */
	public void setMetadataOntologyGroupInstSuffix(
			String metadataOntologyGroupInstSuffix) {
		this.metadataOntologyGroupInstSuffix = metadataOntologyGroupInstSuffix;
	}

	/**
	 * @param metadataOntologyInstPrefix
	 *            the metadataOntologyInstPrefix to set
	 */
	public void setMetadataOntologyInstPrefix(String metadataOntologyInstPrefix) {
		this.metadataOntologyInstPrefix = metadataOntologyInstPrefix;
	}

	/**
	 * @param metadataOntologyInstSuffix
	 *            the metadataOntologyInstSuffix to set
	 */
	public void setMetadataOntologyInstSuffix(String metadataOntologyInstSuffix) {
		this.metadataOntologyInstSuffix = metadataOntologyInstSuffix;
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
	 * @param metadataOntologyViewInstSuffix
	 *            the metadataOntologyViewInstSuffix to set
	 */
	public void setMetadataOntologyViewInstSuffix(
			String metadataOntologyViewInstSuffix) {
		this.metadataOntologyViewInstSuffix = metadataOntologyViewInstSuffix;
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
	 * @param metadataVirtualOntologyInstSuffix
	 *            the metadataVirtualOntologyInstSuffix to set
	 */
	public void setMetadataVirtualOntologyInstSuffix(
			String metadataVirtualOntologyInstSuffix) {
		this.metadataVirtualOntologyInstSuffix = metadataVirtualOntologyInstSuffix;
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
	 * @param metadataVirtualViewInstSuffix
	 *            the metadataVirtualViewInstSuffix to set
	 */
	public void setMetadataVirtualViewInstSuffix(
			String metadataVirtualViewInstSuffix) {
		this.metadataVirtualViewInstSuffix = metadataVirtualViewInstSuffix;
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