package org.ncbo.stanford.service.loader.remote.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ContactTypeBean;
import org.ncbo.stanford.bean.MetadataFileBean;
import org.ncbo.stanford.bean.OBORepositoryInfoHolder;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.InvalidDataException;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.service.loader.remote.OBOCVSPullService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.cvs.CVSFile;
import org.ncbo.stanford.util.cvs.CVSUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.helper.reflection.ReflectionHelper;
import org.ncbo.stanford.util.loader.LoaderUtils;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.PhysicalDirectoryFilePathHandlerImpl;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.URIUploadFilePathHandlerImpl;
import org.ncbo.stanford.util.svn.SVNUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A utility that runs as a scheduled process, connecting to OBO Sourceforge CVS
 * system and pulling all new and updated ontologies into BioPortal
 * 
 * @author Michael Dorf
 */
@Transactional
public class OBOCVSPullServiceImpl implements OBOCVSPullService {

	private static final Log log = LogFactory
			.getLog(OBOCVSPullServiceImpl.class);

	private static final String BIOLOGICAL_PROCESS_OBO_FOUNDRY_ID = "biological_process";
	private static final String CELLULAR_COMPONENT_OBO_FOUNDRY_ID = "cellular_component";
	private static final String MOLECULAR_FUNCTION_OBO_FOUNDRY_ID = "molecular_function";

	private OntologyService ontologyService;
	private UserService userService;
	private Map<String, String> ontologyFormatToOBOFoundryMap = new HashMap<String, String>();
	private Map<String, String> ontologyVersionStatusToOBOFoundryMap = new HashMap<String, String>();
	private Map<String, Byte> ontologyFoundryToOBOFoundryMap = new HashMap<String, Byte>();
	private String tempDir;
	private String oboRepositoriesConfigFilepath;

	/**
	 * Used to the identify the action to be performed on an ontology
	 */
	private enum ActionEnum {
		NO_ACTION, CREATE_REPOSITORY_ACTION, CREATE_DOWNLOAD_ACTION, CREATE_METADATA_ONLY_ACTION, UPDATE_ACTION
	}

