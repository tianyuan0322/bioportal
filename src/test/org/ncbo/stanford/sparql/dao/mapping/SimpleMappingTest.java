package org.ncbo.stanford.sparql.dao.mapping;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ncbo.stanford.bean.concept.ConceptOntologyPairBean;
import org.ncbo.stanford.bean.mapping.MappingUserStatsBean;
import org.ncbo.stanford.sparql.bean.Mapping;
import org.ncbo.stanford.sparql.bean.ProcessInfo;
import org.ncbo.stanford.util.sparql.MappingFilterGenerator;
import org.ncbo.stanford.util.sparql.SPARQLUnionGenerator;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test RDF store connectivity.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext-services-test-mapping.xml"})
public class SimpleMappingTest {

	private static URI mappingId;

	@Autowired
	CustomNcboMappingDAO mappingDAO;
 
	@Autowired
	CustomNcboMappingCountsDAO mappingCountsDAO;

	@Autowired
	CustomNcboMappingStatsDAO mappingStatsDAO;

    //@SuppressWarnings("unchecked")
	@Test
	public void testAllOK() throws Exception {
		//testPairConceptOnt();
		//createMapping();
		//deleteMapping();
        //testUnionGenerator();
		//getMappingTest();
        //getMappingsForConceptTest();
        //getMappingsBetweenConceptsTest();
        //getMappingsToConceptTest();
        //getMappingsFromConceptTest();
        //getMappingsForOntologyTest();
		//int a = getMappingsBetweenOntologiesTest(1032);
		//getOntologyUserCountTest();
        //getMappingsToOntologyTest();
        //getCountMappingsBetweenConceptsTest();
        //getCountMappingsToConceptTest();
        //getCountMappingsFromConceptTest();
        //getCountMappingsForConceptTest();
        //getCountMappingsForOntologyTest();
        //getCountMappingsBetweenOntologiesTest();
        //getCountMappingsToOntologyTest();
        getCountMappingsFromOntologyTest();
	}

    @SuppressWarnings("unchecked")
	//@Test
    public void testPairConceptOnt() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            String [][] pairsString = {
            		{ "http://purl.org/obo/owl/PO#PO_0007014", "1038" },
            		{"http://purl.obolibrary.org/obo/CHEBI_28324", "3022" },
            		{"http://bioontology.org/projects/ontologies/birnlex#birnlex_557", "1089" },
            };
    		List<ConceptOntologyPairBean> pairs = new ArrayList<ConceptOntologyPairBean>();
    		for (String [] pairString : pairsString) {
    			ConceptOntologyPairBean po = new ConceptOntologyPairBean();
    			po.setConceptId(pairString[0]);
    			po.setOntologyId(Integer.parseInt(pairString[1]));
    			pairs.add(po);
    		}
    		mappingDAO.getMappingsByConceptOntologyPairs(pairs);
            System.out.printf("testPairConceptOnt %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public void testUnionGenerator() {
        SPARQLUnionGenerator g = new SPARQLUnionGenerator();
        g.setLimit(100);
        g.setOffset(1);
        g.addBindValue(new URIImpl("http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id"),
                new LiteralImpl("1009", new URIImpl("http://www.w3.org/2001/XMLSchema#integer")));
        
        g.addBindValue(new URIImpl("http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id"),
                new LiteralImpl("1032", new URIImpl("http://www.w3.org/2001/XMLSchema#integer")));
         
        g.addBindValue(new URIImpl("http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source"),
                new URIImpl("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Gallbladder_Disorder"));

        g.addBindValue(new URIImpl("http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source"),
                new URIImpl("http://purl.org/obo/owl/DOID#DOID_0000000"));

        g.addBidirectional(new URIImpl("http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source"),
                           new URIImpl("http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target"));
        g.addBidirectional(new URIImpl("http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#source_ontology_id"),
                           new URIImpl("http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#target_ontology_id"));
        System.out.println("query\n"+g.getSPARQLQuery());

    }

    @SuppressWarnings("unchecked")
	//@Test
    public void deleteMapping() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            mappingDAO.deleteMapping(new URIImpl("http://purl.bioontology.org/mapping/6377ee39-afb5-4d40-8fa5-172c4ebb25ba"));
            
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @SuppressWarnings("unchecked")
	//@Test
    public void createMapping() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            Mapping newMapping = new Mapping();

    		ProcessInfo procInfo = new ProcessInfo();
    		newMapping.setProcessInfo(procInfo);
    		
