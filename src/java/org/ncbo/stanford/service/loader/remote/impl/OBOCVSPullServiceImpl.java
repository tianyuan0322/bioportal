package org.ncbo.stanford.service.loader.remote.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

					OntologyAction ontologyAction = determineOntologyAction(
							mfb, cf);
					ActionEnum action = ontologyAction.getAction();
					log.debug(ontologyAction);

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
										.createFileHandler(format),
								new File(oboSourceforgeCVSCheckoutDir + "/"
										+ cf.getPath() + "/" + filename));
						ontologyService.createOntology(ont, filePathHandler);
						break;
					case CREATE_REMOTE_ACTION:
						ontologyService.createOntology(ont, null);
						break;
					case UPDATE_ACTION:
						ontologyService.cleanupOntologyCategory(ont);
						ontologyService.updateOntology(ont);
						break;
					}
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
				}
			}

			log.debug("**** OBO Pull completed successfully *****");
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
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
			CVSFile cf) throws InvalidDataException {
		ActionEnum action = ActionEnum.NO_ACTION;
		
		OntologyBean ont = ontologyService
				.findLatestOntologyVersionByOboFoundryId(mfb.getId());
		String downloadUrl = mfb.getDownload();
		List<Integer> newCategoryIds = findCategoryIdsByOBONames(downloadUrl);
		byte isRemote = isRemote(downloadUrl);

		// 3/5/09 
		// Created to deal with the lexgrid issue that it cant determine the namespaces which creates an issue with searching.
		// The ontologies are replicated within eachother.
		// This code will ignore cellular_component and molecular_function and turn biological_process into the main GO ontology
		if(mfb.getId().equalsIgnoreCase("cellular_component") || mfb.getId().equalsIgnoreCase("molecular_function") ){
			populateOntologyBean(mfb, cf, action, ont, newCategoryIds, isRemote);
			return new OntologyAction(action, ont);
		}
		if(mfb.getId().equalsIgnoreCase("biological_process")){
			mfb.setTitle("Gene Ontology");		
			mfb.setDescription("Provides structured controlled vocabularies for the annotation of gene products" +
							" with respect to their molecular function, cellular component, and biological role." +
							" The Gene Ontology consists of three Vocabularies.");
		}
		
		// is any action required?
		// ____a. local && categories didn't change
		// is this an update action?
		// ____a. remote && exists in the system
		// ____b. local && categories changed

		if (ont == null) {
			// new ontology
			action = (isRemote == ApplicationConstants.TRUE) ? ActionEnum.CREATE_REMOTE_ACTION
					: ActionEnum.CREATE_LOCAL_ACTION;
			ont = new OntologyBean();
		} else if (ont.getIsManual().byteValue() != ApplicationConstants.TRUE) {
			if (isRemote == ApplicationConstants.TRUE) {
				if (ont.isRemote()) {
					// existing ontology that had been and remains remote
					action = ActionEnum.UPDATE_ACTION;
				} else {
					// existing ontology that had been local but is now remote
					action = ActionEnum.CREATE_REMOTE_ACTION;
				}
			} else if (cf != null
					&& cf.getVersion().equals(ont.getVersionNumber())) {
				// existing ontology local; no new version
				// no new version found; check if categories have been updated
				List<Integer> oldCategoryIds = ont.getCategoryIds();
				boolean categoriesUpdated = isCategoryUpdated(oldCategoryIds,
						newCategoryIds);

				if (categoriesUpdated) {
					action = ActionEnum.UPDATE_ACTION;
				}
			} else if (cf != null) {
				// existing ontology local; new version
				action = ActionEnum.CREATE_LOCAL_ACTION;
			}
		}

		populateOntologyBean(mfb, cf, action, ont, newCategoryIds, isRemote);

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
	private byte isRemote(String downloadUrl) {
		byte isRemote = ApplicationConstants.TRUE;

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
