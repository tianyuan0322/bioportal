package org.ncbo.stanford.service.loader.remote.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ncbo.stanford.bean.ContactTypeBean;
import org.ncbo.stanford.bean.MetadataFileBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.InvalidDataException;
import org.ncbo.stanford.service.loader.remote.OBOCVSPullService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.cvs.CVSFile;
import org.ncbo.stanford.util.cvs.CVSUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OBOCVSPullServiceImpl implements OBOCVSPullService {

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

	@Transactional(propagation = Propagation.NEVER)
	public void doCVSPull() {
		CVSUtils cvsUtils = new CVSUtils(oboSourceforgeCVSUsername,
				oboSourceforgeCVSPassword, oboSourceforgeCVSHostname,
				oboSourceforgeCVSModule, oboSourceforgeCVSRootDirectory,
				oboSourceforgeCVSArgumentString, oboSourceforgeCVSCheckoutDir,
				tempDir);

		try {
			// cvsUtils.cvsCheckout();
			HashMap<String, CVSFile> updateFiles = cvsUtils.getAllCVSEntries();
			OntologyDescriptorParser odp = new OntologyDescriptorParser(
					oboSourceforgeCVSDescriptorFile);
			List<MetadataFileBean> ontologyList = odp.parseOntologyFile();

			for (MetadataFileBean mfb : ontologyList) {
				String filename = OntologyDescriptorParser.getFileName(mfb
						.getDownload());
				CVSFile cf;
				// is the file there?
				if (!StringHelper.isNullOrNullString(filename)
						&& (cf = (CVSFile) updateFiles.get(filename)) != null) {					
					String format = getFormat(mfb.getFormat());
					
					if (!format.equals(ApplicationConstants.FORMAT_INVALID)) {
						OntologyBean ont = ontologyService
								.findLatestOntologyVersionByOboFoundryId(mfb
										.getId());

						if (ont == null) {
							// new ontology
							ont = new OntologyBean();

							
							
							
							
							
							
							UserBean userBean = linkOntologyUser(mfb
									.getContact(), mfb.getId());
							ont.setUserId(userBean.getId());
							ont.setVersionNumber(cf.getVersion());
							ont.setVersionStatus(getStatus(mfb.getStatus()));
							ont.setIsCurrent(ApplicationConstants.TRUE);
							ont.setIsRemote(isRemote(mfb.getDownload()));
							ont.setStatusId(StatusEnum.STATUS_WAITING.getStatus());							
							ont.setDateCreated(Calendar.getInstance().getTime());
							ont.setDateReleased(cf.getTimeStamp().getTime());
							ont.setOboFoundryId(mfb.getId());
							ont.setDisplayLabel(mfb.getTitle());							
							ont.setFormat(format);
							ont.setContactName(userBean.getLastname());
							ont.setContactEmail(userBean.getEmail());
							ont.setHomepage(OntologyDescriptorParser.getHomepage(mfb.getHome()));
							ont.setDocumentation(OntologyDescriptorParser.getDocumentation(mfb.getDocumentation()));
							ont.setPublication(OntologyDescriptorParser.getPublication(mfb.getPublication()));
							ont.setIsFoundry(isFoundry(mfb.getFoundry()));
							
							
							
						} else {
							// do we already have this version loaded?
							if (!cf.getVersion().equals(ont.getVersionNumber())) {
								
								
								
								UserBean userBean = linkOntologyUser(mfb
										.getContact(), mfb.getId());
								ont.setUserId(userBean.getId());
								ont.setVersionNumber(cf.getVersion());
								ont
										.setVersionStatus(getStatus(mfb
												.getStatus()));
								ont.setIsCurrent(ApplicationConstants.TRUE);
								ont.setIsRemote(isRemote(mfb.getDownload()));
								ont.setStatusId(StatusEnum.STATUS_WAITING.getStatus());
								ont.setDateCreated(Calendar.getInstance().getTime());
								ont.setDateReleased(cf.getTimeStamp().getTime());
								ont.setOboFoundryId(mfb.getId());
								ont.setDisplayLabel(mfb.getTitle());
								ont.setFormat(format);
								ont.setContactName(userBean.getLastname());
								ont.setContactEmail(userBean.getEmail());
								ont.setHomepage(OntologyDescriptorParser.getHomepage(mfb.getHome()));
								ont.setDocumentation(OntologyDescriptorParser.getDocumentation(mfb.getDocumentation()));								
								ont.setPublication(OntologyDescriptorParser.getPublication(mfb.getPublication()));
								ont.setIsFoundry(isFoundry(mfb.getFoundry()));
								
								

							}
						}
					} else {
						// TODO: throw an exception unexceptable ontology format

					}
				} else {
					// TODO: throw an exception entry exists but file not found

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	/**
	 * Determines whether the ontology is hosted remotely
	 * 
	 * @param filePath
	 * @return
	 */
	private Byte isRemote(String filePath) {
		Byte isRemote = ApplicationConstants.TRUE;
		
		if (!StringHelper.isNullOrNullString(filePath)
				&& filePath.indexOf(getOboSourceforgeCVSHostname()) > -1) {
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
	 * @param ontologyFoundryToOBOFoundryMap the ontologyFoundryToOBOFoundryMap to set
	 */
	public void setOntologyFoundryToOBOFoundryMap(
			Map<String, Byte> ontologyFoundryToOBOFoundryMap) {
		this.ontologyFoundryToOBOFoundryMap = ontologyFoundryToOBOFoundryMap;
	}
}