	/**
	 * Performs the pull of ontologies from OBO Sourceforge CVS
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void doOntologyPull() {
		try {
			Set<String> processed_oboFoundryIds = doCVSPull();
			// Set<String> processed_oboFoundryId_set= new HashSet<String>();
			doRemoteOntologyPull(processed_oboFoundryIds);
			if (log.isInfoEnabled()) {
				log.info("**** Ontology Pull completed successfully *****");
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	private void doRemoteOntologyPull(Set<String> processed_oboFoundryIds) {
		try {
			List<OntologyBean> ont_list = ontologyService
					.findLatestOntologyVersions();
			if (log.isInfoEnabled()) {
				log.info("**** Started Remote Ontology Pull *****");
			}
			for (OntologyBean ont : ont_list) {
				if ((!processed_oboFoundryIds.contains(ont.getOboFoundryId()))
						&& LoaderUtils.isValidDownloadLocation(ont
								.getDownloadLocation())) {
					// Process only the ontologies that are not marked manual
					if (ont.getIsManual() != null
							&& ont.getIsManual().byteValue() == ApplicationConstants.FALSE) {

						if (LoaderUtils.hasDownloadLocationBeenUpdated(ont
								.getDownloadLocation(), ont)) {
							processCreateNewVersion(ont);

						} else {
							log.debug("[*** NO_ACTION: " + ont + " ***]");
						}
					}
				}

			}
		} catch (Exception ex) {
			log.error(ex);
			ex.printStackTrace();
		}
		if (log.isInfoEnabled()) {
			log.info("**** End Remote Ontology Pull *****");
		}

	}

	private void processCreateNewVersion(OntologyBean ont) {
		try {
			Date now = Calendar.getInstance().getTime();
			ont.setDateReleased(now);
			ont.setDateCreated(now);
			ont.setId(null);
			// reset the status and coding scheme
			ont.setStatusId(null);
			ont.setCodingScheme(null);
			ont.setVersionNumber(null);
			// The file is not in the local cvs/svn repository, but
			// can be downloaded using the downloadLocation
			String downloadLocation = ont.getDownloadLocation();
			FilePathHandler filePathHandler = new URIUploadFilePathHandlerImpl(
					CompressedFileHandlerFactory.createFileHandler(ont
							.getFormat()), new URI(downloadLocation));
			log.debug("[*** CREATE new version: " + ont + " ***]");
			ontologyService.createOntologyOrView(ont, filePathHandler);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}

	}

	/**
	 * Performs the pull of ontologies from OBO Sourceforge CVS
	 */
	private Set<String> doCVSPull() {
		Set<String> processed_OboFoundryId_set = new HashSet<String>();

		try {
			if (log.isInfoEnabled()) {
				log.info("**** Starting Repository Pull *****");
			}
			List<OBORepositoryInfoHolder> repos = parseRepositoryConfigFile();

			HashMap<String, CVSFile> updateFiles = null;
			for (OBORepositoryInfoHolder repo : repos) {
				if (repo.getRepositoryType().equals("SVN")) {
					SVNUtils svnUtils = new SVNUtils(repo.getUsername(), repo
							.getPassword(), repo.getHostname(), repo
							.getModule(), repo.getRootdirectory(), repo
							.getArgumentstring(), repo.getCheckoutdir(),
							tempDir);
					svnUtils.svnCheckout();
					updateFiles = svnUtils.listEntries();
				} else {
					CVSUtils cvsUtils = new CVSUtils(repo.getUsername(), repo
							.getPassword(), repo.getHostname(), repo
							.getModule(), repo.getRootdirectory(), repo
							.getArgumentstring(), repo.getCheckoutdir(),
							tempDir);
					cvsUtils.cvsCheckout();
					updateFiles = cvsUtils.getAllCVSEntries();
				}
				// process repository data.
				Set<String> processed_set = processRecords(repo, updateFiles);
				processed_OboFoundryId_set.addAll(processed_set);
			}

			if (log.isInfoEnabled()) {
				log.info("**** End of repository pull *****");
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return processed_OboFoundryId_set;

	}

	private Set<String> processRecords(OBORepositoryInfoHolder repo,
			HashMap<String, CVSFile> updateFiles) throws IOException {
		OntologyDescriptorParser odp = new OntologyDescriptorParser(repo
				.getDescriptorlocation());
		List<MetadataFileBean> ontologyList = odp.parseOntologyFile();
		Set<String> processed_OboFoundryId_set = new HashSet<String>();

		for (MetadataFileBean mfb : ontologyList) {
			try {
				String format = getFormat(mfb.getFormat());

				if (format.equals(ApplicationConstants.FORMAT_INVALID)) {
					throw new InvalidOntologyFormatException(
							"The ontology format, [" + mfb.getFormat()
									+ "] for ontology [" + mfb.getId()
									+ "] is invalid");
				}

				CVSFile cf = null;
				String filename = OntologyDescriptorParser
						.getFileName(getDownloadLocation(mfb));
				boolean isEmptyFilename = StringHelper
						.isNullOrNullString(filename);

				if (!isEmptyFilename) {
					cf = (CVSFile) updateFiles.get(filename);
				}

				OntologyAction ontologyAction = determineOntologyAction(mfb,
						cf, repo.getHostname());
				ActionEnum action = ontologyAction.getAction();
				OntologyBean ont = ontologyAction.getOntotlogyBean();
				if (ont != null
						&& StringUtils.isNotEmpty(ont.getOboFoundryId())) {
					processed_OboFoundryId_set.add(ont.getOboFoundryId());
				}

				FilePathHandler filePathHandler;
				switch (action) {

				case CREATE_REPOSITORY_ACTION:
					if (isEmptyFilename) {
						throw new InvalidDataException(
								"No filename is specified in the metadata descriptor file for ontology ["
										+ mfb.getId() + "]");
					}

					String path = cf.getPath();
					String checkoutdir = repo.getCheckoutdir();

					if (!path.contains(checkoutdir)) {
						path = checkoutdir + "/" + path;
					}

					filePathHandler = new PhysicalDirectoryFilePathHandlerImpl(
							CompressedFileHandlerFactory
									.createFileHandler(format), new File(path));

					ontologyService.createOntologyOrView(ont, filePathHandler);
					break;
				case CREATE_DOWNLOAD_ACTION:
					if (isEmptyFilename) {
						throw new InvalidDataException(
								"No filename is specified in the metadata descriptor file for ontology ["
										+ mfb.getId() + "]");
					}

					// The file is not in the local cvs/svn repository, but
					// can be downloaded using the downloadLocation
					String downloadLocation = ont.getDownloadLocation();

					filePathHandler = new URIUploadFilePathHandlerImpl(
							CompressedFileHandlerFactory
									.createFileHandler(format), new URI(
									downloadLocation));

					ontologyService.createOntologyOrView(ont, filePathHandler);
					break;
				case CREATE_METADATA_ONLY_ACTION:
					ontologyService.createOntologyOrView(ont, null);
					break;
				case UPDATE_ACTION:
					ontologyService.cleanupOntologyCategory(ont);
					ontologyService.updateOntologyOrView(ont);
					break;
				}
			} catch (InvalidOntologyFormatException ex) {
				log.error(ex);
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		}
		return processed_OboFoundryId_set;
	}

	/**
	 * Determines the action to be executed
	 * 
	 * @param mfb
	 * @param cf
	 * @return
	 * @throws InvalidDataException
	 */
	private OntologyAction determineOntologyAction(MetadataFileBean mfb,
			CVSFile cf, String cvsHostname) throws Exception {
		ActionEnum action = ActionEnum.NO_ACTION;
		String oboFoundryId = mfb.getId();

		OntologyBean ont = ontologyService
				.findLatestOntologyVersionByOboFoundryId(oboFoundryId);
		String downloadUrl = getDownloadLocation(mfb);
		List<Integer> newCategoryIds = findCategoryIdsByOBONames(downloadUrl);
		byte isMetadataOnly = isMetadataOnly(downloadUrl, cvsHostname);

		// is any action required?
		// ____a. this is not cellular_component or molecular_function ontology
		// ____b. local && categories didn't change
		// is this an update action?
		// ____a. remote && exists in the system
		// ____b. local && categories changed

		// The logic that deals with biological_process, cellular_component,
		// and mollecular_function ontologies is designed to deal with the
		// LexGrid limitation, which can't determine namespaces. This creates
		// a problem with searching. The ontologies are replicated within each
		// other. The code will ignore cellular_component and molecular_function
		// and turn biological_process into the main GO ontology

		if (oboFoundryId.equalsIgnoreCase(BIOLOGICAL_PROCESS_OBO_FOUNDRY_ID)) {
			mfb.setTitle(MessageUtils.getMessage("gene.ontology.title"));
			mfb.setDescription(MessageUtils
					.getMessage("gene.ontology.description"));
		}

		if (!oboFoundryId.equalsIgnoreCase(CELLULAR_COMPONENT_OBO_FOUNDRY_ID)
				&& !oboFoundryId
						.equalsIgnoreCase(MOLECULAR_FUNCTION_OBO_FOUNDRY_ID)) {
			if (ont == null) {
				// new ontology
				if (isMetadataOnly == ApplicationConstants.TRUE) {
					action =ActionEnum.CREATE_METADATA_ONLY_ACTION;
				} else if (isHostedInCurrentRepository(downloadUrl, cvsHostname)) {
					action =ActionEnum.CREATE_REPOSITORY_ACTION;
				} else {
					action =ActionEnum.CREATE_DOWNLOAD_ACTION;
				}				
				ont = new OntologyBean(false);
			} else if (ont.getIsManual() != null
					&& ont.getIsManual().byteValue() == ApplicationConstants.FALSE) {
				if (isMetadataOnly == ApplicationConstants.TRUE) {
					if (hasVersions(ont) && ont.isMetadataOnly()) {
						// existing ontology that had been and remains metadata
						// only. We are updating to ensure that the isMetadata flag begins to get populated.
						action = ActionEnum.UPDATE_ACTION;
					} else {
						// existing ontology that had been local but is now
						// remote or an ontology with no versions
						action = ActionEnum.CREATE_METADATA_ONLY_ACTION;
					}
				} else if (!isHostedInCurrentRepository(downloadUrl, cvsHostname) ) {
					if (LoaderUtils.hasDownloadLocationBeenUpdated(downloadUrl, ont)) {
					   action = ActionEnum.CREATE_DOWNLOAD_ACTION;
					}
				}
				
				else if (hasVersions(ont) && cf != null
						&& cf.getVersion().equals(ont.getVersionNumber())) {
					// existing ontology; no new version found
					// check if categories and downloadLocation has been
					// updated
					List<Integer> oldCategoryIds = ont.getCategoryIds();
					boolean categoriesUpdated = isCategoryUpdated(
							oldCategoryIds, newCategoryIds);
					if (categoriesUpdated) {
						action = ActionEnum.UPDATE_ACTION;
					}
					// Check if the metadata needs to be updated
					if (needsUpdateAction(ont, downloadUrl, isMetadataOnly)) {
						action = ActionEnum.UPDATE_ACTION;

					}

				} else if (cf != null) {
					// existing ontology local; new version
					action = ActionEnum.CREATE_REPOSITORY_ACTION; 
				} else if (needsUpdateAction(ont, downloadUrl, isMetadataOnly)) {
					// Check if the metadata needs to be updated
					action = ActionEnum.UPDATE_ACTION;
				}

			}

			populateOntologyBean(mfb, cf, action, ont, newCategoryIds,
					isMetadataOnly);
		}

		OntologyAction ontologyAction = new OntologyAction(action, ont);

		if (log.isDebugEnabled()) {
			if (ont == null) {
				log.debug("[*** " + action + ": " + oboFoundryId + " ***]");
			} else {
				log.debug(ontologyAction);
			}
		}

		return ontologyAction;
	}

	private String getDownloadLocation(MetadataFileBean mfb) {
		String downloadLocation = mfb.getDownload();
		if (StringHelper.isNullOrNullString(downloadLocation)) {
			downloadLocation = mfb.getSource();
			if (downloadLocation.contains("|")) {
				int pipeIndex = downloadLocation.indexOf("|");
				if (pipeIndex + 1 < downloadLocation.length()) {
					downloadLocation = downloadLocation
							.substring(pipeIndex + 1);
				}
			}
		}
		return downloadLocation;
	}

	private boolean hasVersions(OntologyBean ob) {
		return ob.getId() != OntologyMetadataUtils.INVALID_ID;
	}

	/**
	 * Populates ontology bean based on the action
	 * 
	 * @param mfb
	 * @param cf
	 * @param action
	 * @param ont
	 * @param newCategoryIds
	 * @param isMetadataOnly
	 * @throws InvalidDataException
	 */
	private void populateOntologyBean(MetadataFileBean mfb, CVSFile cf,
			ActionEnum action, OntologyBean ont, List<Integer> newCategoryIds,
			byte isMetadataOnly) throws InvalidDataException {
		Date now = Calendar.getInstance().getTime();
		String downloadLocation = getDownloadLocation(mfb);

		if (action != ActionEnum.NO_ACTION) {
			if (action == ActionEnum.CREATE_METADATA_ONLY_ACTION) {
				ont.setVersionNumber(MessageUtils
						.getMessage("remote.ontology.version"));
				ont.setDateReleased(now);
				/**
				 * Pradip: Don't think we need this anymore...We now populate
				 * the downloadLocation instead if
				 * (!StringHelper.isNullOrNullString(downloadUrl)) {
				 * ont.setFilePath(downloadUrl); } else if
				 * (!StringHelper.isNullOrNullString(sourceUrl)) {
				 * ont.setFilePath(sourceUrl); }
				 */
			} else {
				if (action == ActionEnum.CREATE_REPOSITORY_ACTION) {
					ont.setVersionNumber(cf.getVersion());
					ont.setDateReleased(cf.getTimeStamp().getTime());
				} else if (action == ActionEnum.CREATE_DOWNLOAD_ACTION) {
					ont.setVersionNumber(null);
					ont.setDateReleased(now);
				}
				ont.getFilenames().clear();
				ont.addFilename(OntologyDescriptorParser
						.getFileName(downloadLocation));
				ont.setCategoryIds(newCategoryIds);
				// TODO: What does this part of the method do??? (Csongor)
				// We need to deal with information about groups, too
			}

			if (action != ActionEnum.UPDATE_ACTION) {
				ont.setDateCreated(now);
				ont.setId(null);
				// reset the status and coding scheme
				ont.setStatusId(null);
				ont.setUrn(null);
				ont.setCodingScheme(null);
			}

			UserBean userBean = linkOntologyUser(mfb.getContact(), mfb.getId());
			if (isMetadataOnly == ApplicationConstants.FALSE) {
				ont.setDownloadLocation(downloadLocation);
			}
			ont.setUserId(userBean.getId());
			ont.setVersionStatus(getStatus(mfb.getStatus()));
			ont.setIsRemote(ApplicationConstants.TRUE);
			ont.setIsReviewed(ApplicationConstants.TRUE);
			ont.setOboFoundryId(mfb.getId());
			ont.setDisplayLabel(mfb.getTitle());
			ont.setDescription(mfb.getDescription());
			ont.setAbbreviation(mfb.getNamespace());
			ont.setFormat(getFormat(mfb.getFormat()));
			ont.setContactName(userBean.getFirstname() + " "
					+ userBean.getLastname());
			ont.setContactEmail(userBean.getEmail());
			ont
					.setHomepage(OntologyDescriptorParser.getHomepage(mfb
							.getHome()));
			ont.setDocumentation(OntologyDescriptorParser.getDocumentation(mfb
					.getDocumentation()));
			ont.setPublication(OntologyDescriptorParser.getPublication(mfb
					.getPublication()));
			ont.setIsFoundry(new Byte(isFoundry(mfb.getFoundry())));
			ont.setIsMetadataOnly(isMetadataOnly);
			ont.setIsManual(ApplicationConstants.FALSE);
		}
	}

	/**
	 * Determines whether the two category id lists are equal
	 * 
	 * @param oldCategoryIds
	 * @param newCategoryIds
	 * @return
	 */
	private boolean isCategoryUpdated(List<Integer> oldCategoryIds,
			List<Integer> newCategoryIds) {
		Collections.sort(oldCategoryIds);
		Collections.sort(newCategoryIds);

		return !ListUtils.isEqualList(oldCategoryIds, newCategoryIds);
	}

	/**
	 * Extract category ids from the download string
	 * 
	 * @param downloadUrl
	 * @return
	 */
	private List<Integer> findCategoryIdsByOBONames(String downloadUrl)
			throws Exception {
		List<Integer> categoryIds = new ArrayList<Integer>(1);

		if (!StringHelper.isNullOrNullString(downloadUrl)) {
			categoryIds = ontologyService
					.findCategoryIdsByOBOFoundryNames(downloadUrl.split("/"));
		}

		return categoryIds;
	}

	/**
	 * Determines whether the ontology is hosted in the current repository
	 * 
	 * @param downloadUrl
	 * @param cvsHostname
	 * @return
	 */
	private boolean isHostedInCurrentRepository(String downloadUrl,
			String cvsHostname) {
		boolean isHosted = false;

		if (!StringHelper.isNullOrNullString(downloadUrl)
				&& downloadUrl.indexOf(cvsHostname) > -1) {
			isHosted = true;
		}

		return isHosted;
	}

	/**
	 * Determines whether the ontology is metadataOnly
	 * 
	 * @param downloadUrl
	 * @param cvsHostname
	 * @return
	 */
	private byte isMetadataOnly(String downloadUrl, String cvsHostname) {
		byte isMetadataOnly = ApplicationConstants.TRUE;

		if (!StringHelper.isNullOrNullString(downloadUrl)){
			isMetadataOnly = ApplicationConstants.FALSE;
		}

		return isMetadataOnly;
	}

	/**
	 * Determines whether the ontology's metadata needs to be updated
	 * 
	 * @param downloadUrl
	 * @param isMetadataOnly
	 * @return
	 */
	private boolean needsUpdateAction(OntologyBean ont, String downloadUrl,
			byte isMetadataOnly) {
		boolean needsUpdate = false;
		// Check if the download location has changed
		if (StringUtils.isNotBlank(downloadUrl)) {
			if (!downloadUrl.equalsIgnoreCase(ont.getDownloadLocation())) {
				needsUpdate = true;
			}
		}
		// Check if the ontology has isRemote=0; if so, we need to have it
		// updated to 1
		if (ont.getIsRemote() == null
				|| ont.getIsRemote() == ApplicationConstants.FALSE) {
			needsUpdate = true;
		}
		// Check if the ontology's getMetadataFlag== isMetadataFlag
		if (ont.getIsMetadataOnly() == null
				|| ont.getIsMetadataOnly() != isMetadataOnly) {
			needsUpdate = true;
		}
		return needsUpdate;
	}

	/**
	 * Returns the foundry preference (mapped in the Spring config file)
	 * 
	 * @param str
	 * @return
	 */
	private byte isFoundry(String str) {
		byte foundry = ApplicationConstants.FALSE;

		if (ontologyFoundryToOBOFoundryMap.containsKey(str)) {
			foundry = ontologyFoundryToOBOFoundryMap.get(str).byteValue();
		}

		return foundry;
	}

	/**
	 * Returns the version status (mapped in the Spring config file)
	 * 
	 * @param str
	 * @return
	 */
	private String getFormat(String str) {
		String format = ApplicationConstants.FORMAT_INVALID;

		if (!StringHelper.isNullOrNullString(str)
				&& ontologyFormatToOBOFoundryMap.containsKey(str.toLowerCase())) {
			format = ontologyFormatToOBOFoundryMap.get(str.toLowerCase());
		}

		return format;
	}

	/**
	 * Returns the version status (mapped in the Spring config file)
	 * 
	 * @param str
	 * @return
	 */
	private String getStatus(String str) {
		String status = ApplicationConstants.VERSION_STATUS_PREPRODUCTION;

		if (ontologyVersionStatusToOBOFoundryMap.containsKey(str)) {
			status = ontologyVersionStatusToOBOFoundryMap.get(str);
		}

		return status;
	}

	private UserBean linkOntologyUser(String contactString, String oboFoundryId)
			throws InvalidDataException {
		ContactTypeBean contact = OntologyDescriptorParser.getContact(
				contactString, oboFoundryId);
		UserBean userBean = userService.findUser(contact.getEmail());

		// create a new user
		if (userBean == null) {
			userBean = new UserBean();
			userBean.generateDefaultPassword();
			userBean.setUsername(contact.getEmail());
			userBean.setEmail(contact.getEmail());
			userBean.setFirstname(contact.getFirstName());
			userBean.setLastname(contact.getLastName());
			userBean.setDateCreated(Calendar.getInstance().getTime());
			userService.createUser(userBean);
		}

		return userBean;
	}

	private class OntologyAction {
		ActionEnum action;
		OntologyBean ontologyBean;

		/**
		 * @param action
		 * @param ontologyBean
		 */
		private OntologyAction(ActionEnum action, OntologyBean ontologyBean) {
			this.action = action;
			this.ontologyBean = ontologyBean;
		}

		/**
		 * @return the action
		 */
		private ActionEnum getAction() {
			return action;
		}

		/**
		 * @return the ontologyBean
		 */
		private OntologyBean getOntotlogyBean() {
			return ontologyBean;
		}

		public String toString() {
			return "[*** " + action + ": " + ontologyBean + " ***]";
		}
	}

	private List<OBORepositoryInfoHolder> parseRepositoryConfigFile()
			throws Exception {
		List<OBORepositoryInfoHolder> repositories = new ArrayList<OBORepositoryInfoHolder>(
				0);
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(oboRepositoriesConfigFilepath);
		Element root = doc.getDocumentElement();

		for (Node child = root.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {

				NodeList repoInfo = child.getChildNodes();
				OBORepositoryInfoHolder infoHolder = new OBORepositoryInfoHolder();
				NamedNodeMap attributes = child.getAttributes();

				if (attributes != null) {
					for (int i = 0; i < attributes.getLength(); i++) {
						Node attribute = attributes.item(i);
						if (attribute.getNodeName().toString().equals("type")) {
							infoHolder.setRepositoryType(attribute
									.getNodeValue());
						}
					}
				}

				for (int i = 0; i < repoInfo.getLength(); i++) {
					Node node = repoInfo.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {
						// convert label to property and set
						// its value using Reflection
						ReflectionHelper.setProperty(infoHolder, node
								.getNodeName(), node.getTextContent().trim());
					}
				}

				repositories.add(infoHolder);
			}
		}

		return repositories;
	}

	/**
	 * @param tempDir
	 *            the tempDir to set
	 */
	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * @param ontologyFormatToOBOFoundryMap
	 *            the ontologyFormatToOBOFoundryMap to set
	 */
	public void setOntologyFormatToOBOFoundryMap(
			Map<String, String> ontologyFormatToOBOFoundryMap) {
		this.ontologyFormatToOBOFoundryMap = ontologyFormatToOBOFoundryMap;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @param ontologyVersionStatusToOBOFoundryMap
	 *            the ontologyVersionStatusToOBOFoundryMap to set
	 */
	public void setOntologyVersionStatusToOBOFoundryMap(
			Map<String, String> ontologyVersionStatusToOBOFoundryMap) {
		this.ontologyVersionStatusToOBOFoundryMap = ontologyVersionStatusToOBOFoundryMap;
	}

	/**
	 * @param ontologyFoundryToOBOFoundryMap
	 *            the ontologyFoundryToOBOFoundryMap to set
	 */
	public void setOntologyFoundryToOBOFoundryMap(
			Map<String, Byte> ontologyFoundryToOBOFoundryMap) {
		this.ontologyFoundryToOBOFoundryMap = ontologyFoundryToOBOFoundryMap;
	}

	/**
	 * @param oboRepositoriesConfigFilepath
	 *            the oboRepositoriesConfigFilepath to set
	 */
	public void setOboRepositoriesConfigFilepath(
			String oboRepositoriesConfigFilepath) {
		this.oboRepositoriesConfigFilepath = oboRepositoriesConfigFilepath;
	}
}