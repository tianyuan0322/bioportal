-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.77-community


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema bioportal_protege
--

CREATE DATABASE IF NOT EXISTS bioportal_protege;
USE bioportal_protege;

--
-- Definition of table `metadata`
--

DROP TABLE IF EXISTS `metadata`;
CREATE TABLE `metadata` (
  `frame` varbinary(500) NOT NULL,
  `frame_type` smallint(6) NOT NULL,
  `slot` varbinary(500) NOT NULL,
  `facet` varbinary(500) NOT NULL,
  `is_template` bit(1) NOT NULL,
  `value_index` int(11) NOT NULL,
  `value_type` smallint(6) NOT NULL,
  `short_value` varchar(500) character set utf8 default NULL,
  `long_value` mediumtext,
  KEY `metadata_I1` (`frame`,`slot`,`facet`,`is_template`,`value_index`),
  KEY `metadata_I2` (`short_value`(255)),
  KEY `metadata_I3` (`slot`,`frame_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `metadata`
--

/*!40000 ALTER TABLE `metadata` DISABLE KEYS */;
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#:OWL-ONTOLOGY-POINTER-CLASS_1',31,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-POINTER-PROPERTY','','\0',0,29,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#:OWL-ONTOLOGY-POINTER-CLASS_1',31,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,11,':OWL-ONTOLOGY-POINTER-CLASS',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#:OWL-ONTOLOGY-POINTER-CLASS_1',31,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#:OWL-ONTOLOGY-POINTER-CLASS_1',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#:OWL-ONTOLOGY-POINTER-CLASS_1',31,':DIRECT-TYPE','','\0',0,11,':OWL-ONTOLOGY-POINTER-CLASS',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://www.w3.org/2002/07/owl#imports','','\0',0,29,'http://swrl.stanford.edu/ontologies/3.3/swrla.owl',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://www.w3.org/2002/07/owl#imports','','\0',1,29,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://www.w3.org/2002/07/owl#imports','','\0',2,29,'http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',0,3,'sqwrl:http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',1,3,'xsd:http://www.w3.org/2001/XMLSchema#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',2,3,'metadata:http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',3,3,'protege:http://protege.stanford.edu/plugins/owl/protege#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',4,3,'swrlb:http://www.w3.org/2003/11/swrlb#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',5,3,'rdfs:http://www.w3.org/2000/01/rdf-schema#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',6,3,'owl:http://www.w3.org/2002/07/owl#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',7,3,'swrla:http://swrl.stanford.edu/ontologies/3.3/swrla.owl#',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',8,3,'xsp:http://www.owl-ontologies.com/2005/08/07/xsp.owl#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',9,3,'swrl:http://www.w3.org/2003/11/swrl#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',10,3,'j.0:http://biostorm.stanford.edu/db_table_classes/DSN_jdbc.mysql.//ncbo-db-stage1.sunet.3306/bioportal#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',11,3,'rdf:http://www.w3.org/1999/02/22-rdf-syntax-ns#',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://protege.stanford.edu/plugins/owl/protege#OWL-ONTOLOGY-PREFIXES','','\0',12,3,':http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://www.w3.org/2002/07/owl#Ontology',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',29,':DIRECT-TYPE','','\0',0,9,'http://www.w3.org/2002/07/owl#Ontology',NULL),
 (':OWL-ONTOLOGY-POINTER-CLASS',11,':DIRECT-INSTANCES','','\0',0,31,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#:OWL-ONTOLOGY-POINTER-CLASS_1',NULL),
 ('http://www.w3.org/2002/07/owl#Ontology',9,':DIRECT-INSTANCES','','\0',0,29,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',NULL);
/*!40000 ALTER TABLE `metadata` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
