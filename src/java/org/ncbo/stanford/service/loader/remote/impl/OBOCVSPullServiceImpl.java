package org.ncbo.stanford.service.loader.remote.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections.ListUtils;
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
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.PhysicalDirectoryFilePathHandlerImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
		NO_ACTION, CREATE_LOCAL_ACTION, CREATE_REMOTE_ACTION, UPDATE_ACTION
	}

	/**
	 * Performs the pull of ontologies from OBO Sourceforge CVS
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void doCVSPull() {
		try {
			List<OBORepositoryInfoHolder> repos = parseRepositoryConfigFile();

			for (OBORepositoryInfoHolder repo : repos) {
				CVSUtils cvsUtils = new CVSUtils(repo.getUsername(), repo
						.getPassword(), repo.getHostname(), repo.getModule(),
						repo.getRootdirectory(), repo.getArgumentstring(), repo
								.getCheckoutdir(), tempDir);

				cvsUtils.cvsCheckout();
				HashMap<String, CVSFile> updateFiles = cvsUtils
						.getAllCVSEntries();
				processRecords(repo, updateFiles);
			}

			log.debug("**** OBO Pull completed successfully *****");
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	private void processRecords(OBORepositoryInfoHolder repo,
			HashMap<String, CVSFile> updateFiles) throws IOException {
		OntologyDescriptorParser odp = new OntologyDescriptorParser(repo
				.getDescriptorlocation());
		List<MetadataFileBean> ontologyList = odp.parseOntologyFile();

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
				String filename = OntologyDescriptorParser.getFileName(mfb
						.getDownload());
				boolean isEmptyFilename = StringHelper
						.isNullOrNullString(filename);

				if (!isEmptyFilename) {
					cf = (CVSFile) updateFiles.get(filename);
				}

				OntologyAction ontologyAction = determineOntologyAction(mfb,
						cf, repo.getHostname());
				ActionEnum action = ontologyAction.getAction();
				OntologyBean ont = ontologyAction.getOntotlogyBean();

				switch (action) {
				case CREATE_LOCAL_ACTION:
					if (isEmptyFilename) {
						throw new InvalidDataException(
								"No filename is specified in the metadata descriptor file for ontology ["
										+ mfb.getId() + "]");
					}

					if (cf == null) {
						throw new FileNotFoundException(
								"An entry exists in the metadata descriptor for ["
										+ mfb.getId()
										+ "] ontology but the file ["
										+ filename + "] is missing");
					}

					FilePathHandler filePathHandler = new PhysicalDirectoryFilePathHandlerImpl(
							CompressedFileHandlerFactory
									.createFileHandler(format), new File(repo
									.getCheckoutdir()
									+ "/" + cf.getPath() + "/" + filename));
					ontologyService.createOntologyOrView(ont, filePathHandler);
					break;
				case CREATE_REMOTE_ACTION:
					ontologyService.createOntologyOrView(ont, null);
					break;
				case UPDATE_ACTION:
					ontologyService.cleanupOntologyCategory(ont);
					ontologyService.updateOntologyOrView(ont);
					break;
				}
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		}
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
			CVSFile cf, String cvsHostname) throws InvalidDataException {
		ActionEnum action = ActionEnum.NO_ACTION;
		String oboFoundryId = mfb.getId();

		OntologyBean ont = ontologyService
				.findLatestOntologyVersionByOboFoundryId(oboFoundryId);
		String downloadUrl = mfb.getDownload();
		List<Integer> newCategoryIds = findCategoryIdsByOBONames(downloadUrl);
		byte isRemote = isRemote(downloadUrl, cvsHostname);

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
				action = (isRemote == ApplicationConstants.TRUE) ? ActionEnum.CREATE_REMOTE_ACTION
						: ActionEnum.CREATE_LOCAL_ACTION;
				ont = new OntologyBean(false);
			} else if (ont.getIsManual().byteValue() != ApplicationConstants.TRUE) {
				if (isRemote == ApplicationConstants.TRUE) {
					if (ont.isRemote()) {
						// existing ontology that had been and remains remote
						action = ActionEnum.UPDATE_ACTION;
					} else {
						// existing ontology that had been local but is now
						// remote
						action = ActionEnum.CREATE_REMOTE_ACTION;
					}
				} else if (cf != null
						&& cf.getVersion().equals(ont.getVersionNumber())) {
					// existing ontology local; no new version
					// no new version found; check if categories have been
					// updated
					List<Integer> oldCategoryIds = ont.getCategoryIds();
					boolean categoriesUpdated = isCategoryUpdated(
							oldCategoryIds, newCategoryIds);

					//TODO What about groups??? Do we need to check that here also?
					
					if (categoriesUpdated) {
						action = ActionEnum.UPDATE_ACTION;
					}
				} else if (cf != null) {
					// existing ontology local; new version
					action = ActionEnum.CREATE_LOCAL_ACTION;
				}
			}

			populateOntologyBean(mfb, cf, action, ont, newCategoryIds, isRemote);
		}

		return new OntologyAction(action, ont);
	}

	/**
	 * Populates ontology bean based on the action
	 * 
	 * @param mfb
	 * @param cf
	 * @param action
	 * @param ont
	 * @param newCategoryIds
	 * @param isRemote
	 * @throws InvalidDataException
	 */
	private void populateOntologyBean(MetadataFileBean mfb, CVSFile cf,
			ActionEnum action, OntologyBean ont, List<Integer> newCategoryIds,
			byte isRemote) throws InvalidDataException {
		Date now = Calendar.getInstance().getTime();
		String downloadUrl = mfb.getDownload();
		String sourceUrl = mfb.getSource();

		if (action != ActionEnum.NO_ACTION) {
			if (isRemote == ApplicationConstants.TRUE) {
				ont.setVersionNumber(MessageUtils
						.getMessage("remote.ontology.version"));
				ont.setDateReleased(now);

				if (!StringHelper.isNullOrNullString(downloadUrl)) {
					ont.setFilePath(downloadUrl);
				} else if (!StringHelper.isNullOrNullString(sourceUrl)) {
					ont.setFilePath(sourceUrl);
				}
			} else {
				ont.setVersionNumber(cf.getVersion());
				ont.setDateReleased(cf.getTimeStamp().getTime());
				ont.addFilename(OntologyDescriptorParser.getFileName(mfb
						.getDownload()));
				ont.setCategoryIds(newCategoryIds);
				//TODO: What does this part of the method do??? (Csongor)
				//We need to deal with information about groups, too 
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
			ont.setUserId(userBean.getId());
			ont.setVersionStatus(getStatus(mfb.getStatus()));
			ont.setIsRemote(new Byte(isRemote));
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
	private List<Integer> findCategoryIdsByOBONames(String downloadUrl) {
		List<Integer> categoryIds = new ArrayList<Integer>(1);

		if (!StringHelper.isNullOrNullString(downloadUrl)) {
			categoryIds = ontologyService
					.findCategoryIdsByOBOFoundryNames(downloadUrl.split("/"));
		}

		return categoryIds;
	}

	/**
	 * Determines whether the ontology is hosted remotely
	 * 
	 * @param downloadUrl
	 * @return
	 */
	private byte isRemote(String downloadUrl, String cvsHostname) {
		byte isRemote = ApplicationConstants.TRUE;

		if (!StringHelper.isNullOrNullString(downloadUrl)
				&& downloadUrl.indexOf(cvsHostname) > -1) {
			isRemote = ApplicationConstants.FALSE;
		}

		return isRemote;
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

		if (ontologyFormatToOBOFoundryMap.containsKey(str)) {
			format = ontologyFormatToOBOFoundryMap.get(str);
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