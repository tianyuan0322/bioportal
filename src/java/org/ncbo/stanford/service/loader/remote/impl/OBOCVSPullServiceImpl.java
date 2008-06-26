package org.ncbo.stanford.service.loader.remote.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ContactTypeBean;
import org.ncbo.stanford.bean.MetadataFileBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.StatusEnum;
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
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.PhysicalDirectoryFilePathHandlerImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

	private String oboSourceforgeCVSUsername;
	private String oboSourceforgeCVSPassword;
	private String oboSourceforgeCVSHostname;
	private String oboSourceforgeCVSModule;
	private String oboSourceforgeCVSRootDirectory;
	private String oboSourceforgeCVSArgumentString;
	private String oboSourceforgeCVSCheckoutDir;
	private String oboSourceforgeCVSDescriptorFile;
	private OntologyService ontologyService;
	private UserService userService;
	private Map<String, String> ontologyFormatToOBOFoundryMap = new HashMap<String, String>();
	private Map<String, String> ontologyVersionStatusToOBOFoundryMap = new HashMap<String, String>();
	private Map<String, Byte> ontologyFoundryToOBOFoundryMap = new HashMap<String, Byte>();
	private String tempDir;

	private enum ActionEnum {
		NO_ACTION, CREATE_LOCAL_ACTION, CREATE_REMOTE_ACTION, UPDATE_ACTION
	}

	/**
	 * Performs the pull of ontologies from OBO Sourceforge CVS
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void doCVSPull() {
		CVSUtils cvsUtils = new CVSUtils(oboSourceforgeCVSUsername,
				oboSourceforgeCVSPassword, oboSourceforgeCVSHostname,
				oboSourceforgeCVSModule, oboSourceforgeCVSRootDirectory,
				oboSourceforgeCVSArgumentString, oboSourceforgeCVSCheckoutDir,
				tempDir);

		try {
			cvsUtils.cvsCheckout();
			HashMap<String, CVSFile> updateFiles = cvsUtils.getAllCVSEntries();
			OntologyDescriptorParser odp = new OntologyDescriptorParser(
					oboSourceforgeCVSDescriptorFile);
			List<MetadataFileBean> ontologyList = odp.parseOntologyFile();

			for (MetadataFileBean mfb : ontologyList) {
				try {
					String format = getFormat(mfb.getFormat());

					if (format.equals(ApplicationConstants.FORMAT_INVALID)) {
						throw new InvalidOntologyFormatException(
								"The ontology format, " + mfb.getFormat()
										+ " for ontology " + mfb.getId()
										+ " is invalid");
					}

					CVSFile cf = null;
					String filename = OntologyDescriptorParser.getFileName(mfb
							.getDownload());
					boolean isEmptyFilename = StringHelper
							.isNullOrNullString(filename);

					if (!isEmptyFilename) {
						cf = (CVSFile) updateFiles.get(filename);
					}

					HashMap<ActionEnum, OntologyBean> ontologyAction = determineOntologyAction(
							mfb, cf);
					ActionEnum action = (ActionEnum) ontologyAction.keySet()
							.toArray()[0];
					OntologyBean ont = ontologyAction.get(action);

					switch (action) {
					case CREATE_LOCAL_ACTION:
						if (isEmptyFilename) {
							throw new InvalidDataException(
									"No filename is specified in the metadata descriptor file for ontology "
											+ mfb.getId());
						}

						if (cf == null) {
							throw new FileNotFoundException(
									"An entry exists in the metadata descriptor for "
											+ mfb.getId()
											+ " ontology but the file ("
											+ filename + ") is missing");
						}

						FilePathHandler filePathHandler = new PhysicalDirectoryFilePathHandlerImpl(
								CompressedFileHandlerFactory
										.createFileHandler(format),
								new File(oboSourceforgeCVSCheckoutDir + "/"
										+ cf.getPath() + "/" + filename));
						ontologyService.createOntology(ont, filePathHandler);
						break;
					case CREATE_REMOTE_ACTION:
						ontologyService.createOntology(ont, null);
						break;
					case UPDATE_ACTION:
						ontologyService.updateOntology(ont);
						break;
					}
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Populates an ontology bean from the metadata file bean. Returns null if
	 * no new version exists
	 * 
	 * @param mfb
	 * @param cf
	 * @return
	 * @throws InvalidDataException
	 */
	private HashMap<ActionEnum, OntologyBean> determineOntologyAction(
			MetadataFileBean mfb, CVSFile cf) throws InvalidDataException {
		ActionEnum action = ActionEnum.NO_ACTION;
		HashMap<ActionEnum, OntologyBean> ontologyAction = new HashMap<ActionEnum, OntologyBean>(
				1);
		OntologyBean ont = ontologyService
				.findLatestOntologyVersionByOboFoundryId(mfb.getId());
		String downloadUrl = mfb.getDownload();
		List<Integer> newCategoryIds = findCategoryIdsByOBONames(downloadUrl);
		Byte isRemote = isRemote(downloadUrl);
		Date now = Calendar.getInstance().getTime();

		// is any action required?
		// 		a. local && categories didn't change
		// is this an update action?
		// 		a. remote && exists in the system
		// 		b. local && categories changed

		// new ontology
		if (ont == null) {
			action = (isRemote == ApplicationConstants.TRUE) ? ActionEnum.CREATE_REMOTE_ACTION
					: ActionEnum.CREATE_LOCAL_ACTION;
			ont = new OntologyBean();
			// existing ontology remote
		} else if (isRemote == ApplicationConstants.TRUE) {
			action = ActionEnum.UPDATE_ACTION;
			// existing ontology local
		} else if (cf != null && cf.getVersion().equals(ont.getVersionNumber())) {
			// no new version found; check categories
			List<Integer> oldCategoryIds = ont.getCategoryIds();
			boolean categoriesUpdated = isCategoryUpdated(oldCategoryIds,
					newCategoryIds);

			if (categoriesUpdated) {
				action = ActionEnum.UPDATE_ACTION;
			}
		} else if (cf != null) {
			action = ActionEnum.CREATE_LOCAL_ACTION;
			ont.setId(null);
		}

		if (action != ActionEnum.NO_ACTION) {
			if (isRemote == ApplicationConstants.TRUE) {
				ont.setVersionNumber(MessageUtils
						.getMessage("remote.ontology.version"));
				ont.setDateReleased(now);
				ont.setStatusId(StatusEnum.STATUS_NOTAPPLICABLE.getStatus());
			} else {
				ont.setVersionNumber(cf.getVersion());
				ont.setDateReleased(cf.getTimeStamp().getTime());
				ont.setStatusId(StatusEnum.STATUS_WAITING.getStatus());
				ont.addFilename(OntologyDescriptorParser
						.getFileName(downloadUrl));
				ont.setCategoryIds(newCategoryIds);
			}

			UserBean userBean = linkOntologyUser(mfb.getContact(), mfb.getId());
			ont.setUserId(userBean.getId());
			ont.setVersionStatus(getStatus(mfb.getStatus()));
			ont.setIsCurrent(ApplicationConstants.TRUE);
			ont.setIsRemote(isRemote);

			if (action != ActionEnum.UPDATE_ACTION) {
				ont.setDateCreated(now);
			}

			ont.setOboFoundryId(mfb.getId());
			ont.setDisplayLabel(mfb.getTitle());
			ont.setFormat(getFormat(mfb.getFormat()));
			ont.setContactName(userBean.getLastname());
			ont.setContactEmail(userBean.getEmail());
			ont
					.setHomepage(OntologyDescriptorParser.getHomepage(mfb
							.getHome()));
			ont.setDocumentation(OntologyDescriptorParser.getDocumentation(mfb
					.getDocumentation()));
			ont.setPublication(OntologyDescriptorParser.getPublication(mfb
					.getPublication()));
			ont.setIsFoundry(isFoundry(mfb.getFoundry()));
		}

		ontologyAction.put(action, ont);

		return ontologyAction;
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
	private Byte isRemote(String downloadUrl) {
		Byte isRemote = ApplicationConstants.TRUE;

		if (!StringHelper.isNullOrNullString(downloadUrl)
				&& downloadUrl.indexOf(getOboSourceforgeCVSHostname()) > -1) {
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
	private Byte isFoundry(String str) {
		Byte foundry = ApplicationConstants.FALSE;

		if (ontologyFoundryToOBOFoundryMap.containsKey(str)) {
			foundry = ontologyFoundryToOBOFoundryMap.get(str);
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
			userBean.setEmail(contact.getEmail());
			userBean.setFirstname(contact.getName());
			userBean.setLastname(contact.getName());
			userBean.setDateCreated(Calendar.getInstance().getTime());
			userService.createUser(userBean);
		}

		return userBean;
	}

	/**
	 * @return the oboSourceforgeCVSUsername
	 */
	public String getOboSourceforgeCVSUsername() {
		return oboSourceforgeCVSUsername;
	}

	/**
	 * @param oboSourceforgeCVSUsername
	 *            the oboSourceforgeCVSUsername to set
	 */
	public void setOboSourceforgeCVSUsername(String oboSourceforgeCVSUsername) {
		this.oboSourceforgeCVSUsername = oboSourceforgeCVSUsername;
	}

	/**
	 * @return the oboSourceforgeCVSPassword
	 */
	public String getOboSourceforgeCVSPassword() {
		return oboSourceforgeCVSPassword;
	}

	/**
	 * @param oboSourceforgeCVSPassword
	 *            the oboSourceforgeCVSPassword to set
	 */
	public void setOboSourceforgeCVSPassword(String oboSourceforgeCVSPassword) {
		this.oboSourceforgeCVSPassword = oboSourceforgeCVSPassword;
	}

	/**
	 * @return the oboSourceforgeCVSHostname
	 */
	public String getOboSourceforgeCVSHostname() {
		return oboSourceforgeCVSHostname;
	}

	/**
	 * @param oboSourceforgeCVSHostname
	 *            the oboSourceforgeCVSHostname to set
	 */
	public void setOboSourceforgeCVSHostname(String oboSourceforgeCVSHostname) {
		this.oboSourceforgeCVSHostname = oboSourceforgeCVSHostname;
	}

	/**
	 * @return the oboSourceforgeCVSModule
	 */
	public String getOboSourceforgeCVSModule() {
		return oboSourceforgeCVSModule;
	}

	/**
	 * @param oboSourceforgeCVSModule
	 *            the oboSourceforgeCVSModule to set
	 */
	public void setOboSourceforgeCVSModule(String oboSourceforgeCVSModule) {
		this.oboSourceforgeCVSModule = oboSourceforgeCVSModule;
	}

	/**
	 * @return the oboSourceforgeCVSRootDirectory
	 */
	public String getOboSourceforgeCVSRootDirectory() {
		return oboSourceforgeCVSRootDirectory;
	}

	/**
	 * @param oboSourceforgeCVSRootDirectory
	 *            the oboSourceforgeCVSRootDirectory to set
	 */
	public void setOboSourceforgeCVSRootDirectory(
			String oboSourceforgeCVSRootDirectory) {
		this.oboSourceforgeCVSRootDirectory = oboSourceforgeCVSRootDirectory;
	}

	/**
	 * @return the oboSourceforgeCVSArgumentString
	 */
	public String getOboSourceforgeCVSArgumentString() {
		return oboSourceforgeCVSArgumentString;
	}

	/**
	 * @param oboSourceforgeCVSArgumentString
	 *            the oboSourceforgeCVSArgumentString to set
	 */
	public void setOboSourceforgeCVSArgumentString(
			String oboSourceforgeCVSArgumentString) {
		this.oboSourceforgeCVSArgumentString = oboSourceforgeCVSArgumentString;
	}

	/**
	 * @return the oboSourceforgeCVSCheckoutDir
	 */
	public String getOboSourceforgeCVSCheckoutDir() {
		return oboSourceforgeCVSCheckoutDir;
	}

	/**
	 * @param oboSourceforgeCVSCheckoutDir
	 *            the oboSourceforgeCVSCheckoutDir to set
	 */
	public void setOboSourceforgeCVSCheckoutDir(
			String oboSourceforgeCVSCheckoutDir) {
		this.oboSourceforgeCVSCheckoutDir = oboSourceforgeCVSCheckoutDir;
	}

	/**
	 * @return the tempDir
	 */
	public String getTempDir() {
		return tempDir;
	}

	/**
	 * @param tempDir
	 *            the tempDir to set
	 */
	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * @return the oboSourceforgeCVSDescriptorFile
	 */
	public String getOboSourceforgeCVSDescriptorFile() {
		return oboSourceforgeCVSDescriptorFile;
	}

	/**
	 * @param oboSourceforgeCVSDescriptorFile
	 *            the oboSourceforgeCVSDescriptorFile to set
	 */
	public void setOboSourceforgeCVSDescriptorFile(
			String oboSourceforgeCVSDescriptorFile) {
		this.oboSourceforgeCVSDescriptorFile = oboSourceforgeCVSDescriptorFile;
	}

	/**
	 * @return the ontologyFormatToOBOFoundryMap
	 */
	public Map<String, String> getOntologyFormatToOBOFoundryMap() {
		return ontologyFormatToOBOFoundryMap;
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
	 * @return the ontologyService
	 */
	public OntologyService getOntologyService() {
		return ontologyService;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @return the ontologyVersionStatusToOBOFoundryMap
	 */
	public Map<String, String> getOntologyVersionStatusToOBOFoundryMap() {
		return ontologyVersionStatusToOBOFoundryMap;
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
	 * @return the ontologyFoundryToOBOFoundryMap
	 */
	public Map<String, Byte> getOntologyFoundryToOBOFoundryMap() {
		return ontologyFoundryToOBOFoundryMap;
	}

	/**
	 * @param ontologyFoundryToOBOFoundryMap
	 *            the ontologyFoundryToOBOFoundryMap to set
	 */
	public void setOntologyFoundryToOBOFoundryMap(
			Map<String, Byte> ontologyFoundryToOBOFoundryMap) {
		this.ontologyFoundryToOBOFoundryMap = ontologyFoundryToOBOFoundryMap;
	}
}
