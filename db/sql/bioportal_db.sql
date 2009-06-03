-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.81-community-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema bioportal
--

CREATE DATABASE IF NOT EXISTS bioportal;
USE bioportal;

--
-- Definition of table `ncbo_admin_application`
--

DROP TABLE IF EXISTS `ncbo_admin_application`;
CREATE TABLE `ncbo_admin_application` (
  `id` int(11) NOT NULL,
  `application_id` varchar(64) NOT NULL,
  `application_name` varchar(64) NOT NULL,
  `application_description` varchar(512) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `application_id` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ncbo_admin_application`
--

/*!40000 ALTER TABLE `ncbo_admin_application` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_admin_application` ENABLE KEYS */;


--
-- Definition of table `ncbo_app_text`
--

DROP TABLE IF EXISTS `ncbo_app_text`;
CREATE TABLE `ncbo_app_text` (
  `identifier` varchar(128) NOT NULL,
  `description` varchar(512) default NULL,
  `text_content` text,
  `datatype_code` char(3) NOT NULL default 'LTX',
  `last_modifier` varchar(128) default NULL,
  `date_created` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `date_updated` datetime NOT NULL,
  PRIMARY KEY  (`identifier`),
  KEY `FK_ncbo_app_text_datatype` (`datatype_code`),
  CONSTRAINT `FK_ncbo_app_text_datatype` FOREIGN KEY (`datatype_code`) REFERENCES `ncbo_l_app_text_datatype` (`datatype_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ncbo_app_text`
--

/*!40000 ALTER TABLE `ncbo_app_text` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_app_text` ENABLE KEYS */;


--
-- Definition of trigger `trg_create_ncbo_app_text_revision_after_insert`
--

DROP TRIGGER /*!50030 IF EXISTS */ `trg_create_ncbo_app_text_revision_after_insert`;

DELIMITER $$

CREATE DEFINER = `root`@`localhost` TRIGGER `trg_create_ncbo_app_text_revision_after_insert` AFTER INSERT ON `ncbo_app_text` FOR EACH ROW BEGIN
	INSERT INTO ncbo_app_text_revision (
		identifier, description, text_content, datatype_code, date_revised)
	SELECT NEW.identifier, NEW.description, NEW.text_content, NEW.datatype_code, CURRENT_TIMESTAMP;
END $$

DELIMITER ;

--
-- Definition of trigger `trg_create_ncbo_app_text_revision_after_update`
--

DROP TRIGGER /*!50030 IF EXISTS */ `trg_create_ncbo_app_text_revision_after_update`;

DELIMITER $$

CREATE DEFINER = `root`@`localhost` TRIGGER `trg_create_ncbo_app_text_revision_after_update` AFTER UPDATE ON `ncbo_app_text` FOR EACH ROW BEGIN
	DECLARE strNewTextIdent VARCHAR(128);
	DECLARE strOldTextIdent VARCHAR(128);	
	SELECT LOWER(NEW.identifier) INTO strNewTextIdent;
	SELECT LOWER(OLD.identifier) INTO strOldTextIdent;
	IF strNewTextIdent != strOldTextIdent THEN
		UPDATE ncbo_app_text_revision
		SET identifier = strNewTextIdent
		WHERE LOWER(identifier) = strOldTextIdent;
	END IF;
	INSERT INTO ncbo_app_text_revision (
		identifier, description, text_content, datatype_code, date_revised)
	SELECT strNewTextIdent, NEW.description, NEW.text_content, NEW.datatype_code, CURRENT_TIMESTAMP;
END $$

DELIMITER ;

--
-- Definition of table `ncbo_app_text_revision`
--

DROP TABLE IF EXISTS `ncbo_app_text_revision`;
CREATE TABLE `ncbo_app_text_revision` (
  `identifier` varchar(128) NOT NULL,
  `description` varchar(512) default NULL,
  `text_content` text,
  `datatype_code` char(3) NOT NULL,
  `date_revised` timestamp NOT NULL default CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ncbo_app_text_revision`
--

/*!40000 ALTER TABLE `ncbo_app_text_revision` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_app_text_revision` ENABLE KEYS */;


--
-- Definition of table `ncbo_l_app_text_datatype`
--

DROP TABLE IF EXISTS `ncbo_l_app_text_datatype`;
CREATE TABLE `ncbo_l_app_text_datatype` (
  `datatype_code` char(3) NOT NULL default '',
  `datatype` varchar(48) NOT NULL,
  PRIMARY KEY  (`datatype_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ncbo_l_app_text_datatype`
--

/*!40000 ALTER TABLE `ncbo_l_app_text_datatype` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_l_app_text_datatype` ENABLE KEYS */;


--
-- Definition of table `ncbo_l_role`
--

DROP TABLE IF EXISTS `ncbo_l_role`;
CREATE TABLE `ncbo_l_role` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` varchar(256) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ncbo_l_role`
--

/*!40000 ALTER TABLE `ncbo_l_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_l_role` ENABLE KEYS */;


--
-- Definition of table `ncbo_l_status`
--

DROP TABLE IF EXISTS `ncbo_l_status`;
CREATE TABLE `ncbo_l_status` (
  `id` int(11) NOT NULL,
  `status` varchar(32) NOT NULL,
  `description` varchar(256) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ncbo_l_status`
--

/*!40000 ALTER TABLE `ncbo_l_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_l_status` ENABLE KEYS */;


--
-- Definition of table `ncbo_ontology_file`
--

DROP TABLE IF EXISTS `ncbo_ontology_file`;
CREATE TABLE `ncbo_ontology_file` (
  `id` int(11) NOT NULL auto_increment,
  `ontology_version_id` int(11) NOT NULL,
  `filename` varchar(128) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ind_ontology_id_filename` (`ontology_version_id`,`filename`),
  KEY `ontology_id_2` (`ontology_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `ncbo_ontology_file`
--

/*!40000 ALTER TABLE `ncbo_ontology_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_ontology_file` ENABLE KEYS */;


--
-- Definition of table `ncbo_ontology_load_queue`
--

DROP TABLE IF EXISTS `ncbo_ontology_load_queue`;
CREATE TABLE `ncbo_ontology_load_queue` (
  `id` int(11) NOT NULL auto_increment,
  `ontology_version_id` int(11) NOT NULL,
  `status_id` int(11) NOT NULL,
  `error_message` varchar(1024) default NULL,
  `date_created` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `date_processed` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_ncbo_ontology_load_queue_ontology_version_id` (`ontology_version_id`),
  KEY `FK_ncbo_ontology_load_queue_status_id` (`status_id`),
  CONSTRAINT `FK_ncbo_ontology_load_queue_status_id` FOREIGN KEY (`status_id`) REFERENCES `ncbo_l_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `ncbo_ontology_load_queue`
--

/*!40000 ALTER TABLE `ncbo_ontology_load_queue` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_ontology_load_queue` ENABLE KEYS */;


--
-- Definition of table `ncbo_user`
--

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
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_unique_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `ncbo_user`
--

/*!40000 ALTER TABLE `ncbo_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_user` ENABLE KEYS */;


--
-- Definition of table `ncbo_user_role`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `ncbo_user_role`
--

/*!40000 ALTER TABLE `ncbo_user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbo_user_role` ENABLE KEYS */;


--
-- Definition of procedure `sp_insert_app_text_record`
--

DROP PROCEDURE IF EXISTS `sp_insert_app_text_record`;

DELIMITER $$

/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */ $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insert_app_text_record`(
	p_strTextIdent VARCHAR(128),
	p_strDescription VARCHAR(512),
	p_strLastModifier VARCHAR(128),
	p_strDatatypeCode CHAR(3),
	OUT p_strMessage VARCHAR(20),
	p_txtContent TEXT
)
BEGIN
	DECLARE exit handler for sqlexception ROLLBACK; 
	DECLARE exit handler for sqlwarning ROLLBACK;
	SET p_strMessage := "ERROR"; 
	START TRANSACTION;
	
	IF p_strDatatypeCode IS NULL THEN
		INSERT
		INTO 
			ncbo_app_text (
				identifier,
				description,
				last_modifier,
				text_content,
				date_created, 
				date_updated
			) VALUES (
				LOWER(p_strTextIdent),
				p_strDescription,
				p_strLastModifier,
				p_txtContent,
				CURRENT_TIMESTAMP, 
				CURRENT_TIMESTAMP
			);
	ELSE
		INSERT
		INTO 
			ncbo_app_text (
				identifier,
				description,
				last_modifier,
				text_content,
				datatype_code,
				date_created, 
				date_updated
			) VALUES (
				LOWER(p_strTextIdent),
				p_strDescription,
				p_strLastModifier,
				p_txtContent,
				p_strDatatypeCode,
				CURRENT_TIMESTAMP, 
				CURRENT_TIMESTAMP
			);
	END IF;	
	COMMIT;
	SET p_strMessage := "SUCCESS"; 
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `sp_insert_update_app_text_record`
--

DROP PROCEDURE IF EXISTS `sp_insert_update_app_text_record`;

DELIMITER $$

/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */ $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insert_update_app_text_record`(
	p_strOldTextIdent VARCHAR(128),
	p_strNewTextIdent VARCHAR(128),
	p_strDescription VARCHAR(512),
	p_strLastModifier VARCHAR(128),
	p_strDatatypeCode CHAR(3),
	OUT p_strExecType VARCHAR(6),
	OUT p_strMessage VARCHAR(20),
	p_txtContent TEXT
)
BEGIN
	DECLARE count_recs INT; 
	
	IF p_strNewTextIdent IS NULL THEN
		SET p_strNewTextIdent := p_strOldTextIdent;
	END IF;
	SELECT COUNT(*) INTO count_recs FROM ncbo_app_text WHERE LOWER(identifier) = LOWER(p_strOldTextIdent);
	
	IF count_recs > 0 THEN 
		CALL sp_update_app_text_record(
			p_strOldTextIdent,
			p_strNewTextIdent, 
			p_strDescription,
			p_strLastModifier,
			p_strDatatypeCode,
			p_strMessage,
			p_txtContent
		);
		
		SET p_strExecType = "Update";
	ELSE
		CALL sp_insert_app_text_record(
			p_strOldTextIdent,
			p_strDescription, 
			p_strLastModifier,
			p_strDatatypeCode,	
			p_strMessage,
			p_txtContent
		);
		SET p_strExecType = "Insert";
	END IF;	
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `sp_update_app_text_record`
--

DROP PROCEDURE IF EXISTS `sp_update_app_text_record`;

DELIMITER $$

/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */ $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_update_app_text_record`(
	p_strOldTextIdent VARCHAR(128),
	p_strNewTextIdent VARCHAR(128),
	p_strDescription VARCHAR(512),
	p_strLastModifier VARCHAR(128),
	p_strDatatypeCode CHAR(3),
	OUT p_strMessage VARCHAR(20),
	p_txtContent TEXT
)
BEGIN
	DECLARE exit handler for sqlexception ROLLBACK; 
	DECLARE exit handler for sqlwarning ROLLBACK;
	SET p_strMessage := "ERROR"; 
	START TRANSACTION;
	IF p_strDatatypeCode IS NULL THEN
		UPDATE 
			ncbo_app_text 
		SET
			description = p_strDescription,
			last_modifier = p_strLastModifier,
			text_content = p_txtContent,
			date_updated = CURRENT_TIMESTAMP
		WHERE
			LOWER(identifier) = LOWER(p_strOldTextIdent);
	ELSE
		UPDATE 
			ncbo_app_text 
		SET
			description = p_strDescription,
			last_modifier = p_strLastModifier,
			text_content = p_txtContent,
			date_updated = CURRENT_TIMESTAMP,
			datatype_code = p_strDatatypeCode
		WHERE
			LOWER(identifier) = LOWER(p_strOldTextIdent);
	END IF;
	IF LOWER(p_strOldTextIdent) != LOWER(p_strNewTextIdent) THEN
		UPDATE 
			ncbo_app_text
		SET
			identifier = LOWER(p_strNewTextIdent)
		WHERE
			LOWER(identifier) = LOWER(p_strOldTextIdent);
		DELETE 
		FROM 
			ncbo_app_text_revision 
		WHERE 
			date_revised = (
				SELECT date_revised FROM (
					SELECT MAX(date_revised) AS date_revised 
					FROM ncbo_app_text_revision
				) as x
		);
	END IF;
	COMMIT;
	SET p_strMessage := "SUCCESS"; 
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
