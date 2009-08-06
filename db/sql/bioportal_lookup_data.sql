/*
SQLyog Enterprise - MySQL GUI v6.1
MySQL - 5.0.45-community-nt-log : Database - bioportal
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

CREATE DATABASE IF NOT EXISTS `bioportal`;

USE `bioportal`;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

/*Data for the table `ncbo_admin_application` */

INSERT  INTO `ncbo_admin_application`(`id`,`application_id`,`application_name`,`application_description`) VALUES (1,'4ea81d74-8960-4525-810b-fa1baab576ff','NcboBioPortal','NCBO Ruby on Rails based BioPortal GUI implementation');
INSERT  INTO `ncbo_admin_application`(`id`,`application_id`,`application_name`,`application_description`) VALUES (2,'6a7c5586-5744-2041-818e-e3bc21db91b2','BioPortalAdmin','BioPortal administration application');

/*Data for the table `ncbo_l_additional_metadata` */

/*Data for the table `ncbo_l_app_text_datatype` */

INSERT  INTO `ncbo_l_app_text_datatype`(`datatype_code`,`datatype`) VALUES ('DAT','Date'),('LTX','Long Text'),('NUM','Numeric'),('RTX','Rich Text'),('TXT','Short Text (one line)');

/*Data for the table `ncbo_l_category` */

-- insert  into `ncbo_l_category`(`id`,`name`,`obo_foundry_name`,`parent_category_id`) values (2801,'Other',NULL,NULL),(2802,'Chemical','chemical',NULL),(2803,'Development','developmental',NULL),(2804,'Ethology','ethology',NULL),(2805,'Experimental Conditions','experimental_conditions',NULL),(2806,'Genomic and Proteomic','genomic-proteomic',NULL),(2807,'Phenotype','phenotype',NULL),(2808,'Taxonomic Classification',NULL,NULL),(2809,'Vocabularies','vocabularies',NULL),(2810,'Anatomy','anatomy',NULL),(2811,'Gross Anatomy','gross_anatomy',2810),(2812,'Animal Gross Anatomy','animal_gross_anatomy',2811),(2813,'Fish Anatomy','fish',2812),(2814,'Human Developmental Anatomy','human',2812),(2815,'Microbial Anatomy','microbial_gross_anatomy',2811),(2816,'Plant Anatomy','plant_gross_anatomy',2811),(2817,'Mouse Anatomy','mouse',2812),(2818,'Animal Development','animal_development',2803),(2819,'Plant Development','plant_development',2803),(2820,'Gene Product',NULL,2806),(2821,'Protein',NULL,2820),(2822,'Physicochemical','physicochemical',NULL),(5057,'Imaging','imaging_methods',NULL);

/*Data for the table `ncbo_l_role` */

INSERT  INTO `ncbo_l_role`(`id`,`name`,`description`) VALUES (2822,'ROLE_DEVELOPER','Can develop ontologies'),(2823,'ROLE_LIBRARIAN','Can validate Ontologies'),(2824,'ROLE_ADMINISTRATOR','Has administration priveleges');

/*Data for the table `ncbo_l_status` */

INSERT  INTO `ncbo_l_status`(`id`,`status`,`description`) VALUES (1,'Waiting','The action has not been taken'),(2,'Parsing','The action is in progress'),(3,'Ready','The action has completed successfully'),(4,'Error','The action has encountered an error while executing'),(5,'Not Applicable','The action does not apply to this record');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
