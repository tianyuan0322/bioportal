/*
SQLyog Enterprise - MySQL GUI v6.1
MySQL - 5.0.45-community-nt : Database - bioportal
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

create database if not exists `bioportal`;

USE `bioportal`;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

/*Table structure for table `ncbo_l_additional_metadata` */

DROP TABLE IF EXISTS `ncbo_l_additional_metadata`;

CREATE TABLE `ncbo_l_additional_metadata` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `ncbo_l_category` */

DROP TABLE IF EXISTS `ncbo_l_category`;

CREATE TABLE `ncbo_l_category` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `parent_category_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `parent_category_id` (`parent_category_id`),
  CONSTRAINT `FK_ncbo_l_category_ncbo_l_category_new` FOREIGN KEY (`parent_category_id`) REFERENCES `ncbo_l_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `ncbo_l_role` */

DROP TABLE IF EXISTS `ncbo_l_role`;

CREATE TABLE `ncbo_l_role` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` varchar(256) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `ncbo_ontology_additional_metadata` */

DROP TABLE IF EXISTS `ncbo_ontology_additional_metadata`;

CREATE TABLE `ncbo_ontology_additional_metadata` (
  `id` int(11) NOT NULL auto_increment,
  `ontology_version_id` int(11) NOT NULL,
  `additional_metadata_id` int(11) NOT NULL,
  `additional_metadata_value` varchar(2048) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IND_ontology_id_additional_metadata_id` (`ontology_version_id`,`additional_metadata_id`),
  KEY `additional_metadata_id` (`additional_metadata_id`),
  KEY `ontology_id` (`ontology_version_id`),
  CONSTRAINT `FK_ncbo_ontology_additional_metadata_ncbo_ontology_version` FOREIGN KEY (`ontology_version_id`) REFERENCES `ncbo_ontology_version` (`id`),
  CONSTRAINT `FK_ncbo_ontology_addl_metadata_ncbo_l_addl_metadata_new` FOREIGN KEY (`additional_metadata_id`) REFERENCES `ncbo_l_additional_metadata` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `ncbo_ontology_category` */

DROP TABLE IF EXISTS `ncbo_ontology_category`;

