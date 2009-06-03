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
 ('http://www.w3.org/2002/07/owl#Ontology',9,':DIRECT-INSTANCES','','\0',0,29,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2809',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2809',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5063',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5063',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2820',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2820',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2821',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2821',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2810',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2810',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2816',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2816',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2819',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2819',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2808',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2808',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2815',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2815',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5062',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5062',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5059',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5059',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2807',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2807',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5058',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5058',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2804',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2804',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2806',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2806',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5064',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5064',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5057',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5057',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5060',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5060',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2818',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2818',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2814',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2814',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5061',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5061',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2805',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2805',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2813',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2813',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2801',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2801',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2802',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2802',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2822',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2822',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2817',25,':NAME','','\0',0,3,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2817',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5062',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',1,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5062',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5062',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5057',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',2,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5057',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5057',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2816',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',3,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2816',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2816',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5060',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',4,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5060',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5060',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2822',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',5,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2822',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2822',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2806',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',6,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2806',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2806',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2809',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',7,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2809',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2809',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5059',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',8,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5059',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5059',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5061',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',9,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5061',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5061',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2801',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',10,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2801',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2801',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2805',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',11,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2805',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2805',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',12,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2807',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',13,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2807',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2807',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5064',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',14,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5064',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5064',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2818',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',15,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2818',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2818',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2814',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',16,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2814',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2814',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5058',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',17,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5058',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5058',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2821',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',18,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2821',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2821',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2817',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',19,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2817',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2817',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5063',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',20,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5063',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5063',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',21,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2804',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',22,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2804',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2804',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2820',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',23,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2820',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2820',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2802',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',24,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2802',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2802',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2819',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',25,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2819',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2819',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2815',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',26,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2815',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2815',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2810',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',27,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2810',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2810',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2813',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',28,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2813',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2813',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2808',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',29,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2808',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2808',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',25,':DIRECT-TYPE','','\0',2,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://omv.ontoware.org/2005/05/ontology#OntologyDomain',9,':DIRECT-INSTANCES','','\0',30,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',25,'http://www.w3.org/1999/02/22-rdf-syntax-ns#type','','\0',0,9,'http://omv.ontoware.org/2005/05/ontology#OntologyDomain',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2811',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2801',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2801',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2820',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2820',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2812',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5063',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'5063',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2817',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2817',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2818',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2818',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2808',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2808',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2815',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2815',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2813',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2813',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5058',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'5058',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2816',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2816',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5057',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'5057',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2809',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2809',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2819',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2819',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2814',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2814',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2807',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2807',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5061',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'5061',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2802',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2802',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5062',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'5062',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2822',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2822',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2803',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2806',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2806',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2810',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2810',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2804',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2804',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2805',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2805',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5059',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'5059',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2821',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'2821',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5060',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'5060',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5064',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#id','','\0',0,1,'5064',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2802',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Chemical',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5062',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Human',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2819',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Plant Development',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2807',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'phenotype',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2814',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Human Developmental Anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2806',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'genomic-proteomic',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2815',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Microbial Anatomy',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5063',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Plant',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2816',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Plant Anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2817',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'mouse',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2815',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'microbial_gross_anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'animal_gross_anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2808',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Taxonomic Classification',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2810',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Animal Gross Anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5059',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Biomedical Resources',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2818',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Animal Development',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2813',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5058',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Health',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2821',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2820',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2805',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Experimental Conditions',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2813',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'fish',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2802',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'chemical',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2813',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Fish Anatomy',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2821',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Protein',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2810',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2816',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'plant_gross_anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Development',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'developmental',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2817',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Mouse Anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2819',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'plant_development',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2818',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2804',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Ethology',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2814',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'human',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'gross_anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2815',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2806',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Genomic and Proteomic',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2809',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'vocabularies',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2816',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2809',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Vocabularies',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2810',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'anatomy',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2822',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Physicochemical',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2820',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Gene Product',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2801',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Other',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5061',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Arabadopsis',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2818',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'animal_development',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2804',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'ethology',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5060',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Biological Process',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2822',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'physicochemical',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5057',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Imaging',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2807',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Phenotype',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2817',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2820',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2806',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5057',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'imaging_methods',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2819',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2803',NULL);
INSERT INTO `metadata` (`frame`,`frame_type`,`slot`,`facet`,`is_template`,`value_index`,`value_type`,`short_value`,`long_value`) VALUES 
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_5064',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Yeast',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2814',25,'http://omv.ontoware.org/2005/05/ontology#isSubDomainOf','','\0',0,25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2812',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2805',25,'http://protege.stanford.edu/ontologies/metadata/BioPortalMetadata.owl#oboFoundryName','','\0',0,3,'experimental_conditions',NULL),
 ('http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#cat_2811',25,'http://omv.ontoware.org/2005/05/ontology#name','','\0',0,3,'Gross Anatomy',NULL);
/*!40000 ALTER TABLE `metadata` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
