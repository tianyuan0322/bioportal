# ************************************************************
# Generation Time: 2011-11-17 12:16:04 -0800
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`bioportal_notes` /*!40100 DEFAULT CHARACTER SET latin1 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`bioportal_lexgrid` /*!40100 DEFAULT CHARACTER SET latin1 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`bioportal_protege` /*!40100 DEFAULT CHARACTER SET latin1 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`bioportal` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `bioportal`;


# Dump of table ncbo_admin_application
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_admin_application`;

CREATE TABLE `ncbo_admin_application` (
  `id` int(11) NOT NULL,
  `application_id` varchar(64) NOT NULL,
  `application_name` varchar(64) NOT NULL,
  `application_description` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `application_id` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table ncbo_app_text
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_app_text`;

CREATE TABLE `ncbo_app_text` (
  `identifier` varchar(128) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `text_content` text,
  `datatype_code` char(3) NOT NULL DEFAULT 'LTX',
  `last_modifier` varchar(128) DEFAULT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_updated` datetime NOT NULL,
  PRIMARY KEY (`identifier`),
  KEY `FK_ncbo_app_text_datatype` (`datatype_code`),
  CONSTRAINT `FK_ncbo_app_text_datatype` FOREIGN KEY (`datatype_code`) REFERENCES `ncbo_l_app_text_datatype` (`datatype_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table ncbo_app_text_revision
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_app_text_revision`;

CREATE TABLE `ncbo_app_text_revision` (
  `identifier` varchar(128) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `text_content` text,
  `datatype_code` char(3) NOT NULL,
  `date_revised` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table ncbo_l_app_text_datatype
# ------------------------------------------------------------



# Dump of table ncbo_l_notification_type
# ------------------------------------------------------------



# Dump of table ncbo_l_role
# ------------------------------------------------------------



# Dump of table ncbo_l_status
# ------------------------------------------------------------



# Dump of table ncbo_l_usage_event_type
# ------------------------------------------------------------



# Dump of table ncbo_ontology_acl
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_ontology_acl`;

CREATE TABLE `ncbo_ontology_acl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ontology_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `is_owner` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_ontology_id_user_id_unique` (`ontology_id`,`user_id`),
  KEY `idx_ontology_id` (`ontology_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `FK_ncbo_ontology_acl_user_id` FOREIGN KEY (`user_id`) REFERENCES `ncbo_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;



# Dump of table ncbo_ontology_file
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_ontology_file`;

CREATE TABLE `ncbo_ontology_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ontology_version_id` int(11) NOT NULL,
  `filename` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ind_ontology_id_filename` (`ontology_version_id`,`filename`),
  KEY `ontology_id_2` (`ontology_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;



# Dump of table ncbo_ontology_load_queue
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_ontology_load_queue`;

CREATE TABLE `ncbo_ontology_load_queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ontology_version_id` int(11) NOT NULL,
  `status_id` int(11) NOT NULL,
  `error_message` varchar(1024) DEFAULT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_processed` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ncbo_ontology_load_queue_ontology_version_id` (`ontology_version_id`),
  KEY `FK_ncbo_ontology_load_queue_status_id` (`status_id`),
  CONSTRAINT `FK_ncbo_ontology_load_queue_status_id` FOREIGN KEY (`status_id`) REFERENCES `ncbo_l_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;



# Dump of table ncbo_usage_log
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_usage_log`;

CREATE TABLE `ncbo_usage_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_id` varchar(64) DEFAULT NULL,
  `event_type` int(11) NOT NULL,
  `request_url` varchar(256) DEFAULT NULL,
  `http_method` varchar(8) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `session_id` varchar(256) DEFAULT NULL,
  `ip_address` varchar(32) DEFAULT NULL,
  `ontology_version_id` int(11) DEFAULT NULL,
  `ontology_id` int(11) DEFAULT NULL,
  `ontology_name` varchar(128) DEFAULT NULL,
  `concept_id` varchar(512) DEFAULT NULL,
  `concept_name` varchar(512) DEFAULT NULL,
  `search_query` varchar(128) DEFAULT NULL,
  `search_parameters` varchar(64) DEFAULT NULL,
  `num_search_results` int(11) DEFAULT NULL,
  `date_accessed` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_application_id` (`application_id`),
  KEY `idx_request_url` (`request_url`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_ip_address` (`ip_address`),
  KEY `idx_ontology_name` (`ontology_name`),
  KEY `idx_concept_id` (`concept_id`),
  KEY `idx_concept_name` (`concept_name`),
  KEY `idx_search_query` (`search_query`),
  KEY `idx_search_parameters` (`search_parameters`),
  KEY `FK_ncbo_usage_log_event_type` (`event_type`),
  CONSTRAINT `FK_ncbo_usage_log_event_type` FOREIGN KEY (`event_type`) REFERENCES `ncbo_l_usage_event_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table ncbo_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_user`;

CREATE TABLE `ncbo_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `api_key` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'UUID()',
  `open_id` varchar(256) DEFAULT NULL,
  `password` varchar(128) NOT NULL,
  `email` varchar(128) NOT NULL,
  `firstname` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `lastname` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_username` (`username`),
  UNIQUE KEY `idx_api_key_unique` (`api_key`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;



# Dump of table ncbo_user_ontology_license
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_user_ontology_license`;

CREATE TABLE `ncbo_user_ontology_license` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `ontology_id` int(11) NOT NULL,
  `license_text` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id_ontology_id_unique` (`user_id`,`ontology_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_ontology_id` (`ontology_id`),
  CONSTRAINT `FK_ncbo_user_ontology_license_user_id` FOREIGN KEY (`user_id`) REFERENCES `ncbo_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table ncbo_user_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_user_role`;

CREATE TABLE `ncbo_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`role_id`),
  KEY `ncbo_user_role_fk1_new` (`role_id`),
  CONSTRAINT `ncbo_user_role_fk1_new` FOREIGN KEY (`role_id`) REFERENCES `ncbo_l_role` (`id`),
  CONSTRAINT `ncbo_user_role_fk_new` FOREIGN KEY (`user_id`) REFERENCES `ncbo_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;



# Dump of table ncbo_user_subscriptions
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ncbo_user_subscriptions`;

CREATE TABLE `ncbo_user_subscriptions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `ontology_id` varchar(11) NOT NULL,
  `notification_type` int(11) NOT NULL DEFAULT '3',
  PRIMARY KEY (`id`),
  KEY `FK_ncbo_notification_type` (`notification_type`),
  CONSTRAINT `FK_ncbo_notification_type` FOREIGN KEY (`notification_type`) REFERENCES `ncbo_l_notification_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
