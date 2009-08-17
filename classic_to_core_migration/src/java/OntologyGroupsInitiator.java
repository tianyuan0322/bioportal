import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.GroupBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.util.metadata.OntologyGroupMetadataUtils;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;

import com.thoughtworks.xstream.XStream;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;


/*
[11:38:26 AM] nickg788 says: filter_groups = {
	"OBO Foundry"=>"OBO Foundry",
	"caBIG"=>"Cancer Biomedical Informatics Grid",
#	"NCIBI"=>"National Center for Integrative Biomedical Informatics",
#	"CTSA"=>"Clinical and Translational Science Awards",
	"WHO-FIC"=>"The WHO Family of International Classifications",
	"PSI"=>"Proteomics Standards Initiative"
#	"MSI"=>"Metabolomics Standards Initiative"		//Note: Drop it!	
}
[11:38:42 AM] nickg788 says: ontology_groups = {
	1090=>["OBO Foundry"],
	1070=>["OBO Foundry"],
	1049=>["OBO Foundry"],
	1048=>["OBO Foundry"],
	1067=>["OBO Foundry"],
	1006=>["OBO Foundry"],
	1102=>["OBO Foundry"],
	1037=>["OBO Foundry"],
	1007=>["OBO Foundry"],
	1063=>["OBO Foundry"],
	1008=>["OBO Foundry"],
	1016=>["OBO Foundry"],
	1015=>["OBO Foundry"],
	1069=>["OBO Foundry"],
	1012=>["OBO Foundry"],
	1064=>["OBO Foundry"],
	1071=>["OBO Foundry"],
	1019=>["OBO Foundry"],
	1021=>["OBO Foundry"],
	1022=>["OBO Foundry"],
	1009=>["OBO Foundry"],
	1092=>["OBO Foundry"],
	1025=>["OBO Foundry"],
	1105=>["OBO Foundry","PSI"],
	1027=>["OBO Foundry"],
	1131=>["MGED","caBIG"],		//Note: MGED is not needed
	1103=>["OBO Foundry"],
	1030=>["OBO Foundry"],
	1077=>["OBO Foundry"],
	1000=>["OBO Foundry"],
	1010=>["OBO Foundry"],
	1031=>["OBO Foundry"],
	1083=>["caBIG"],
	1032=>["caBIG"],
	1033=>["OBO Foundry","MSI"],
	1042=>["OBO Foundry"],
	1123=>["OBO Foundry"],
	1094=>["OBO Foundry"],
	1107=>["OBO Foundry"],
	1038=>["OBO Foundry"],
	1108=>["OBO Foundry"],
	1041=>["OBO Foundry"],
	1062=>["OBO Foundry"],
	1040=>["OBO Foundry","PSI"],
	1044=>["PSI"],
	1109=>["OBO Foundry"],
	1078=>["OBO Foundry"],
	1091=>["OBO Foundry"],
	1093=>["OBO Foundry"],
	1145=>["OBO Foundry"],
	1046=>["OBO Foundry"],
	1110=>["OBO Foundry"],
	1081=>["OBO Foundry"],
	1065=>["OBO Foundry"],
	1112=>["OBO Foundry"],
	1095=>["OBO Foundry"],
	1115=>["OBO Foundry"],
	1051=>["OBO Foundry"],
	1057=>["caBIG"],
	1101=>["WHO-FIC"],
	1041=>["PSI"]
	
	+ UMLS from Trish
}
 */

/**
 * @author Csongor Nyulas
 *
 */
@SuppressWarnings("unchecked")
public class OntologyGroupsInitiator {
	
	public static final String PROPERTY_FILENAME = "build.properties";
	
