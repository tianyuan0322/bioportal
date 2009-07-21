/*
SQLyog Enterprise - MySQL GUI v8.05 
MySQL - 5.0.81-community-nt : Database - bioportal
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`bioportal` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `bioportal`;

/*Table structure for table `ncbo_usage_log` */

DROP TABLE IF EXISTS `ncbo_usage_log`;

CREATE TABLE `ncbo_usage_log` (
  `id` int(11) NOT NULL auto_increment,
  `hash_code` int(11) NOT NULL,
  `application_id` varchar(64) default NULL,
  `request_url` varchar(128) default NULL,
  `http_method` varchar(8) default NULL,
  `resource_parameters` varchar(512) default NULL,
  `request_parameters` varchar(512) default NULL,
  `user_id` int(11) default NULL,
  `hit_count` int(11) NOT NULL default '0',
  `date_accessed` date NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_hash_code_unique` (`hash_code`),
  KEY `idx_request_url` (`request_url`),
  KEY `idx_application_id` (`application_id`),
  KEY `idx_request_parameters` (`request_parameters`),
  KEY `idx_resource_parameters` (`resource_parameters`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
