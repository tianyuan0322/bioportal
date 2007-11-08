package org.ncbo.stanford.service.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.service.ontology.OntologyService;

public class OntologyServiceImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;

	public List<OntologyBean> findLatestOntologyVersions() {
		ArrayList<OntologyBean> ontBeanList = new ArrayList<OntologyBean>(1);
		List<NcboOntology> ontEntityList = ncboOntologyVersionDAO
				.findLatestOntologyVersions();

		for (NcboOntology ncboOntology : ontEntityList) {
			OntologyBean ontologyBean = new OntologyBean();

			ontologyBean.setId(ncboOntology.getId());
			ontologyBean.setOntologyId(ncboOntology.getOntologyId());
			ontologyBean.setInternalVersionNumber(ncboOntology
					.getInternalVersionNumber());
			ontologyBean.setVersionNumber(ncboOntology.getVersionNumber());
			ontologyBean.setVersionStatus(ncboOntology.getVersionStatus());
			ontologyBean.setFilePath(ncboOntology.getFilePath());
			ontologyBean.setIsCurrent(ncboOntology.getIsCurrent());
			ontologyBean.setIsRemote(ncboOntology.getIsRemote());
			ontologyBean.setIsReviewed(ncboOntology.getIsReviewed());
			ontologyBean.setDateCreated(ncboOntology.getDateCreated());
			ontologyBean.setDateReleased(ncboOntology.getDateReleased());
			ontologyBean.setDisplayLabel(ncboOntology.getDisplayLabel());
			ontologyBean.setFormat(ncboOntology.getFormat());
			ontologyBean.setContactName(ncboOntology.getContactName());
			ontologyBean.setContactEmail(ncboOntology.getContactEmail());
			ontologyBean.setHomepage(ncboOntology.getHomepage());
			ontologyBean.setDocumentation(ncboOntology.getDocumentation());
			ontologyBean.setPublication(ncboOntology.getPublication());
			ontologyBean.setUrn(ncboOntology.getUrn());
			ontologyBean.setIsFoundry(ncboOntology.getIsFoundry());
			ontBeanList.add(ontologyBean);
		}

		return ontBeanList;
	}

	/**
	 * @return the ncboOntologyVersionDAO
	 */
	public CustomNcboOntologyVersionDAO getNcboOntologyVersionDAO() {
		return ncboOntologyVersionDAO;
	}

	/**
	 * @param ncboOntologyVersionDAO
	 *            the ncboOntologyVersionDAO to set
	 */
	public void setNcboOntologyVersionDAO(
			CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
	}
}