CREATE TABLE `ncbo_ontology_category` (
  `id` int(11) NOT NULL auto_increment,
  `ontology_version_d` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IND_ontology_id_category_id` (`ontology_version_d`,`category_id`),
  KEY `category_id` (`category_id`),
  KEY `ontology_id` (`ontology_version_d`),
  CONSTRAINT `FK_ncbo_ontology_category_ncbo_ontology_version` FOREIGN KEY (`ontology_version_d`) REFERENCES `ncbo_ontology_version` (`id`),
  CONSTRAINT `FK_ncbo_ontology_category_ncbo_l_category_new` FOREIGN KEY (`category_id`) REFERENCES `ncbo_l_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15821 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `ncbo_ontology_file` */

DROP TABLE IF EXISTS `ncbo_ontology_file`;

CREATE TABLE `ncbo_ontology_file` (
  `id` int(11) NOT NULL auto_increment,
  `ontology_version_id` int(11) NOT NULL,
  `filename` varchar(128) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ind_ontology_id_filename` (`ontology_version_id`,`filename`),
  KEY `ontology_id_2` (`ontology_version_id`),
  CONSTRAINT `FK_ncbo_ontology_file_ncbo_ontology_version` FOREIGN KEY (`ontology_version_id`) REFERENCES `ncbo_ontology_version` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15818 DEFAULT CHARSET=latin1;

/*Table structure for table `ncbo_ontology_metadata` */

DROP TABLE IF EXISTS `ncbo_ontology_metadata`;

CREATE TABLE `ncbo_ontology_metadata` (
  `id` int(11) NOT NULL auto_increment,
  `ontology_version_id` int(11) NOT NULL,
  `display_label` varchar(128) NOT NULL,
  `format` varchar(50) NOT NULL,
  `contact_name` varchar(128) default NULL,
  `contact_email` varchar(128) default NULL,
  `homepage` varchar(2048) default NULL,
  `documentation` varchar(2048) default NULL,
  `publication` varchar(2048) default NULL,
  `urn` varchar(512) default NULL,
  `is_foundry` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `ontology_id` (`ontology_version_id`),
  CONSTRAINT `FK_ncbo_ontology_metadata_ncbo_ontology_version` FOREIGN KEY (`ontology_version_id`) REFERENCES `ncbo_ontology_version` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=172 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `ncbo_ontology_version` */

DROP TABLE IF EXISTS `ncbo_ontology_version`;

CREATE TABLE `ncbo_ontology_version` (
  `id` int(11) NOT NULL auto_increment,
  `ontology_id` int(11) NOT NULL,
  `parent_id` int(11) default NULL,
  `user_id` int(11) NOT NULL,
  `internal_version_number` int(11) NOT NULL,
  `version_number` varchar(64) NOT NULL,
  `version_status` varchar(64) default NULL,
  `file_path` varchar(2048) default NULL,
  `is_current` tinyint(1) NOT NULL,
  `is_remote` tinyint(1) NOT NULL,
  `is_reviewed` tinyint(1) NOT NULL default '0',
  `date_released` datetime NOT NULL,
  `date_created` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  KEY `parent_id` (`parent_id`),
  KEY `ncbo_ontology_fk_new` (`user_id`),
  CONSTRAINT `FK_ncbo_ontology_ncbo_ontology_new` FOREIGN KEY (`parent_id`) REFERENCES `ncbo_ontology_version` (`id`),
  CONSTRAINT `FK_ncbo_ontology_ncbo_user` FOREIGN KEY (`user_id`) REFERENCES `ncbo_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15817 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Table structure for table `ncbo_seq_ontology_id` */

DROP TABLE IF EXISTS `ncbo_seq_ontology_id`;

CREATE TABLE `ncbo_seq_ontology_id` (
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `ncbo_user` */

DROP TABLE IF EXISTS `ncbo_user`;

CREATE TABLE `ncbo_user` (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(64) NOT NULL,
  `password` varchar(128) NOT NULL,
  `email` varchar(128) NOT NULL,
  `firstname` varchar(64) NOT NULL,
  `lastname` varchar(64) NOT NULL,
  `phone` varchar(32) default NULL,
  `date_created` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15481 DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 3072 kB';

/*Table structure for table `ncbo_user_role` */

DROP TABLE IF EXISTS `ncbo_user_role`;

CREATE TABLE `ncbo_user_role` (
  `id` int(11) NOT NULL auto_increment,
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `user_id` (`user_id`,`role_id`),
  KEY `ncbo_user_role_fk1_new` (`role_id`),
  CONSTRAINT `ncbo_user_role_fk1_new` FOREIGN KEY (`role_id`) REFERENCES `ncbo_l_role` (`id`),
  CONSTRAINT `ncbo_user_role_fk_new` FOREIGN KEY (`user_id`) REFERENCES `ncbo_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15482 DEFAULT CHARSET=latin1;

/* Procedure structure for procedure `sp_update_ontology_id` */

/*!50003 DROP PROCEDURE IF EXISTS  `sp_update_ontology_id` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_update_ontology_id`()
BEGIN
		DECLARE dl VARCHAR(128);
		DECLARE done INT DEFAULT 0;
		DECLARE cur1 CURSOR FOR select distinct display_label from ncbo_ontology_metadata order by display_label;
		OPEN cur1;
  
		REPEAT
			FETCH cur1 INTO dl;
			IF NOT done THEN
				update ncbo_seq_ontology_id set id = last_insert_id(id + 1);
				update ncbo_ontology_version set ontology_id = last_insert_id() where id in (select ontology_version_id from ncbo_ontology_metadata where display_label = dl);
			END IF;
		UNTIL done END REPEAT;
		CLOSE cur1;
	END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
