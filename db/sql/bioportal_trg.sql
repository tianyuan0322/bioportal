DELIMITER $$

DROP TRIGGER IF EXISTS `bioportal`.`trg_create_ncbo_app_text_revision_after_update`$$

CREATE DEFINER=`root`@`localhost` TRIGGER `trg_create_ncbo_app_text_revision_after_update` AFTER UPDATE ON ncbo_app_text FOR EACH ROW
BEGIN
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
END$$

DELIMITER ;

DELIMITER $$

DROP TRIGGER IF EXISTS `bioportal`.`trg_create_ncbo_app_text_revision_after_insert`$$

CREATE DEFINER=`root`@`localhost` TRIGGER `trg_create_ncbo_app_text_revision_after_insert` AFTER INSERT ON ncbo_app_text FOR EACH ROW
BEGIN
	INSERT INTO ncbo_app_text_revision (
		identifier, description, text_content, datatype_code, date_revised)
	SELECT NEW.identifier, NEW.description, NEW.text_content, NEW.datatype_code, CURRENT_TIMESTAMP;
END$$

DELIMITER ;