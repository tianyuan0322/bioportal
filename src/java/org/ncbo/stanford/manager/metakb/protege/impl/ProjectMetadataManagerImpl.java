package org.ncbo.stanford.manager.metakb.protege.impl;

import java.util.List;

import org.ncbo.stanford.bean.metadata.ProjectBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.metakb.ProjectMetadataManager;


/**
 * Implementation of {@link ProjectMetadataManager} for Protege OWL metadata KB.
 * 
 * @author Tony Loeser
 */
public class ProjectMetadataManagerImpl extends SimpleObjectManagerImpl<ProjectBean>
		implements ProjectMetadataManager {

	public ProjectMetadataManagerImpl() {
		super(ProjectBean.class);
	}
	
	@Override
	public List<ProjectBean> getProjectsForUser(Integer userId)
			throws MetadataException {
		String query = "SELECT ?obj " +
		   "WHERE { ?obj <metadata:userId> \""+userId+"\"^^xsd:int . " +
		   "        ?obj <rdf:type> <metadata:Project> . }";
		return getDALayer().getDAOForBeanType(ProjectBean.class).getInstancesForSPARQLQuery(query);
	}
	
}