    		// Set all properties
    		newMapping.setComment("test create new mapping");
    		newMapping.setCreatedInSourceOntologyVersion(1003);
    		newMapping.setCreatedInTargetOntologyVersion(1004);
    		procInfo.setDate(new Date());
    		procInfo.setMappingSource("test unit desc");
    		procInfo.setMappingSourceName("test unit name");
    		procInfo.setMappingSourceAlgorithm("just a random test");
    		procInfo.setMappingSourcecontactInfo("manuelso");
    		procInfo.setMappingSourceSite(new  URIImpl("http://purl.bioontology.org/mapping/site"));
    		procInfo.setMappingType("test");
    		newMapping.setRelation(new  URIImpl("http://purl.bioontology.org/mapping/rel/test"));
    		List<URI> source = new ArrayList<URI>();
    		source.add(new  URIImpl("http://purl.bioontology.org/mapping/source/term/test"));
    		newMapping.setSource(source);
    		newMapping.setSourceOntologyId(6789);
    		procInfo.setSubmittedBy(39184);
    		List<URI> target = new ArrayList<URI>();
    		target.add(new  URIImpl("http://purl.bioontology.org/mapping/source/target/test"));
    		newMapping.setTarget(target);
    		newMapping.setTargetOntologyId(9876);

            mappingDAO.createMapping(newMapping);
            
