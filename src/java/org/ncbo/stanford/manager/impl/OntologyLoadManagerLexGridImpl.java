/*
 * The contents of this file are licensed under the Eclipse Public License,
 * Version 1.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at -
 *
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The initial developer of the original code is Stanford University. Portions
 * created by Stanford University are Copyright (c) 2005-2007.  All Rights Reserved.
 *
 */
package org.ncbo.stanford.manager.impl;

/**
 * Author: Pradip Kanjamala
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.LoadStatus;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.types.ProcessState;
import org.LexGrid.LexBIG.Extensions.Load.LexGrid_Loader;
import org.LexGrid.LexBIG.Extensions.Load.Loader;
import org.LexGrid.LexBIG.Extensions.Load.OBO_Loader;
import org.LexGrid.LexBIG.Extensions.Load.OWL_Loader;
import org.LexGrid.LexBIG.Extensions.Load.UMLS_Loader;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.LexBIGServiceManager;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexOnt.CodingSchemeManifest;
import org.LexGrid.LexOnt.CsmfFormalName;
import org.LexGrid.LexOnt.CsmfRegisteredName;
import org.LexGrid.LexOnt.CsmfVersion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.generated.NcboOntologyVersionMetadata;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.OntologyLoadManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;

public class OntologyLoadManagerLexGridImpl extends
		AbstractOntologyManagerLexGrid implements OntologyLoadManager {

	private static final Log log = LogFactory
			.getLog(OntologyLoadManagerLexGridImpl.class);
	private String targetTerminologies;

	/**
	 * A comma delimited list of UMLS terminologies to load. If null, all
	 * available terminologies will be loaded. The terminology name should be
	 * the SAB name of the terminology - or values from the RSAB column of the
	 * MRSAB file.
	 */

	/**
	 * Loads the specified ontology into the LexGrid repository. If the
	 * specified ontology identifier already exists, overwrite.
	 * 
	 * @param ontologyId
	 *            the ontology id for the specified ontology file.
	 * @param ontologyUri
	 *            the uri of the ontology to be loaded.
	 * @param ontology_bean
	 *            the ontology_bean that contains the metadata information of
	 *            the ontology to be loaded.
	 * 
	 * @exception Exception
	 *                catch all for all other ontology file load errors.
	 */
	public void loadOntology(URI ontologyUri, OntologyBean ontology_bean)
			throws Exception {
		boolean stopOnErrors = false;
		boolean async = false;
		URI source = ontologyUri;

		File ontologyFile = new File(source.getPath());
		String filePath = source.getPath();

		if (ontologyFile == null) {
			log.error("Missing ontology file to load: " + filePath);
			throw new FileNotFoundException("Missing ontology file to load: "
					+ filePath);
		}

		log.debug("Loading ontology file: " + ontologyFile.getName());

		// Get the LexBIGService
		LexBIGService lbs = LexBIGServiceImpl.defaultInstance();
		LexBIGServiceManager lbsm = lbs.getServiceManager(null);

		// remove existing scheme if it exists before parsing...
		cleanup(ontology_bean);

		Loader loader = null;
		CodingSchemeManifest csm = createCodingSchemeManifest(ontology_bean);

		// Load OBO
		if (ontology_bean.getFormat().equalsIgnoreCase(
				ApplicationConstants.FORMAT_OBO)) {
			loader = lbsm
					.getLoader(org.LexGrid.LexBIG.Impl.loaders.OBOLoaderImpl.name);
			loader.setCodingSchemeManifest(csm);
			((OBO_Loader) loader).load(source, null, stopOnErrors, async);
			// Load UMLS
		} else if (ontology_bean.getFormat().equalsIgnoreCase(
				ApplicationConstants.FORMAT_UMLS_RRF)) {
			loader = lbsm
					.getLoader(org.LexGrid.LexBIG.Impl.loaders.UMLSLoaderImpl.name);
			LocalNameList lnl = getLocalNameListFromTargetTerminologies();
			((UMLS_Loader) loader).load(source, lnl, stopOnErrors, async);
			// Load LEXGRID XML
		} else if (ontology_bean.getFormat().equalsIgnoreCase(
				ApplicationConstants.FORMAT_LEXGRID_XML)) {
			loader = lbsm
					.getLoader(org.LexGrid.LexBIG.Impl.loaders.LexGridLoaderImpl.name);
			((LexGrid_Loader) loader).load(source, stopOnErrors, async);
			// Load OWL
		} else if (ontology_bean.getFormat().equalsIgnoreCase(
				ApplicationConstants.FORMAT_OWL_DL)
				|| ontology_bean.getFormat().equalsIgnoreCase(
						ApplicationConstants.FORMAT_OWL_FULL)) {
			loader = lbsm
					.getLoader(org.LexGrid.LexBIG.Impl.loaders.OWLLoaderImpl.name);
			loader.setCodingSchemeManifest(csm);

			// Load only NCI Thesaurus for now.
			if (ontology_bean.getFilePath() != null
					&& ontology_bean.getFilePath().indexOf("Thesaurus") >= 0) {
				((OWL_Loader) loader).loadNCI(source, null, false,
						stopOnErrors, async);
			} else {
				((OWL_Loader) loader).load(source, null, stopOnErrors, async);
			}
		}

		// No Loader could be found for the format
		if (loader == null) {
			String error_msg = "No LexBIG loader could be found to load the ontology format '"
					+ ontology_bean.getFormat() + "'";
			log.error(error_msg);
			throw new Exception(error_msg);
		}

		LoadStatus status = loader.getStatus();

		if (status.getState().getType() == ProcessState.COMPLETED_TYPE) {
			// Activate the newly loaded scheme(s) ...
			AbsoluteCodingSchemeVersionReference[] refs = loader
					.getCodingSchemeReferences();
			String urn = null;
			String version = null;

			for (int i = 0; i < refs.length; i++) {
				AbsoluteCodingSchemeVersionReference ref = refs[i];
				lbsm.activateCodingSchemeVersion(ref);
				urn = ref.getCodingSchemeURN();
				version = ref.getCodingSchemeVersion();
			}

			// Update the NCBO Metadata table with the unique LexGrid url and
			// version for the ontology
			NcboOntologyVersionMetadata ncboMetadata = ncboOntologyVersionDAO
					.findOntologyMetadataById(ontology_bean.getId());
			
			if (ncboMetadata != null) {
				String urnAndVersion = urn + "|" + version;
				ncboMetadata.setCodingScheme(urnAndVersion);
				log
						.debug("Updating the NcboOntologyMetadata with the codingScheme name="
								+ urnAndVersion);
				ncboOntologyVersionMetadataDAO.getHibernateTemplate().update(
						ncboMetadata);
				ncboOntologyVersionMetadataDAO.getSessionFactory().getCurrentSession()
						.flush();
				ontology_bean.setCodingScheme(urnAndVersion);
			} else {
				String message = "Could not update the codingScheme informtion into NcboOntologyMetadata for ontologyversionid="
						+ ontology_bean.getId();
				log.warn(message);
			}

		} else {
			if (status.getErrorsLogged().booleanValue()) {
				String error_message = "";

				if (status.getMessage() != null) {
					error_message = "LexBIG Ontology load failed with message= "
							+ status.getMessage();
				} else {
					error_message = "LexBIG Ontology load failed. Check the LexBIG load logs for details.";
				}

				log.error(error_message);
				throw new Exception(error_message);
			}
		}
	}

	/**
	 * Remove the LexGrid references to a OntologyBean. We remove the
	 * codingScheme that the bean refers to.
	 * 
	 * @param ontology_bean
	 *            the ontology_bean that contains the metadata information of
	 *            the ontology to be loaded.
	 * 
	 * @exception Exception
	 *                catch all for all other ontology file load errors.
	 */
	public void cleanup(OntologyBean ontology_bean) throws Exception {
		// Get the LexBIGService
		LexBIGService lbs = LexBIGServiceImpl.defaultInstance();
		LexBIGServiceManager lbsm = lbs.getServiceManager(null);

		// remove existing scheme if it exists before parsing...
		CodingSchemeRendering csRendering = getCodingSchemeRendering(lbs,
				ontology_bean.getCodingScheme());

		if (csRendering != null) {
			AbsoluteCodingSchemeVersionReference acsvr = Constructors
					.createAbsoluteCodingSchemeVersionReference(csRendering
							.getCodingSchemeSummary());
			lbsm.deactivateCodingSchemeVersion(acsvr, null);
			lbsm.removeCodingSchemeVersion(acsvr);
			ontology_bean.setCodingScheme(null);
			NcboOntologyVersionMetadata ncboMetadata = ncboOntologyVersionDAO
					.findOntologyMetadataById(ontology_bean.getId());

			if (ncboMetadata != null) {
				ncboMetadata.setCodingScheme(null);
				ncboOntologyVersionMetadataDAO.save(ncboMetadata);
			}
		}
	}

	public String getTargetTerminologies() {
		return targetTerminologies;
	}

	public void setTargetTerminologies(String targetTerminologies) {
		this.targetTerminologies = targetTerminologies;
	}

	public LocalNameList getLocalNameListFromTargetTerminologies() {
		LocalNameList lnl = null;

		if (targetTerminologies != null) {
			lnl = new LocalNameList();
			String[] terminologies = targetTerminologies.split(",");

			for (int i = 0; i < terminologies.length; i++) {
				lnl.addEntry(terminologies[i]);
			}
		}

		return lnl;
	}

	public CodingSchemeManifest createCodingSchemeManifest(
			OntologyBean ontology_bean) {
		CodingSchemeManifest csm = new CodingSchemeManifest();
		if (ontology_bean.getFormat().equalsIgnoreCase(
				ApplicationConstants.FORMAT_OBO)
				|| ontology_bean.getFormat().equalsIgnoreCase(
						ApplicationConstants.FORMAT_OWL_DL)
				|| ontology_bean.getFormat().equalsIgnoreCase(
						ApplicationConstants.FORMAT_OWL_FULL)) {
			// Override registered name using metadata from the ontology bean
			String registeredName = "http://www.bioontology.org/"
					+ ontology_bean.getId().toString() + "/"
					+ ontology_bean.getDisplayLabel();
			CsmfRegisteredName csmfRegisteredName = new CsmfRegisteredName();
			csmfRegisteredName.setContent(registeredName);
			csm.setRegisteredName(csmfRegisteredName);
			// Override version using metadata from the ontology bean
			CsmfVersion csmfVersion = new CsmfVersion();
			String version= ontology_bean.getOntologyId()+ "/"+ontology_bean.getInternalVersionNumber().toString();
			csmfVersion.setContent(version);
			csm.setRepresentsVersion(csmfVersion);
			// Override formal name using metadata from the ontology bean
			CsmfFormalName csmfFormalName = new CsmfFormalName();
			csmfFormalName.setContent(ontology_bean.getDisplayLabel());
			csm.setFormalName(csmfFormalName);
		}

		return csm;
	}
}
