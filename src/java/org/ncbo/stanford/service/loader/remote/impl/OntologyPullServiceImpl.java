package org.ncbo.stanford.service.loader.remote.impl;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.loader.remote.OntologyPullService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.loader.LoaderUtils;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.URIUploadFilePathHandlerImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * A utility that runs as a scheduled process, that processes all the latest
 * ontologies that have a download location and is not manual and pulls the
 * ontologies that have changed since the last run.
 * 
 * @author Pradip Kanjamala
 */
@Transactional
public class OntologyPullServiceImpl implements OntologyPullService {

	private static final Log log = LogFactory
			.getLog(OntologyPullServiceImpl.class);

	private OntologyService ontologyService;

	/**
	 * Performs the pull of ontologies from OBO Sourceforge CVS
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void doOntologyPull() {
		try {

			doRemoteOntologyPull();
			if (log.isInfoEnabled()) {
				log.info("**** Ontology Pull completed successfully *****");
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	private void doRemoteOntologyPull() {
		try {
			if (log.isInfoEnabled()) {
				log.info("**** Ontology Pull Started *****");
			}
			List<OntologyBean> ont_list = ontologyService
					.findLatestOntologyVersions();

			for (OntologyBean ont : ont_list) {
				try {
					// Process only the ontologies that are not marked
					// manual, not metadataOnly and had a non blank downloadLocation
					if (isPullableOntology(ont)) {

						if (LoaderUtils.isValidDownloadLocation(ont
								.getDownloadLocation())) {

							if (LoaderUtils
									.hasDownloadLocationContentBeenUpdated(ont
											.getDownloadLocation(), ont)) {
								processCreateNewVersion(ont);

							} else {
								log.debug("[*** NO_ACTION: " + ont + " ***]");
							}
						} else {
							log
									.debug("[*** NO_ACTION: INVALID DOWNLOAD LOCATION- "
											+ ont + " ***]");
						}
					}

				} catch (Exception ex) {
					log.error("Error while pulling ontology " + ont);
					log.error("Error message= " + ex);
					ex.printStackTrace();

				}
			}

		} catch (Exception ex) {
			log.error(ex);
			ex.printStackTrace();
		}
		if (log.isInfoEnabled()) {
			log.info("**** Ontology Pull completed *****");
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

	private boolean isPullableOntology(OntologyBean ont) {
		boolean isPullable = true;
		if (ont.getIsManual() != null
				&& ont.getIsManual() == ApplicationConstants.TRUE) {
			return false;
		}
		if (ont.getIsMetadataOnly() != null
				&& ont.getIsMetadataOnly() == ApplicationConstants.TRUE) {
			return false;
		}
		if (StringUtils.isBlank(ont.getDownloadLocation())) {
			return false;
		}

		return isPullable;

	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

}