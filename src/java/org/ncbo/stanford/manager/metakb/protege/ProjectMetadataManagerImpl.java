package org.ncbo.stanford.manager.metakb.protege;

import java.util.List;

import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.metakb.ProjectMetadataManager;
import org.ncbo.stanford.manager.metakb.protege.DAO.ProjectDAO;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;

/**
 * Implementation of {@link ProjectMetadataManager} for Protege OWL metadata KB.
 * 
 * @author Tony Loeser
 */
public class ProjectMetadataManagerImpl extends SimpleObjectManagerImpl<ProjectBean>
		implements ProjectMetadataManager {

	private ProjectDAO projectDAO = null; // Do not access directly
	
	protected ProjectDAO getProjectDAO() {
		if (projectDAO == null) {
			projectDAO = new ProjectDAO(getMetadataKb());
		}
		return projectDAO;
	}
	
	protected AbstractDAO<ProjectBean> getAbstractDAO() {
		return getProjectDAO();
	}
	
	// Implement interface
	public List<ProjectBean> getProjectsForUser(Integer userId)
			throws MetadataException {
		String query = "SELECT ?obj " +
		   "WHERE { ?obj <metadata:userId> \""+userId+"\"^^xsd:int . " +
		   "        ?obj <rdf:type> <metadata:Project> . }";
		return getProjectDAO().getInstancesForSPARQLQuery(query);
	}
	
}
