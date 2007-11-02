DELIMITER $$

DROP PROCEDURE IF EXISTS `bioportal`.`sp_update_ontology_id`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_update_ontology_id`()
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
	END$$

DELIMITER ;