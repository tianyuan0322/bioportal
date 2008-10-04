DELIMITER $$

DROP PROCEDURE IF EXISTS `sp_remove_ontology_by_display_label`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_remove_ontology_by_display_label`(
	p_strDisplayLabel VARCHAR(128)
)
BEGIN
		DECLARE done INT DEFAULT 0;
		DECLARE ontologyVersionId INT;
		DECLARE ontologyId INT DEFAULT -1;
		DECLARE iterCount INT DEFAULT 1;
		DECLARE cur CURSOR FOR SELECT ontology_version_id FROM ncbo_ontology_version_metadata WHERE LOWER(display_label) = LOWER(p_strDisplayLabel);
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

		OPEN cur;

		REPEAT
			FETCH cur INTO ontologyVersionId;

			IF iterCount = 1 THEN
				SELECT ontology_id INTO ontologyId FROM ncbo_ontology_version WHERE id = ontologyVersionId;
			END IF;

			IF NOT done THEN
				DELETE FROM ncbo_ontology_category WHERE ontology_version_id = ontologyVersionId;
				DELETE FROM ncbo_ontology_file WHERE ontology_version_id = ontologyVersionId;
				DELETE FROM ncbo_ontology_load_queue WHERE ontology_version_id = ontologyVersionId;
				DELETE FROM ncbo_ontology_version_metadata WHERE ontology_version_id = ontologyVersionId;
				DELETE FROM ncbo_ontology_version WHERE ontology_id = ontologyId;
   			END IF;
   			
   			SET iterCount := iterCount + 1;
		UNTIL done END REPEAT;
		
		CLOSE cur;
		
		DELETE FROM ncbo_ontology WHERE id = ontologyId;

	END$$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS `bioportal`.`sp_update_app_text_record`$$

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
END$$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS `bioportal`.`sp_insert_app_text_record`$$

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
END$$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS `bioportal`.`sp_insert_update_app_text_record`$$

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
END$$

DELIMITER ;
