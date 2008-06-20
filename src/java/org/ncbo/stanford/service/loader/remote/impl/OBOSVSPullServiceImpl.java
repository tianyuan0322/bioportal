package org.ncbo.stanford.service.loader.remote.impl;

import java.util.HashMap;
import java.util.List;

import org.ncbo.stanford.bean.MetadataFileBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.loader.remote.OBOCVSPullService;
import org.ncbo.stanford.util.cvs.CVSFile;
import org.ncbo.stanford.util.cvs.CVSUtils;
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;

public class OBOSVSPullServiceImpl implements OBOCVSPullService {

	private String oboSourceforgeCVSUsername;
	private String oboSourceforgeCVSPassword;
	private String oboSourceforgeCVSHostname;
	private String oboSourceforgeCVSModule;
	private String oboSourceforgeCVSRootDirectory;
	private String oboSourceforgeCVSArgumentString;
	private String oboSourceforgeCVSCheckoutDir;
	private String oboSourceforgeCVSDescriptorFile;
	private String tempDir;

	public void doCVSPull() {
		CVSUtils cvsUtils = new CVSUtils(oboSourceforgeCVSUsername,
				oboSourceforgeCVSPassword, oboSourceforgeCVSHostname,
				oboSourceforgeCVSModule, oboSourceforgeCVSRootDirectory,
				oboSourceforgeCVSArgumentString, oboSourceforgeCVSCheckoutDir,
				tempDir);

		try {
			cvsUtils.cvsCheckout();
			HashMap<String, CVSFile> updateFiles = cvsUtils.getAllCVSEntries();
			OntologyDescriptorParser odp = new OntologyDescriptorParser(oboSourceforgeCVSDescriptorFile);
			List<MetadataFileBean> ontologyList = odp.parseOntologyFile();
			boolean isRemote = false;
			
			
		
			for (MetadataFileBean mfb : ontologyList) {
				OntologyBean ontologyBean = new OntologyBean();
				//ontologyBean.po
			
			
			}		
			
			
			
			
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * @param oboSourceforgeCVSDescriptorFile the oboSourceforgeCVSDescriptorFile to set
	 */
	public void setOboSourceforgeCVSDescriptorFile(
			String oboSourceforgeCVSDescriptorFile) {
		this.oboSourceforgeCVSDescriptorFile = oboSourceforgeCVSDescriptorFile;
	}

}
