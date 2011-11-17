# ************************************************************
# Generation Time: 2011-11-17 12:24:22 -0800
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`bioportal` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `bioportal`;


# Dump of table ncbo_l_app_text_datatype
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_l_app_text_datatype`;

CREATE TABLE `ncbo_l_app_text_datatype` (
  `datatype_code` char(3) NOT NULL DEFAULT '',
  `datatype` varchar(48) NOT NULL,
  PRIMARY KEY (`datatype_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `ncbo_l_app_text_datatype` WRITE;
/*!40000 ALTER TABLE `ncbo_l_app_text_datatype` DISABLE KEYS */;

INSERT INTO `ncbo_l_app_text_datatype` (`datatype_code`, `datatype`)
VALUES
	('DAT','Date'),
	('LTX','Long Text'),
	('NUM','Numeric'),
	('RTX','Rich Text'),
	('TXT','Short Text (one line)');

/*!40000 ALTER TABLE `ncbo_l_app_text_datatype` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table ncbo_l_notification_type
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_l_notification_type`;

CREATE TABLE `ncbo_l_notification_type` (
  `id` int(11) NOT NULL,
  `type` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

LOCK TABLES `ncbo_l_notification_type` WRITE;
/*!40000 ALTER TABLE `ncbo_l_notification_type` DISABLE KEYS */;

INSERT INTO `ncbo_l_notification_type` (`id`, `type`)
VALUES
	(3,'ALL_NOTIFICATION'),
	(10,'CREATE_NOTE_NOTIFICATION');

/*!40000 ALTER TABLE `ncbo_l_notification_type` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table ncbo_l_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_l_role`;

CREATE TABLE `ncbo_l_role` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `ncbo_l_role` WRITE;
/*!40000 ALTER TABLE `ncbo_l_role` DISABLE KEYS */;

INSERT INTO `ncbo_l_role` (`id`, `name`, `description`)
VALUES
	(2822,'ROLE_DEVELOPER','Can develop ontologies'),
	(2823,'ROLE_LIBRARIAN','Can validate Ontologies'),
	(2824,'ROLE_ADMINISTRATOR','Has administration priveleges');

/*!40000 ALTER TABLE `ncbo_l_role` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table ncbo_l_status
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_l_status`;

CREATE TABLE `ncbo_l_status` (
  `id` int(11) NOT NULL,
  `status` varchar(32) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `ncbo_l_status` WRITE;
/*!40000 ALTER TABLE `ncbo_l_status` DISABLE KEYS */;

INSERT INTO `ncbo_l_status` (`id`, `status`, `description`)
VALUES
	(1,'Waiting','The action has not been taken'),
	(2,'Parsing','The action is in progress'),
	(3,'Ready','The action has completed successfully'),
	(4,'Error','The action has encountered an error while executing'),
	(5,'Not Applicable','The action does not apply to this record');

/*!40000 ALTER TABLE `ncbo_l_status` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table ncbo_l_usage_event_type
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_l_usage_event_type`;

CREATE TABLE `ncbo_l_usage_event_type` (
  `id` int(11) NOT NULL,
  `event_name` varchar(64) NOT NULL,
  `event_description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_event_name_unique` (`event_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `ncbo_l_usage_event_type` WRITE;
/*!40000 ALTER TABLE `ncbo_l_usage_event_type` DISABLE KEYS */;

INSERT INTO `ncbo_l_usage_event_type` (`id`, `event_name`, `event_description`)
VALUES
	(1000,'show_all_ontologies','Show all ontologies'),
	(1001,'show_ontology','Show ontology'),
	(1002,'show_virtual_ontology','Show virtual ontology'),
	(1003,'show_virtual_concept','Show virtual concept'),
	(1004,'visualize_ontology','Visualize Ontology'),
	(1005,'visualize_concept_direct','Visualize Concept Directly'),
	(1006,'visualize_concept_browse','Visualize Concept By Browsing'),
	(1007,'jump_to_search','Jump to Search'),
	(1008,'jump_to_nav','Jump to Navigation'),
	(1009,'tab_details','UI Tab Details'),
	(1010,'tab_visualization','UI Tab Visualization'),
	(1011,'tab_notes','UI Tab Notes'),
	(1012,'tab_mappings','UI Tab Mappings'),
	(1013,'tab_resource_index','UI Tab Resource Index'),
	(1014,'tab_metadata','UI Tab Metadata'),
	(1015,'tab_views','UI Tab Views'),
	(1016,'tab_projects','UI Tab Projects'),
	(1017,'tab_reviews','UI Tab Reviews'),
	(1018,'tab_metrics','UI Tab Metrics'),
	(1019,'tab_widgets','UI Tab Widgets');

/*!40000 ALTER TABLE `ncbo_l_usage_event_type` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
