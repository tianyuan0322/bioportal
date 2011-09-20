USE `bioportal`;

-- Default emails for notifying ontology submitters about parse success/failure
INSERT INTO `ncbo_app_text` (`identifier`, `description`, `text_content`, `datatype_code`, `last_modifier`, `date_created`, `date_updated`)
VALUES
	('PARSE_ONTOLOGY_NOTIFICATION', NULL, '<TAG_KEYWORD ONTOLOGY_DISPLAY_LABEL> parsing <TAG_KEYWORD PARSE_STATUS>.\n<br><br>\n<TAG_KEYWORD PARSE_MESSAGE>\n<br><br>\nPlease contact <TAG_KEYWORD CONTACT_EMAIL> if you have questions.\n<br><br>\nThank you,<br>\nThe BioPortal Team', 'LTX', NULL, '2011-09-01 15:12:13', '2011-09-01 15:03:01'),
	('PARSE_ONTOLOGY_NOTIFICATION_SUB', NULL, '[BioPortal] <TAG_KEYWORD ONTOLOGY_DISPLAY_LABEL> Parsing <TAG_KEYWORD PARSE_STATUS_UP>', 'LTX', NULL, '2011-09-01 15:12:13', '2011-09-01 15:03:34');

/*Table structure for table `ncbo_user_ontology_license` */

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
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=latin1;
	