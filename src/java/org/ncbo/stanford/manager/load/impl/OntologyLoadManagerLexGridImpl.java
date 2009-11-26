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
package org.ncbo.stanford.manager.load.impl;

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
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Load.LexGrid_Loader;
import org.LexGrid.LexBIG.Extensions.Load.Loader;
import org.LexGrid.LexBIG.Extensions.Load.OBO_Loader;
import org.LexGrid.LexBIG.Extensions.Load.OWL_Loader;
import org.LexGrid.LexBIG.Extensions.Load.UmlsBatchLoader;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.LexBIGServiceManager;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexOnt.CodingSchemeManifest;
import org.LexGrid.LexOnt.CsmfCodingSchemeURI;
import org.LexGrid.LexOnt.CsmfFormalName;
import org.LexGrid.LexOnt.CsmfVersion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.load.OntologyLoadManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;

public class OntologyLoadManagerLexGridImpl extends
		AbstractOntologyManagerLexGrid implements OntologyLoadManager {

	private static final Log log = LogFactory
			.getLog(OntologyLoadManagerLexGridImpl.class);

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
	 * @param ob
	 *            the ontology_bean that contains the metadata information of
	 *            the ontology to be loaded.
	 * 
	 * @exception Exception
	 *                catch all for all other ontology file load errors.
	 */
	public void loadOntology(URI ontologyUri, OntologyBean ob) throws Exception {
		boolean stopOnErrors = false;
		boolean async = false;

		File ontologyFile = new File(ontologyUri.getPath());
		String filePath = ontologyUri.getPath();

		if (ontologyFile == null) {
			log.error("Missing ontology file to load: " + filePath);
			throw new FileNotFoundException("Missing ontology file to load: "
					+ filePath);
		}

		log
				.debug("In OntologyLoadManagerLexGridImpl. Loading ontology from uri: "
						+ ontologyUri);

		// Get the LexBIGService
		LexBIGService lbs = LexBIGServiceImpl.defaultInstance();
		LexBIGServiceManager lbsm = lbs.getServiceManager(null);

		// remove existing scheme if it exists before parsing...
		try {
			cleanup(ob);
		} catch (LBParameterException e) {
			// ignore this exception, coding scheme not found
		}

		Loader loader = null;
		CodingSchemeManifest csm = createCodingSchemeManifest(ob);

		// Load OBO
		if (ob.getFormat().equalsIgnoreCase(ApplicationConstants.FORMAT_OBO)) {
			loader = lbsm
					.getLoader(org.LexGrid.LexBIG.Impl.loaders.OBOLoaderImpl.name);
			loader.setCodingSchemeManifest(csm);
			((OBO_Loader) loader).load(ontologyUri, null, stopOnErrors, async);
			// Load UMLS
		} else if (ob.getFormat().equalsIgnoreCase(
				ApplicationConstants.FORMAT_UMLS_RRF)) {
			loader = lbsm
					.getLoader(org.LexGrid.LexBIG.Extensions.Load.UmlsBatchLoader.NAME);
			((UmlsBatchLoader) loader).loadUmls(ontologyUri, ob
					.getTargetTerminologies());

			/*
			 * loader = lbsm
			 * .getLoader(org.LexGrid.LexBIG.Impl.loaders.UMLSLoaderImpl.name);
			 * LocalNameList lnl = getLocalNameListFromTargetTerminologies(ob);
			 * log.debug("Using the UMLS loader. Target terminology= " +
			 * ObjectToString.toString(lnl)); ((UMLS_Loader)
			 * loader).load(ontologyUri, lnl, stopOnErrors, async);
			 */
			// Load LEXGRID XML
		} else if (ob.getFormat().equalsIgnoreCase(
				ApplicationConstants.FORMAT_LEXGRID_XML)) {
			loader = lbsm
					.getLoader(org.LexGrid.LexBIG.Impl.loaders.LexGridLoaderImpl.name);
			((LexGrid_Loader) loader).load(ontologyUri, stopOnErrors, async);
			// Load OWL
		} else if (ob.getFormat().equalsIgnoreCase(
				ApplicationConstants.FORMAT_OWL_DL)
				|| ob.getFormat().equalsIgnoreCase(
						ApplicationConstants.FORMAT_OWL)
				|| ob.getFormat().equalsIgnoreCase(
						ApplicationConstants.FORMAT_OWL_FULL)) {
			loader = lbsm
					.getLoader(org.LexGrid.LexBIG.Impl.loaders.OWLLoaderImpl.name);
			loader.setCodingSchemeManifest(csm);

			// Load only NCI Thesaurus for now.
			if (ob.getFilePath() != null
					&& ob.getFilePath().indexOf("Thesaurus") >= 0) {
				((OWL_Loader) loader).loadNCI(ontologyUri, null, false,
						stopOnErrors, async);
			} else {
				int memSafe = 1;
				((OWL_Loader) loader).load(ontologyUri, null, memSafe,
						stopOnErrors, async);
			}
		}

		// No Loader could be found for the format
		if (loader == null) {
			String error_msg = "No LexBIG loader could be found to load the ontology format '"
					+ ob.getFormat() + "'";
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

			String urnAndVersion = urn + "|" + version;
			ob.setCodingScheme(urnAndVersion);
			log
					.debug("Updating the NcboOntologyMetadata with the codingScheme name="
							+ urnAndVersion);
			ontologyMetadataManager.saveOntologyOrView(ob);
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
	 * @param ontologyBean
	 *            the ontology_bean that contains the metadata information of
	 *            the ontology to be loaded.
	 * 
	 * @exception Exception
	 *                catch all for all other ontology file load errors.
	 */
	public void cleanup(OntologyBean ontologyBean) throws Exception {
		// Get the LexBIGService
		LexBIGService lbs = LexBIGServiceImpl.defaultInstance();
		LexBIGServiceManager lbsm = lbs.getServiceManager(null);

		// remove existing scheme if it exists before parsing...
		String codingSchemeName = ontologyBean.getCodingScheme();
		CodingSchemeRendering csRendering = getCodingSchemeRendering(lbs,
				codingSchemeName);

		if (csRendering != null) {
			AbsoluteCodingSchemeVersionReference acsvr = Constructors
					.createAbsoluteCodingSchemeVersionReference(csRendering
							.getCodingSchemeSummary());
			lbsm.deactivateCodingSchemeVersion(acsvr, null);
			lbsm.removeCodingSchemeVersion(acsvr);
			ontologyBean.setCodingScheme(null);
			ontologyMetadataManager.saveOntologyOrView(ontologyBean);
		} else {
			throw new LBParameterException(
					"No coding scheme could be located for the values you provided",
					"codingSchemeName", (new StringBuilder()).append(
							codingSchemeName).toString());
		}
	}

	public LocalNameList getLocalNameListFromTargetTerminologies(OntologyBean ob) {
		LocalNameList lnl = null;
		String targetTerminologies = ob.getTargetTerminologies();
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
						ApplicationConstants.FORMAT_OWL)
				|| ontology_bean.getFormat().equalsIgnoreCase(
						ApplicationConstants.FORMAT_OWL_DL)
				|| ontology_bean.getFormat().equalsIgnoreCase(
						ApplicationConstants.FORMAT_OWL_FULL)) {
			// Override registered name using metadata from the ontology bean
			String strCodingSchemeURI = "http://www.bioontology.org/"
					+ ontology_bean.getId().toString() + "/"
					+ ontology_bean.getDisplayLabel();
			// CodingSchemeManifest needs an id for it to be valid...set the id
			// value
			csm.setId(strCodingSchemeURI);
			CsmfCodingSchemeURI csmfCSURI = new CsmfCodingSchemeURI();
			csmfCSURI.setContent(strCodingSchemeURI);
			csm.setCodingSchemeURI(csmfCSURI);
			// Override version using metadata from the ontology bean
			CsmfVersion csmfVersion = new CsmfVersion();
			String version = ontology_bean.getOntologyId() + "/"
					+ ontology_bean.getInternalVersionNumber().toString();
			csmfVersion.setContent(version);
			csm.setRepresentsVersion(csmfVersion);
			// Override formal name using metadata from the ontology bean
			CsmfFormalName csmfFormalName = new CsmfFormalName();
			csmfFormalName.setContent(ontology_bean.getDisplayLabel());
			csm.setFormalName(csmfFormalName);
		}

		return csm;
	}

	public OntologyMetricsBean extractOntologyMetrics(OntologyBean ontologyBean)
			throws Exception {
		OntologyMetricsBean res = new OntologyMetricsBean();
		// TODO: fill in
		res.setId(ontologyBean.getId());

		return res;
	}
}