	public static final GroupBean[] BIOPORTAL_GROUP_DEFINITIONS = {
		new GroupBean(6001, "OBO Foundry", "OBO Foundry"),
		new GroupBean(6002, "Cancer Biomedical Informatics Grid", "caBIG"),
		new GroupBean(6003, "National Center for Integrative Biomedical Informatics", "NCIBI"),
		new GroupBean(6004, "Clinical and Translational Science Awards", "CTSA"),
		new GroupBean(6005, "The WHO Family of International Classifications", "WHO-FIC"),
		new GroupBean(6006, "Proteomics Standards Initiative", "PSI"),
		new GroupBean(6007, "Metabolomics Standards Initiative", "MSI"),
		new GroupBean(6008, "Unified Medical Language System", "UMLS")
	};
	public static final Integer[] OBO_FOUNDRY = {6001};
	public static final Integer[] OBO_FOUNDRY_PSI = {6001, 6006};
	public static final Integer[] OBO_FOUNDRY_MSI = {6001, 6007};
	public static final Integer[] CABIG = {6002};
	public static final Integer[] PSI = {6006};
	public static final Integer[] WHO_FIC = {6005};
	public static final Integer[] UMLS = {6008};
	public static final Integer[] OBO_FOUNDRY_UMLS = {6001, 6008};
	public static final Integer[] CABIG_UMLS = {6002, 6008};

	private static Properties properties = new Properties();
	public static Map<Integer, List<Integer>> ONTOLOGY_TO_GROUP_MAP;