            System.out.println("Created "+newMapping.getId());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @SuppressWarnings("unchecked")
	//@Test
    public void getMappingTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            String mappingId = "http://purl.bioontology.org/mapping/000015a0-ff2e-012d-748c-005056bd0010";
            // many to many     http://purl.bioontology.org/mapping/49e0ece0-0018-012e-7493-005056bd0010
            URI uriMappingId = new URIImpl(mappingId);
            Mapping m = this.mappingDAO.getMapping(uriMappingId);
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
	//@Test
    public void getMappingsToOntologyTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            List<Mapping> mappings = this.mappingDAO.getMappingsToOntology(1032,100,0,filter);
            System.out.println("getMappingsToOntologyTest --> "+ mappings.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingsToOntologyTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
	//@Test
    public int getMappingsBetweenOntologiesTest(int user) throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            List<Integer> users = new ArrayList<Integer>();
            users.add(user);
            filter.setSubmittedBy(users);
            List<Mapping> mappings = this.mappingDAO.getMappingsBetweenOntologies(1131,1136,false,1,0,filter);
            System.out.println("getMappingsBetweenOntologies --> "+ mappings.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingsBetweenOntologies %.3f sec. elapsed time\n",ts/1000.0);
            return mappings.size();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
	//@Test
    public void getMappingsForOntologyTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            List<Mapping> mappings = this.mappingDAO.getMappingsForOntology(1032,20,0,filter);
            System.out.println("getMappingsForOntologyTest --> "+ mappings.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingsForOntologyTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @SuppressWarnings("unchecked")
	//@Test
    public void getMappingsToConceptTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            String conceptId = "http://purl.org/obo/owl/DOID#DOID_0000000";
            List<Mapping> mappings = this.mappingDAO.getMappingsToConcept(1009,conceptId,20,0,filter);
            System.out.println("getMappingsToConceptTest --> "+ mappings.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingsToConceptTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
	//@Test
    public void getMappingsFromConceptTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            String conceptId = "http://purl.org/obo/owl/DOID#DOID_0000000";
            List<Mapping> mappings = this.mappingDAO.getMappingsFromConcept(1009,conceptId,20,0,filter);
            System.out.println("getMappingsFromConceptTest --> "+ mappings.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingsFromConceptTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @SuppressWarnings("unchecked")
	//@Test
    public void getMappingsForConceptTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            String conceptId = "http://purl.org/obo/owl/DOID#DOID_0000000";
            int ontologyId = 1009;
            MappingFilterGenerator filter = new MappingFilterGenerator();
            List<Mapping> r = this.mappingDAO.getMappingsForConcept(ontologyId,conceptId,10,0,filter);
            System.out.println("result --> "+ r.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
	//@Test
    public void getMappingsBetweenConceptsTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            String sourceConceptId = "http://purl.org/obo/owl/DOID#DOID_0000000";
            int sourceOntologyId = 1009;
            String targetConceptId = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Gallbladder_Disorder";
            int targetOntologyId = 1032;

            MappingFilterGenerator filter = new MappingFilterGenerator();
            List<Mapping> r = this.mappingDAO.getMappingsBetweenConcepts(sourceOntologyId,targetOntologyId,
            sourceConceptId,targetConceptId, false,
            10,0,filter);
            System.out.println("result --> "+ r.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingsBetweenConceptsTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void getMappingsFromOntologyTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            int ontologyId = 1009;


            MappingFilterGenerator filter = new MappingFilterGenerator();
            List<Mapping> r = this.mappingDAO.getMappingsFromOntology(ontologyId,10,0,filter);
            System.out.println("result --> "+ r.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingsFromOntologyTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void getMappingsForParametersTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            Calendar cal = Calendar.getInstance(); 
            MappingFilterGenerator filter = new MappingFilterGenerator();
            cal.clear();
            //2010-05-17T23:24:34Z
            cal.set(Calendar.YEAR,2010);
            cal.set(Calendar.MONTH,0); //Jan = 0
            cal.set(Calendar.DATE,15);
            cal.set(Calendar.HOUR,23);
            cal.set(Calendar.MINUTE,24);
            cal.set(Calendar.SECOND,34);
            //filter.setStartDate(cal.getTime());
            List<URI> rels = new ArrayList<URI>();
            rels.add(new URIImpl("http://www.w3.org/2004/02/skos/core#closeMatch"));
            filter.setRelationshipTypes(rels);
            List<Mapping> r = this.mappingDAO.getMappingsForParameters(10,0,filter);
            System.out.println("result --> "+ r.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getMappingsForParametersTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }


    public void getRecentMappingsTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            List<Mapping> r = this.mappingStatsDAO.getRecentMappings(10);
            System.out.println("result --> "+ r.size());
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getRecentMappingsTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

  
     public void getCountMappingsFromConceptTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            String sourceConceptId = "http://purl.org/obo/owl/DOID#DOID_0000000";
            int sourceOntologyId = 1009;
            String targetConceptId = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Gallbladder_Disorder";
            int targetOntologyId = 1032;

            MappingFilterGenerator filter = new MappingFilterGenerator();
            int x = this.mappingCountsDAO.getCountMappingsFromConcept(sourceOntologyId,sourceConceptId,filter);
            System.out.println("result --> "+x);
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getCountMappingsFromConceptTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

   public void getCountMappingsToConceptTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            String sourceConceptId = "http://purl.org/obo/owl/DOID#DOID_0000000";
            int sourceOntologyId = 1009;
            String targetConceptId = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Gallbladder_Disorder";
            int targetOntologyId = 1032;

            MappingFilterGenerator filter = new MappingFilterGenerator();
            int x = this.mappingCountsDAO.getCountMappingsToConcept(sourceOntologyId,sourceConceptId,filter);
            System.out.println("result --> "+x);
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getCountMappingsToConceptTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public void getCountMappingsBetweenConceptsTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            String sourceConceptId = "http://purl.org/obo/owl/DOID#DOID_0000000";
            int sourceOntologyId = 1009;
            String targetConceptId = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Gallbladder_Disorder";
            int targetOntologyId = 1032;

            MappingFilterGenerator filter = new MappingFilterGenerator();
            int x = this.mappingCountsDAO.getCountMappingsBetweenConcepts(sourceOntologyId,targetOntologyId,
            sourceConceptId,targetConceptId, false,filter);
            System.out.println("result --> "+x);
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getCountMappingsBetweenConceptsTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void getCountMappingsToOntologyTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            Integer sourceOntologyId = 1009;
            Integer targetOntologyId = 1032;
            int x = this.mappingCountsDAO.getCountMappingsToOntology(sourceOntologyId,filter);
            System.out.println("result --> "+ x);
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getCountMappingsBetweenOntologies %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void getCountMappingsFromOntologyTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            //List<URI> rels = new ArrayList<URI>();
            //rels.add(new URIImpl("http://www.w3.org/2004/02/skos/core#closeMatch"));
            //filter.setRelationshipTypes(rels);
            Calendar cal = Calendar.getInstance(); 
            cal.set(Calendar.YEAR,2010);
            cal.set(Calendar.MONTH,0); //Jan = 0
            cal.set(Calendar.DATE,15);
            cal.set(Calendar.HOUR,23);
            cal.set(Calendar.MINUTE,24);
            cal.set(Calendar.SECOND,34);
            filter.setStartDate(cal.getTime());
            Integer sourceOntologyId = 1057;
            Integer targetOntologyId = 1032;
            int x = this.mappingCountsDAO.getCountMappingsFromOntology(sourceOntologyId,null);
            System.out.println("result --> "+ x);
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getCountMappingsFromOntologyTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void getCountMappingsBetweenOntologiesTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            Integer sourceOntologyId = 1009;
            Integer targetOntologyId = 1032;
            int x = this.mappingCountsDAO.getCountMappingsBetweenOntologies(sourceOntologyId,targetOntologyId,false,filter);
            System.out.println("result --> "+ x);
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getCountMappingsBetweenOntologies %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void getCountMappingsForOntologyTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            Integer sourceOntologyId = 1009;
            int x = this.mappingCountsDAO.getCountMappingsForOntology(sourceOntologyId,filter);
            System.out.println("result --> "+ x);
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getCountMappingsForConceptTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void getCountMappingsForConceptTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            String conceptId = "http://purl.org/obo/owl/DOID#DOID_0000000";
            Integer sourceOntologyId = 1009;
            int x = this.mappingCountsDAO.getCountMappingsForConcept(sourceOntologyId,conceptId,filter);
            System.out.println("result --> "+ x);
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getCountMappingsForConceptTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public void getOntologyUserCountTest() throws Exception {
        try {
            long ts = System.currentTimeMillis();
            MappingFilterGenerator filter = new MappingFilterGenerator();
            Integer sourceOntologyId = 1131;
            Integer targetOntologyId = 1136;
             List<MappingUserStatsBean> x = this.mappingStatsDAO.getOntologyUserCount(sourceOntologyId,targetOntologyId);
            System.out.println("result --> "+ x.size());
            for (MappingUserStatsBean u : x)
                System.out.println("\t " + u.getUserId() + " --> "+ u.getMappingCount());
            	
            ts = System.currentTimeMillis() - ts;
            System.out.printf("getOntologyUserCountTest %.3f sec. elapsed time\n",ts/1000.0);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
