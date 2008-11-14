USE `bioportal`;

ALTER TABLE ncbo_ontology_version_metadata MODIFY
	COLUMN `synonym_slot` varchar(512) character set utf8 collate utf8_bin default NULL;

ALTER TABLE ncbo_ontology_version_metadata MODIFY
	COLUMN `preferred_name_slot` varchar(512) character set utf8 collate utf8_bin default NULL;

ALTER TABLE ncbo_ontology_version_metadata ADD
	COLUMN `description` varchar(512) default NULL
	AFTER `display_label`;

ALTER TABLE ncbo_ontology_version_metadata ADD
	COLUMN `abbreviation` varchar(32) default NULL
	AFTER `description`;

ALTER TABLE ncbo_ontology_version_metadata AUTO_INCREMENT = 1000;

CREATE OR REPLACE VIEW `v_ncbo_ontology` AS
SELECT 
	ov.id,
	ov.ontology_id,				
	ov.user_id,
	ov.internal_version_number,
	ov.version_number,
	ov.version_status,
	ov.file_path,
	ov.is_remote,
	ov.is_reviewed,
	ov.status_id,
	ov.date_created,
	ov.date_released,
	o.obo_foundry_id,
	o.is_manual,
	ovm.display_label,
	ovm.description,
	ovm.abbreviation,
	ovm.format,
	ovm.contact_name,
	ovm.contact_email,
	ovm.homepage,
	ovm.documentation,
	ovm.publication,
	ovm.urn,
	ovm.coding_scheme,
	ovm.target_terminologies,
	ovm.is_foundry,
	ovm.synonym_slot,
	ovm.preferred_name_slot
FROM
	ncbo_ontology o 
	INNER JOIN ncbo_ontology_version ov on o.id = ov.ontology_id
	INNER JOIN ncbo_ontology_version_metadata ovm ON ov.id = ovm.ontology_version_id;