	static {
		try {
			properties.load(new FileInputStream(PROPERTY_FILENAME));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ONTOLOGY_TO_GROUP_MAP = new HashMap<Integer, List<Integer>>();
		
		ONTOLOGY_TO_GROUP_MAP.put(1090, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1070, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY_UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1049, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1048, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1067, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1006, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1102, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1037, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1007, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1063, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1008, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1016, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1015, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1069, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1012, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1064, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1071, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1019, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1021, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1022, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1009, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1092, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1025, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1105, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY_PSI)));
		ONTOLOGY_TO_GROUP_MAP.put(1027, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1131, new ArrayList<Integer>(Arrays.asList(CABIG)));
		ONTOLOGY_TO_GROUP_MAP.put(1103, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1030, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1077, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1000, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1010, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1031, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1083, new ArrayList<Integer>(Arrays.asList(CABIG)));
		ONTOLOGY_TO_GROUP_MAP.put(1032, new ArrayList<Integer>(Arrays.asList(CABIG_UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1033, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY_MSI)));
		ONTOLOGY_TO_GROUP_MAP.put(1042, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1123, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1094, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1107, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1038, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1108, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1041, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1062, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1040, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY_PSI)));
		ONTOLOGY_TO_GROUP_MAP.put(1044, new ArrayList<Integer>(Arrays.asList(PSI)));
		ONTOLOGY_TO_GROUP_MAP.put(1109, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1078, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1091, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1093, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1145, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1046, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1110, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1081, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1065, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1112, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1095, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1115, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1051, new ArrayList<Integer>(Arrays.asList(OBO_FOUNDRY)));
		ONTOLOGY_TO_GROUP_MAP.put(1057, new ArrayList<Integer>(Arrays.asList(CABIG)));
		ONTOLOGY_TO_GROUP_MAP.put(1101, new ArrayList<Integer>(Arrays.asList(WHO_FIC)));
		ONTOLOGY_TO_GROUP_MAP.put(1041, new ArrayList<Integer>(Arrays.asList(PSI)));
		ONTOLOGY_TO_GROUP_MAP.put(1341, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1342, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1343, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1344, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1350, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1351, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1347, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1352, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1348, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1349, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1353, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1354, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1053, new ArrayList<Integer>(Arrays.asList(UMLS)));
		ONTOLOGY_TO_GROUP_MAP.put(1132, new ArrayList<Integer>(Arrays.asList(UMLS)));

	}
	
	public static final String BIOPORTAL_GROUP_INFO_FILENAME = "classic_to_core_migration/frontend_data/bp_groups.xml";
	private static final String METADATA_TABLE_NAME = "metadata";
	private static final Log log = LogFactory.getLog(AbstractOntologyManagerProtege.class);

	private static final String CLASS_ONTOLOGY_GROUP = OntologyGroupMetadataUtils.CLASS_ONTOLOGY_GROUP;
	
	public enum RegenerateXmlOption {True, False};

	class OntologyGroups {
		private List<GroupBean> groupDefinitions = new ArrayList<GroupBean>(Arrays.asList(BIOPORTAL_GROUP_DEFINITIONS));
		private Map<Integer, List<Integer>> ontologyToGroupMapping = ONTOLOGY_TO_GROUP_MAP;
		
		@Override
		public String toString() {
			return "groupDefinitions: " + groupDefinitions + "\n" +
					"ontologyToGroupMapping: " + ontologyToGroupMapping;
		}
	}
	
	public static void main(String[] args) {
		OntologyGroupsInitiator initiator = new OntologyGroupsInitiator();
		
		initiator.writeToOntology(BIOPORTAL_GROUP_INFO_FILENAME, RegenerateXmlOption.False);
	}

	private void createXMLFileWithBioPortalGroupInfo(String fileName) {
		XStream xstream = new XStream();
		setXStreamAliases(xstream);
		File f = new File(fileName);
		if ( ! f.exists() ) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(f);
			
			OntologyGroups info = new OntologyGroups();
			
			xstream.toXML(info, fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	private void setXStreamAliases(XStream xstream) {
		xstream.alias("GroupBean", GroupBean.class);
		xstream.alias("OntologyGroups", OntologyGroups.class);
	}

	
	private void writeToOntology(String fileName, RegenerateXmlOption regenerateXML) {
		XStream xstream = new XStream();
		setXStreamAliases(xstream);
		File f = new File(fileName);
		if ( (! f.exists()) || (regenerateXML == RegenerateXmlOption.True) ) {
			createXMLFileWithBioPortalGroupInfo(fileName);
		}
		FileReader fw = null;
		try {
			fw = new FileReader(f);
			
			OntologyGroups info = (OntologyGroups) xstream.fromXML(fw);
			log.info(info);
			
			String jdbcUrl = properties.getProperty("protege.jdbc.url");
			String jdbcDriver = properties.getProperty("protege.jdbc.driver");
			String jdbcUsername = properties.getProperty("protege.jdbc.username");
			String jdbcPassword = properties.getProperty("protege.jdbc.password");
			OWLModel owlModel = getMetadataOWLModel(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);
			
			writeInfoToOntology(info, owlModel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	

	/**
	 * Copied from {@link org.ncbo.stanford.manager.AbstractOntologyManagerProtege#getMetadataOWLModel()}
	 * TODO please synchronize these to methods after any changes 
	 * @param protegeJdbcDriver 
	 * @param protegeJdbcUrl 
	 * @param protegeJdbcUsername 
	 * @param protegeJdbcPassword 
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private OWLModel getMetadataOWLModel(
			String protegeJdbcDriver, String protegeJdbcUrl, 
			String protegeJdbcUsername, String protegeJdbcPassword) {
		// TODO this solution is a temporary hack. 
		// We should use the creator after migration to Protege 3.4.1
		//start...
		DatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();

		List errors = new ArrayList();
        Project project = Project.createBuildProject(factory, errors);
		DatabaseKnowledgeBaseFactory.setSources(project.getSources(),
				protegeJdbcDriver, protegeJdbcUrl, METADATA_TABLE_NAME,
				protegeJdbcUsername, protegeJdbcPassword);
        project.createDomainKnowledgeBase(factory, errors, false);
        KnowledgeBase kb = project.getKnowledgeBase();
        OWLModel owlModel = (OWLModel) kb;
        
        Repository repository = new LocalFolderRepository(new File(properties.getProperty("bioportal.metadata.includes.path")), true);
        owlModel.getRepositoryManager().addProjectRepository(repository);
        
        MergingNarrowFrameStore mnfs = MergingNarrowFrameStore.get(owlModel);
        owlModel.setGenerateEventsEnabled(false);
        NarrowFrameStore nfs = factory.createNarrowFrameStore("<new>");
        mnfs.addActiveFrameStore(nfs);
        factory.loadKnowledgeBase(owlModel, project.getSources(), errors);	        
        owlModel.setGenerateEventsEnabled(true);
        owlModel.setChanged(false);
        project.getInternalProjectKnowledgeBase().setChanged(false);
        //end
        
        if (!errors.isEmpty()) {
        	log.error("Errors during Protege metadata project creation: " + errors);
        }

		if (log.isDebugEnabled()) {
			log.debug("Created new metadata knowledgebase: " + kb.getName());
		}

		return owlModel;
	}
	
	private void writeInfoToOntology(OntologyGroups info,
			OWLModel owlModel) {
		
		//create OntologyGroup instances
		for (GroupBean gb : info.groupDefinitions) {
			try {
				OWLIndividual ontGroupInd = getOrCreateOntologyGroupIndividual(owlModel, gb.getId());
				OntologyGroupMetadataUtils.fillInGroupInstancePropertiesFromBean(ontGroupInd, gb);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//add groups to VirtualOntology instances
		OWLProperty belongsToGroupProp = owlModel.getOWLProperty(OntologyGroupMetadataUtils.PROPERTY_BELONGS_TO_GROUP);
		if (belongsToGroupProp == null) {
			log.error("Metadata ontology does not contain the property: " + 
					OntologyGroupMetadataUtils.PROPERTY_BELONGS_TO_GROUP + "!");
		}
		else {
			for (Integer vOntId : info.ontologyToGroupMapping.keySet()) {
				OWLIndividual vOntInd = getVirtualOntologyOrViewInstance(owlModel, vOntId);
				if (vOntInd == null) {
					log.error("Virtual ontology or view with id " + vOntId + " could not be found in the metadata ontology!");
					continue;
				}
				List<Integer> groupIds = info.ontologyToGroupMapping.get(vOntId);
				log.info("Setting groups " + groupIds + " for virtual ontology " + vOntId +".");
				vOntInd.setPropertyValue(belongsToGroupProp, null);
				for (Integer grpId : groupIds) {
					vOntInd.addPropertyValue(belongsToGroupProp, getOrCreateOntologyGroupIndividual(owlModel, grpId));
				}
			}
		}
	}

	
	private OWLIndividual getOrCreateOntologyGroupIndividual(OWLModel owlModel, Integer id) {
		String ontGroupIndName = 
			properties.getProperty("metadata.ontologygroup.inst.prefix") +
			id + 
			properties.getProperty("metadata.ontologygroup.inst.suffix");
		OWLNamedClass ontGrpClass = owlModel.getOWLNamedClass(CLASS_ONTOLOGY_GROUP);
		OWLIndividual ontGroupInd = owlModel.getOWLIndividual(ontGroupIndName);
		//alternative lookup
		if (ontGroupInd == null) {
			ontGroupInd = OntologyGroupMetadataUtils.getOntologyGroupWithId(owlModel, id);
			if (ontGroupInd != null) {
				log.warn("Ontology group instance for id: " + id + " has been found having non-standard name: " + ontGroupInd);
			}
		}
		if (ontGroupInd == null) {
			ontGroupInd = ontGrpClass.createOWLIndividual(ontGroupIndName);
			log.info("Created new OntologyGroup instance with name: " + ontGroupIndName);
		}
		return ontGroupInd;
	}
	
	
	private OWLIndividual getVirtualOntologyOrViewInstance(OWLModel metadata, int id) {//TODO
		String ontInstName = 
			properties.getProperty("metadata.virtualontology.inst.prefix") +
			id + 
			properties.getProperty("metadata.virtualontology.inst.suffix");
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		if (ontInd == null) {
			String viewInstName = 
				properties.getProperty("metadata.virtualview.inst.prefix") +
				id + 
				properties.getProperty("metadata.virtualview.inst.suffix");
			ontInd = metadata.getOWLIndividual(viewInstName);
		}
		//alternative lookup
		if (ontInd == null) {
			ontInd = OntologyMetadataUtils.getVirtualOntologyOrViewWithId(metadata, id);
			if (ontInd != null) {
				log.warn("Virtual ontology or view instance for id: " + id + " has been found having non-standard name: " + ontInd);
			}
		}
		return ontInd;
	}

}
