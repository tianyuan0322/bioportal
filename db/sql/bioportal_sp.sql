DELIMITER $$

DROP PROCEDURE IF EXISTS `bioportal`.`sp_update_app_text_record`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_update_app_text_record`(
	p_strOldTextIdent VARCHAR(128),
	p_strNewTextIdent VARCHAR(128),
	p_strDescription VARCHAR(512),
	p_strLastModifier VARCHAR(128),
	p_strDatatypeCode CHAR(3),
	p_txtContent TEXT,
	OUT message VARCHAR(20)
)
BEGIN
	DECLARE exit handler for sqlexception ROLLBACK; 
	DECLARE exit handler for sqlwarning ROLLBACK;
	SET message := "ERROR"; 

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
				SELECT 
					MAX(date_revised) 
				FROM 
					ncbo_app_text_revision
			);
	END IF;

	COMMIT;
	SET message := "SUCCESS"; 
END$$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS `bioportal`.`sp_insert_app_text_record`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insert_app_text_record`(
	p_strTextIdent VARCHAR(128),
	p_strDescription VARCHAR(512),
	p_strLastModifier VARCHAR(128),
	p_strDatatypeCode CHAR(3),
	p_txtContent TEXT,
	OUT message VARCHAR(20)
)
BEGIN
	DECLARE exit handler for sqlexception ROLLBACK; 
	DECLARE exit handler for sqlwarning ROLLBACK;
	SET message := "ERROR"; 

	START TRANSACTION;
	
	IF p_strDatatypeCode IS NULL THEN
		INSERT
		INTO 
			tsn_app_text (
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
			tsn_app_text (
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
	SET message := "SUCCESS"; 
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
	p_txtContent TEXT,
	OUT p_strExecType VARCHAR(6),
	OUT message VARCHAR(20)
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
			p_txtContent,
			message
		);
		
		SET p_strExecType = "Update";
	ELSE
		CALL sp_insert_app_text_record(
			p_strOldTextIdent,
			p_strDescription, 
			p_strLastModifier,
			p_strDatatypeCode,	
			p_txtContent,
			message
		);

		SET p_strExecType = "Insert";
	END IF;	
END$$

DELIMITER ;
